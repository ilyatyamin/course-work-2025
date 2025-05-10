package org.ilyatyamin.yacontesthelper.client.service

import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateInfo
import org.ilyatyamin.yacontesthelper.autoupdate.service.AutoUpdateService
import org.ilyatyamin.yacontesthelper.client.dto.GetUserInfoResponse
import org.ilyatyamin.yacontesthelper.error.AuthException
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages
import org.ilyatyamin.yacontesthelper.security.enums.Role
import org.ilyatyamin.yacontesthelper.security.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class ClientServiceImpl(
    private val userService: UserService,
    private val autoUpdateService: AutoUpdateService,
) : ClientService {
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

    private fun <Response> clientSecurityCheck(username: String, action: () -> Response): Response {
        if (isAccessAllowed(username)) {
            return action.invoke()
        } else {
            throw AuthException(
                HttpStatus.FORBIDDEN.value(),
                ExceptionMessages.FORBIDDEN.message
            )
        }
    }

    private fun isAccessAllowed(requestedUserName: String?): Boolean {
        val currentUserName = SecurityContextHolder.getContext().authentication.credentials as String?
        val authorities = userService.getCurrentUserRole()

        return currentUserName == requestedUserName || authorities.contains(Role.ROLE_ADMIN)
    }
}