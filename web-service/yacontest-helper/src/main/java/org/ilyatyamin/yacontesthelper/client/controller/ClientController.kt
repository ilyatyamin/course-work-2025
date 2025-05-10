package org.ilyatyamin.yacontesthelper.client.controller

import org.ilyatyamin.yacontesthelper.client.dto.GetUserAutoUpdatesRequest
import org.ilyatyamin.yacontesthelper.client.dto.GetUserAutoUpdatesResponse
import org.ilyatyamin.yacontesthelper.client.dto.GetUserInfoRequest
import org.ilyatyamin.yacontesthelper.client.dto.GetUserInfoResponse
import org.ilyatyamin.yacontesthelper.client.service.ClientService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api/user"])
class ClientController(
    private val clientService: ClientService,
) {
    @GetMapping
    fun getUserInfo(
        @RequestBody request: GetUserInfoRequest
    ): ResponseEntity<GetUserInfoResponse> {
        return ResponseEntity.ok(clientService.getUserInfo(request.username))
    }

    @GetMapping("/autoupdate")
    fun getAutoUpdateJobs(
        @RequestBody request: GetUserAutoUpdatesRequest
    ): ResponseEntity<GetUserAutoUpdatesResponse> {
        val updates = clientService.getAutoUpdateList(request.username)
        return ResponseEntity.ok(GetUserAutoUpdatesResponse(updates))
    }
}