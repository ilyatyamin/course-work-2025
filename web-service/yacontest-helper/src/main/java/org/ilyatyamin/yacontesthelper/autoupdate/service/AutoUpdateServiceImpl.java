package org.ilyatyamin.yacontesthelper.autoupdate.service;

import com.google.api.services.sheets.v4.Sheets;
import lombok.AllArgsConstructor;
import org.ilyatyamin.yacontesthelper.autoupdate.dao.TaskStatus;
import org.ilyatyamin.yacontesthelper.autoupdate.dao.UpdateTaskDao;
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest;
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateResponse;
import org.ilyatyamin.yacontesthelper.autoupdate.repository.UpdateTaskRepository;
import org.ilyatyamin.yacontesthelper.configs.ExceptionMessages;
import org.ilyatyamin.yacontesthelper.error.YaContestException;
import org.ilyatyamin.yacontesthelper.grades.service.sheets.GoogleSheetsService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
@AllArgsConstructor
public class AutoUpdateServiceImpl implements AutoUpdateService {
    private GoogleSheetsService googleSheetsService;
    private SchedulingService schedulingService;
    private UpdateTaskRepository updateTaskRepository;

    private static final AtomicLong taskIdGenerator = new AtomicLong(0);

    public AutoUpdateResponse setOnAutoUpdate(final AutoUpdateRequest autoUpdateRequest) {
        if (!checkIfWritingInSheetPossible(autoUpdateRequest.getCredentialsGoogle(),
                autoUpdateRequest.getSpreadsheetUrl())) {
            throw new YaContestException(HttpStatus.NOT_FOUND.value(),
                    ExceptionMessages.GOOGLE_SHEETS_NOT_FOUND.getMessage());
        }

        Long taskId = taskIdGenerator.incrementAndGet();
        schedulingService.putTaskOnScheduling(taskId, autoUpdateRequest);

        // TODO: add ownerId when registration will be completed
        UpdateTaskDao dao = updateTaskRepository.save(
                new UpdateTaskDao(null, null, taskId,
                        autoUpdateRequest.getCronExpression(),
                        autoUpdateRequest.getSpreadsheetUrl(),
                        autoUpdateRequest.getCredentialsGoogle(),
                        TaskStatus.ACTIVE)
        );
        return new AutoUpdateResponse(dao.getId());
    }

    private Boolean checkIfWritingInSheetPossible(String googleCredentials, String spreadsheetUrl) {
        try {
            Sheets userSheets = googleSheetsService.getSpreadsheets(googleCredentials);
            return googleSheetsService.isSpreadSheetExists(userSheets, spreadsheetUrl);
        } catch (Exception e) {
            return false;
        }
    }
}
