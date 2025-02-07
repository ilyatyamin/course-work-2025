package org.ilyatyamin.yacontesthelper.grades.controller;

import lombok.AllArgsConstructor;
import org.ilyatyamin.yacontesthelper.grades.dto.GoogleSheetsRequest;
import org.ilyatyamin.yacontesthelper.grades.dto.GradesRequest;
import org.ilyatyamin.yacontesthelper.grades.dto.GradesResponse;
import org.ilyatyamin.yacontesthelper.grades.service.core.GradesService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/grades")
@AllArgsConstructor
public class GradesController {
    private GradesService gradesService;

    @PostMapping
    ResponseEntity<GradesResponse> getGrades(@RequestBody GradesRequest gradesRequest) {
        GradesResponse response = gradesService.getGradesList(gradesRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{tableId}/xlsx")
    ResponseEntity<byte[]> getGradesExcelTable(@PathVariable Long tableId) {
        byte[] excelSheet = gradesService.generateGradesTable(tableId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "data.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelSheet);
    }

    @PostMapping("/{tableId}/googleSheets")
    ResponseEntity<Void> getGradesGoogleSheets(@PathVariable Long tableId,
                                               @RequestBody GoogleSheetsRequest request) {
        gradesService.writeToGoogleSheets(tableId, request);
        return ResponseEntity.ok().build();
    }
}
