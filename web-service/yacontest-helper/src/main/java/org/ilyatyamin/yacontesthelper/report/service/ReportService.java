package org.ilyatyamin.yacontesthelper.report.service;

import org.ilyatyamin.yacontesthelper.grades.dto.ContestSubmissionWithCode;
import org.ilyatyamin.yacontesthelper.report.dto.ReportRequest;

import java.util.List;
import java.util.Map;

public interface ReportService {
    byte[] createReport(ReportRequest request);
    String getMarkdownReport(ReportRequest request,
                             Map<String, Map<String, Double>> totalGrades,
                             Map<String, Double> submissionStatsOk,
                             List<ContestSubmissionWithCode> codeSubmissions);
}
