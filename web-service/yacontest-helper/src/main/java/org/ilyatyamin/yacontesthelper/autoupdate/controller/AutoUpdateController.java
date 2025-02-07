package org.ilyatyamin.yacontesthelper.autoupdate.controller;

import lombok.AllArgsConstructor;
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping
public class AutoUpdateController {
    @PostMapping("/api/update")
    public ResponseEntity<Void> setAutoUpdate(@RequestBody AutoUpdateRequest request) {
        return ResponseEntity.ok().build();
    }
}
