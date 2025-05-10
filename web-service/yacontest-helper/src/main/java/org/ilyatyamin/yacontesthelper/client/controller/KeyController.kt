package org.ilyatyamin.yacontesthelper.client.controller

import org.ilyatyamin.yacontesthelper.client.dto.GetAllUserKeysRequest
import org.ilyatyamin.yacontesthelper.client.dto.GetAllUserKeysResponse
import org.ilyatyamin.yacontesthelper.client.service.key.KeyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api/user/secretKey"])
class KeyController(
    private val keyService: KeyService,
) {
    @PostMapping
    fun getAllUserKeys(
        @RequestBody request: GetAllUserKeysRequest
    ): ResponseEntity<GetAllUserKeysResponse> {
        return ResponseEntity.ok(GetAllUserKeysResponse(keyService.getAllKeys(request)))
    }
}