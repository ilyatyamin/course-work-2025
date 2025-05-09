package org.ilyatyamin.yacontesthelper.report.service

import org.ilyatyamin.yacontesthelper.grades.dto.ContestSubmissionWithCode
import org.ilyatyamin.yacontesthelper.report.dto.ReportRequest

interface ReportService {
    fun createReport(request: ReportRequest): ByteArray

    fun getMarkdownReport(
        request: ReportRequest,
        totalGrades: Map<String, Map<String, Double>?>,
        submissionStatsOk: Map<String, Double>,
        codeSubmissions: List<ContestSubmissionWithCode>
    ): String?
}
