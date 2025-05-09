package org.ilyatyamin.yacontesthelper.report.controller

import org.ilyatyamin.yacontesthelper.report.dto.ReportRequest
import org.ilyatyamin.yacontesthelper.report.service.ReportService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class ReportController(private val reportService: ReportService) {
    @PostMapping("api/report")
    fun getGradesReport(@RequestBody request: ReportRequest): ResponseEntity<ByteArray> {
        val data = reportService.createReport(request)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        val fileName = String.format("report.%s", request.saveFormat.name.lowercase(Locale.getDefault()))

        headers.setContentDispositionFormData("attachment", fileName)

        return ResponseEntity
            .ok()
            .headers(headers)
            .body(data)
    }
}
