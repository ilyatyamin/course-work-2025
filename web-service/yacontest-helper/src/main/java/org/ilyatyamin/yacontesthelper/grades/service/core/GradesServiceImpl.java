package org.ilyatyamin.yacontesthelper.grades.service.core;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilyatyamin.yacontesthelper.configs.ExceptionMessages;
import org.ilyatyamin.yacontesthelper.grades.dao.GradesResult;
import org.ilyatyamin.yacontesthelper.grades.dto.GoogleSheetsRequest;
import org.ilyatyamin.yacontesthelper.grades.dto.GradesRequest;
import org.ilyatyamin.yacontesthelper.grades.dto.GradesResponse;
import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestSubmission;
import org.ilyatyamin.yacontesthelper.exceptions.YaContestException;
import org.ilyatyamin.yacontesthelper.grades.repository.GradesResultRepository;
import org.ilyatyamin.yacontesthelper.grades.service.processor.SubmissionProcessorService;
import org.ilyatyamin.yacontesthelper.grades.service.excel.ExcelFormatterServiceImpl;
import org.ilyatyamin.yacontesthelper.grades.service.sheets.GoogleSheetsService;
import org.ilyatyamin.yacontesthelper.grades.service.yacontest.YaContestService;
import org.ilyatyamin.yacontesthelper.utils.UtilsService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class GradesServiceImpl implements GradesService {
    private GradesResultRepository gradesResultRepository;

    private YaContestService yaContestService;
    private SubmissionProcessorService submissionProcessorService;
    private ExcelFormatterServiceImpl excelFormatterService;
    private GoogleSheetsService googleSheetsService;
    private UtilsService utilsService;

    @Override
    public GradesResponse getGradesList(GradesRequest gradesRequest) {
        List<String> problemList = yaContestService.getListOfProblems(gradesRequest.contestId(), gradesRequest.yandexKey());
        log.info("Got problem list for request: {}", problemList);

        List<ContestSubmission> submissionList = yaContestService.getSubmissionList(gradesRequest.contestId(), gradesRequest.yandexKey());
        log.info("Got {} submissions for request", submissionList.size());

        var resultTable = submissionProcessorService.processSubmissionList(
                submissionList,
                problemList,
                gradesRequest.participants(),
                utilsService.processLocalDateTime(gradesRequest.deadline())
        );

        // TODO: после регистрации допилить userId
        GradesResult result = gradesResultRepository.saveAndFlush(
                new GradesResult(null, resultTable)
        );

        return new GradesResponse(result.getId(), resultTable);
    }

    @Override
    public byte[] generateGradesTable(Long tableId) {
        // TODO: проверка что таблица с таким tableId доступна вообще данному пользователю
        if (!gradesResultRepository.existsById(tableId)) {
            throw new YaContestException(HttpStatus.NOT_FOUND.value(), ExceptionMessages.GRADES_TABLE_NOT_FOUND.getMessage());
        }
        GradesResult result = gradesResultRepository.getReferenceById(tableId);

        return excelFormatterService.generateGradesTable(result.getPayload());
    }

    @Override
    public void writeToGoogleSheets(Long tableId, GoogleSheetsRequest request) {
        // TODO: проверка что таблица с таким tableId доступна вообще данному пользователю
        if (!gradesResultRepository.existsById(tableId)) {
            throw new YaContestException(HttpStatus.NOT_FOUND.value(), ExceptionMessages.GRADES_TABLE_NOT_FOUND.getMessage());
        }
        GradesResult result = gradesResultRepository.getReferenceById(tableId);

        googleSheetsService.writeToSheet(
                request.googleServiceAccountCredentials(),
                request.spreadsheetUrl(),
                request.listName(),
                result.getPayload()
        );
    }
}
