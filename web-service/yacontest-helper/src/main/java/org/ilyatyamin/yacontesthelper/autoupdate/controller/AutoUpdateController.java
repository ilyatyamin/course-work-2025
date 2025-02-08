package org.ilyatyamin.yacontesthelper.autoupdate.controller;

import lombok.AllArgsConstructor;
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest;
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateResponse;
import org.ilyatyamin.yacontesthelper.autoupdate.service.AutoUpdateService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping
public class AutoUpdateController {
    private final AutoUpdateService autoUpdateService;

    @PutMapping("/api/update")
    public ResponseEntity<AutoUpdateResponse> setAutoUpdate(@RequestBody AutoUpdateRequest request) {
        AutoUpdateResponse response = autoUpdateService.setOnAutoUpdate(request);
        return ResponseEntity.ok(response);
    }
}
