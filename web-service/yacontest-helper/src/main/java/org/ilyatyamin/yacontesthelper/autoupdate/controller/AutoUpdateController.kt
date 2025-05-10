package org.ilyatyamin.yacontesthelper.autoupdate.controller

import lombok.AllArgsConstructor
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateDeleteRequest
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateResponse
import org.ilyatyamin.yacontesthelper.autoupdate.service.AutoUpdateService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@AllArgsConstructor
@RequestMapping("/api")
class AutoUpdateController(
    private val autoUpdateService: AutoUpdateService
) {
    @PutMapping("/autoupdate")
    fun setAutoUpdate(@RequestBody request: AutoUpdateRequest): ResponseEntity<AutoUpdateResponse> {
        val response = autoUpdateService.setOnAutoUpdate(request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/autoupdate")
    fun removeAutoUpdate(@RequestBody request: AutoUpdateDeleteRequest): ResponseEntity<Void> {
        autoUpdateService.removeFromAutoUpdate(request)
        return ResponseEntity.ok().build()
    }
}
