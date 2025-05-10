package org.ilyatyamin.yacontesthelper.security.service

import org.ilyatyamin.yacontesthelper.error.AuthException
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages
import org.ilyatyamin.yacontesthelper.security.dao.UserDao
import org.ilyatyamin.yacontesthelper.security.enums.Role
import org.ilyatyamin.yacontesthelper.security.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull


@Service
class UserService(
    private var userRepository: UserRepository
) {
    fun getCurrentUserId(): Long? {
        val userName = SecurityContextHolder.getContext().authentication.credentials as String?
        userName?.let {
            val user = userRepository.findByUsername(userName)
            return user.getOrNull()?.id
        }
        return null
    }

    fun getCurrentUserRole(): List<Role> {
        return SecurityContextHolder.getContext().authentication.authorities
            .map { Role.valueOf(it.authority) }
            .toList()
    }

    internal fun createUser(dao: UserDao): UserDao {
        if (userRepository.existsByUsername(dao.username)) {
            throw AuthException(
                HttpStatus.CONFLICT.value(),
                ExceptionMessages.USERNAME_ALREADY_EXISTS.message
            )
        }
        if (userRepository.existsByEmail(dao.email)) {
            throw AuthException(
                HttpStatus.CONFLICT.value(),
                ExceptionMessages.USER_EMAIL_ALREADY_EXISTS.message
            )
        }
        return userRepository.save(dao)
    }

    internal fun getByUsername(username: String): UserDao {
        return userRepository.findByUsername(username)
            .orElseThrow {
                AuthException(
                    HttpStatus.UNAUTHORIZED.value(),
                    ExceptionMessages.NO_SUCH_USER.message
                )
            }
    }

    internal fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username -> getByUsername(username) }
    }
}
