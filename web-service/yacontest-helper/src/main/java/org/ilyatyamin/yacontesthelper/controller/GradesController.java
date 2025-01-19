package org.ilyatyamin.yacontesthelper.controller;

import org.ilyatyamin.yacontesthelper.dto.grades.GradesRequest;
import org.ilyatyamin.yacontesthelper.dto.grades.GradesResponse;
import org.ilyatyamin.yacontesthelper.service.GradesService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class GradesController {
    private GradesService gradesService;

    @PostMapping("/grades")
    ResponseEntity<GradesResponse> getGrades(@RequestBody GradesRequest gradesRequest) {
        return ResponseEntity.ok(null);
    }
}
