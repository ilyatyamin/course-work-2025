package org.ilyatyamin.yacontesthelper.security.service

import org.ilyatyamin.yacontesthelper.error.AuthException
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages
import org.ilyatyamin.yacontesthelper.security.dao.UserDao
import org.ilyatyamin.yacontesthelper.security.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service


@Service
class UserService {
    @Autowired
    private lateinit var userRepository: UserRepository

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

    private fun getByUsername(username: String): UserDao {
        return userRepository.findByUsername(username)
            .orElseThrow {
                AuthException(
                    HttpStatus.UNAUTHORIZED.value(),
                    ExceptionMessages.NO_SUCH_USER.message
                )
            }
    }

    internal fun userDetailsService(): UserDetailsService {
        return UserDetailsService{username -> getByUsername(username)}
    }

    internal fun getCurrentUser() : UserDao {
        val username = SecurityContextHolder.getContext().authentication.name
        return getByUsername(username)
    }
}
