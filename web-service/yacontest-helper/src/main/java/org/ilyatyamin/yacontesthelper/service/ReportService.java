package org.ilyatyamin.yacontesthelper.service;

import org.ilyatyamin.yacontesthelper.dto.report.ReportRequest;
import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestSubmissionWithCode;

import java.util.List;
import java.util.Map;

public interface ReportService {
    byte[] createReport(ReportRequest request);
    String getMarkdownReport(ReportRequest request,
                             Map<String, Map<String, Double>> totalGrades,
                             Map<String, Double> submissionStatsOk,
                             List<ContestSubmissionWithCode> codeSubmissions);
}
