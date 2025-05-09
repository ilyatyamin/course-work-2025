package org.ilyatyamin.yacontesthelper.report.service;

import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.AllArgsConstructor;
import org.ilyatyamin.yacontesthelper.grades.dto.ContestSubmission;
import org.ilyatyamin.yacontesthelper.grades.dto.ContestSubmissionWithCode;
import org.ilyatyamin.yacontesthelper.grades.service.feign.ContestFeignClient;
import org.ilyatyamin.yacontesthelper.grades.service.processor.SubmissionProcessorService;
import org.ilyatyamin.yacontesthelper.grades.service.yacontest.YaContestService;
import org.ilyatyamin.yacontesthelper.report.dto.ReportRequest;
import org.ilyatyamin.yacontesthelper.utils.HeaderLevel;
import org.ilyatyamin.yacontesthelper.utils.MarkdownFormatter;
import org.ilyatyamin.yacontesthelper.utils.UtilsService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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

        switch (request.getSaveFormat()) {
            case MD -> {
                return markdownFormat.getBytes();
            }
            case PDF -> {
                return getPDFReport(markdownFormat);
            }
            default -> throw new IllegalStateException("Unexpected value: " + request.getSaveFormat());
        }
    }

    @Override
    public String getMarkdownReport(ReportRequest request,
                                    Map<String, Map<String, Double>> totalGrades,
                                    Map<String, Double> submissionStatsOk,
                                    List<ContestSubmissionWithCode> codeSubmissions) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);

        MarkdownFormatter format = MarkdownFormatter.create()
                .addHeader("Отчет по посылкам", HeaderLevel.FIRST)
                .addHeader(String.format("Сформирован %s для контеста с id #%s", formatter.format(ZonedDateTime.now(ZoneId.systemDefault())), request.getContestId()), HeaderLevel.SECOND)
                .addText(String.format("Крайний срок сдачи: %s", request.getDeadline()))
                .addHeader("Результаты", HeaderLevel.THIRD)
                .addTable(totalGrades)
                .addHeader("Статистика по вердиктам (количество посылок со статусом ОК среди всех посылок данной задачи)", HeaderLevel.THIRD)
                .addOneDimTable(submissionStatsOk)
                .addHeader("Исходные коды посылок со статусом OK: ", HeaderLevel.SECOND);

        // TODO: планировались графики. Как их встроить?
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

        // TODO: планировалась проверка на плагиат. Как встроить?

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
                .toList();
    }

    private byte[] getPDFReport(String markdownReport) {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, List.of(TablesExtension.create()));

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Node htmlReport = parser.parse(markdownReport);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        HtmlConverter.convertToPdf(renderer.render(htmlReport), baos);
        return baos.toByteArray();
    }
}
