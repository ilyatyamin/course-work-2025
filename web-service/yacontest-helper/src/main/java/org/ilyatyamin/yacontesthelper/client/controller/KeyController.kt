package org.ilyatyamin.yacontesthelper.client.controller

import org.ilyatyamin.yacontesthelper.client.dto.*
import org.ilyatyamin.yacontesthelper.client.service.key.KeyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    @PutMapping
    fun addNewKey(
        @RequestBody request: AddNewKeyRequest
    ): ResponseEntity<SuccessResponse> {
        keyService.addNewKey(request)
        return ResponseEntity.ok(SuccessResponse(true))
    }

    @DeleteMapping
    fun deleteNewKey(
        @RequestBody request: DeleteKeyRequest
    ): ResponseEntity<SuccessResponse> {
        keyService.deleteKey(request)
        return ResponseEntity.ok(SuccessResponse(true))
    }
}