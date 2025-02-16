package org.ilyatyamin.yacontesthelper.security.service

import org.ilyatyamin.yacontesthelper.error.AuthException
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages
import org.ilyatyamin.yacontesthelper.security.dao.Role
import org.ilyatyamin.yacontesthelper.security.dao.TokenType
import org.ilyatyamin.yacontesthelper.security.dao.UserDao
import org.ilyatyamin.yacontesthelper.security.dto.LoginRequest
import org.ilyatyamin.yacontesthelper.security.dto.RefreshTokenRequest
import org.ilyatyamin.yacontesthelper.security.dto.RegisterRequest
import org.ilyatyamin.yacontesthelper.security.dto.TokenResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val userService: UserService,
    private val jwtTokenService: JwtTokenService
) {
    internal fun registerUser(request: RegisterRequest): TokenResponse {
        val user = UserDao(
            request.username,
            passwordEncoder.encode(request.password),
            request.email,
            Role.ROLE_USER
        )
        userService.createUser(user)

        val authToken = jwtTokenService.generateToken(user, TokenType.AUTH)
        val refreshToken = jwtTokenService.generateToken(user, TokenType.REFRESH)
        return TokenResponse(authToken, refreshToken, jwtTokenService.tokenType)
    }

    internal fun loginUser(request: LoginRequest): TokenResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )

        val user: UserDao = userService.getByUsername(request.username)

        val authToken = jwtTokenService.generateToken(user, TokenType.AUTH)
        val refreshToken = jwtTokenService.generateToken(user, TokenType.REFRESH)
        return TokenResponse(authToken, refreshToken, jwtTokenService.tokenType)
    }

    internal fun refreshToken(request: RefreshTokenRequest): TokenResponse {
        val username = jwtTokenService.extractUsername(request.refreshToken)

        if (!jwtTokenService.isTokenExists(request.refreshToken)) {
            throw AuthException(
                HttpStatus.UNAUTHORIZED.value(),
                ExceptionMessages.TOKEN_DOES_NOT_EXIST.message
            )
        }
        if (!jwtTokenService.isTokenRefresh(request.refreshToken)) {
            throw AuthException(
                HttpStatus.UNAUTHORIZED.value(),
                ExceptionMessages.PROVIDED_AUTH_MUST_REFRESH.message
            )
        }

        if (jwtTokenService.isTokenNotExpired(request.refreshToken)) {
            val user: UserDao = userService.getByUsername(username)

            val authToken = jwtTokenService.generateToken(user, TokenType.AUTH)
            val refreshToken = jwtTokenService.generateToken(user, TokenType.REFRESH)
            return TokenResponse(authToken, refreshToken, jwtTokenService.tokenType)
        } else {
            throw AuthException(
                HttpStatus.UNAUTHORIZED.value(),
                ExceptionMessages.TOKEN_EXPIRED.message
            )
        }
    }
}
