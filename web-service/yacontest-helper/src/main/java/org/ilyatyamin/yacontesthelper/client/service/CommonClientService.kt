package org.ilyatyamin.yacontesthelper.client.service

import org.ilyatyamin.yacontesthelper.error.AuthException
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages
import org.ilyatyamin.yacontesthelper.security.enums.Role
import org.ilyatyamin.yacontesthelper.security.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder

open class CommonClientService(
    protected val userService: UserService,
) {
    protected fun <Response> clientSecurityCheck(username: String, action: () -> Response): Response {
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