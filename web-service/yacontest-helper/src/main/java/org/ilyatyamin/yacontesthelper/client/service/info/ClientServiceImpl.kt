package org.ilyatyamin.yacontesthelper.client.service.info

import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateInfo
import org.ilyatyamin.yacontesthelper.autoupdate.service.AutoUpdateService
import org.ilyatyamin.yacontesthelper.client.dto.GetUserInfoResponse
import org.ilyatyamin.yacontesthelper.client.service.CommonClientService
import org.ilyatyamin.yacontesthelper.error.AuthException
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages
import org.ilyatyamin.yacontesthelper.security.enums.Role
import org.ilyatyamin.yacontesthelper.security.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class ClientServiceImpl(
    private val autoUpdateService: AutoUpdateService,
    userService: UserService,
) : ClientService, CommonClientService(userService) {
    override fun getUserInfo(username: String): GetUserInfoResponse = clientSecurityCheck(username) {
        val userInfo = userService.getByUsername(username)
        GetUserInfoResponse(
            username = userInfo.username,
            firstName = userInfo.firstName,
            lastName = userInfo.lastName
        )
    }

    override fun getAutoUpdateList(username: String): List<AutoUpdateInfo> = clientSecurityCheck(username) {
        autoUpdateService.getAutoUpdateInfo(username)
    }
}