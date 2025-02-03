package org.ilyatyamin.yacontesthelper.service.impl;

import lombok.AllArgsConstructor;
import org.ilyatyamin.yacontesthelper.dto.report.ReportRequest;
import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestSubmission;
import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestSubmissionWithCode;
import org.ilyatyamin.yacontesthelper.service.*;
import org.ilyatyamin.yacontesthelper.utils.HeaderLevel;
import org.ilyatyamin.yacontesthelper.utils.MarkdownFormatter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@AllArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {
    private YaContestService yaContestService;
    private ContestFeignClient contestFeignClient;
    private SubmissionProcessorService submissionProcessorService;
    private UtilsService utilsService;

    @Override
    public byte[] createReport(ReportRequest request) {
        List<ContestSubmission> response = yaContestService.getSubmissionList(
                request.getContestId(),
                request.getYandexKey()
        );
        String authHeader = String.format("OAuth %s", request.getYandexKey());

        List<ContestSubmissionWithCode> codeSubmissions = fillSubmissionByCode(response, request.getContestId(), authHeader);

        Map<String, Map<String, Double>> grades = submissionProcessorService.processSubmissionList(
                response,
                yaContestService.getListOfProblems(request.getContestId(), request.getYandexKey()),
                request.getParticipants(),
                utilsService.processLocalDateTime(request.getDeadline())
        );
        Map<String, Double> submissionStatsOk = submissionProcessorService.getOkPercentageForTasks(grades);

        String markdownFormat = getMarkdownReport(request, grades, submissionStatsOk, codeSubmissions);

        return markdownFormat.getBytes();
    }

    @Override
    public String getMarkdownReport(ReportRequest request,
                                    Map<String, Map<String, Double>> totalGrades,
                                    Map<String, Double> submissionStatsOk,
                                    List<ContestSubmissionWithCode> codeSubmissions) {
        MarkdownFormatter format = MarkdownFormatter.create()
                .addHeader("Отчет по посылкам", HeaderLevel.FIRST)
                .addHeader(String.format("Сформирован %s для контеста с id #%s", LocalDateTime.now(), request.getContestId()), HeaderLevel.SECOND)
                .addText(String.format("Крайний срок сдачи: %s", request.getDeadline()))
                .addHeader("Результаты", HeaderLevel.THIRD)
                .addTable(totalGrades)
                .addHeader("Статистика по вердиктам (количество посылок со статусом ОК среди всех посылок данной задачи)", HeaderLevel.THIRD)
                .addOneDimTable(submissionStatsOk)
                .addHeader("Исходные коды посылок со статусом OK: ", HeaderLevel.SECOND);

        for (ContestSubmissionWithCode subm : codeSubmissions) {
            if (subm.getSubmission().getVerdict().equals("OK")) {
                format.addText(String.format("id=%s. Автор=%s. Задача #%s. Вердикт #%s",
                        subm.getSubmission().getId(),
                        subm.getSubmission().getAuthor(),
                        subm.getSubmission().getProblemAlias(),
                        subm.getSubmission().getVerdict()));
                format.addCode(subm.getCode());
            }
        }

        return format.get();
    }


    private List<ContestSubmissionWithCode> fillSubmissionByCode(List<ContestSubmission> submissions, String contestId, String authHeader) {
        Function<ContestSubmission, ContestSubmissionWithCode> transfromFunction = subm -> {
            if (subm.getVerdict().equals("OK")) {
                return new ContestSubmissionWithCode(subm,
                        contestFeignClient.getSubmissionCode(contestId, String.valueOf(subm.getId()), authHeader));
            }
            return new ContestSubmissionWithCode(subm, null);
        };

        return submissions.stream()
                .map(transfromFunction)
                .limit(10)
                .toList();
    }
}
