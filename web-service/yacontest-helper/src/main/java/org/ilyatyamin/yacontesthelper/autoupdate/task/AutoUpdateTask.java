package org.ilyatyamin.yacontesthelper.autoupdate.task;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest;
import org.ilyatyamin.yacontesthelper.grades.dto.GradesRequest;
import org.ilyatyamin.yacontesthelper.grades.enums.RequestType;
import org.ilyatyamin.yacontesthelper.grades.service.core.GradesService;
import org.ilyatyamin.yacontesthelper.grades.service.sheets.GoogleSheetsService;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
public class AutoUpdateTask implements Runnable {
    private GoogleSheetsService googleSheetsService;
    private GradesService gradesService;

    private final Long taskId;
    private final AutoUpdateRequest info;

    @Override
    public void run() {
        var result = gradesService.getGradesList(
                new GradesRequest(info.getContestId(), info.getParticipants(),
                        info.getDeadline(), info.getYandexKey()),
                RequestType.AUTOMATICALLY
        );
        log.info("For auto update task id {} got grades and saved it with id {}", taskId, result.tableId());

        googleSheetsService.writeToSheet(
                info.getCredentialsGoogle(),
                info.getSpreadsheetUrl(),
                info.getSheetName(),
                result.results()
        );
        log.info("Sheet was updated successfully as part of task #{} at {}", taskId, LocalDateTime.now());
    }
}
