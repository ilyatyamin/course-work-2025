package org.ilyatyamin.yacontesthelper.autoupdate.controller;

import lombok.AllArgsConstructor;
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateDeleteRequest;
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest;
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateResponse;
import org.ilyatyamin.yacontesthelper.autoupdate.service.AutoUpdateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class AutoUpdateController {
    private final AutoUpdateService autoUpdateService;

    @PutMapping("/autoupdate")
    public ResponseEntity<AutoUpdateResponse> setAutoUpdate(@RequestBody AutoUpdateRequest request) {
        AutoUpdateResponse response = autoUpdateService.setOnAutoUpdate(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/autoupdate")
    public ResponseEntity<Void> removeAutoUpdate(@RequestBody AutoUpdateDeleteRequest request) {
        autoUpdateService.removeFromAutoUpdate(request);
        return ResponseEntity.ok().build();
    }
}
