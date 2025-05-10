package org.ilyatyamin.yacontesthelper.client.controller

import org.ilyatyamin.yacontesthelper.client.dto.GetUserInfoRequest
import org.ilyatyamin.yacontesthelper.client.dto.GetUserInfoResponse
import org.ilyatyamin.yacontesthelper.client.service.ClientService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api"])
class ClientController(
    private val clientService: ClientService,
) {
    @GetMapping(value = ["/user"])
    fun getUserInfo(@RequestBody request: GetUserInfoRequest): ResponseEntity<GetUserInfoResponse> {
        return ResponseEntity.ok(clientService.getUserInfo(request.username))
    }
}