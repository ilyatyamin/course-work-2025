package org.ilyatyamin.yacontesthelper.controller;

import lombok.AllArgsConstructor;
import org.ilyatyamin.yacontesthelper.dto.report.ReportRequest;
import org.ilyatyamin.yacontesthelper.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping("api/report")
    ResponseEntity<byte[]> getGradesReport(@RequestBody ReportRequest request) {
        byte[] data = reportService.createReport(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String fileName = String.format("report.%s", request.getSaveFormat().name().toLowerCase());

        headers.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(data);
    }
}
