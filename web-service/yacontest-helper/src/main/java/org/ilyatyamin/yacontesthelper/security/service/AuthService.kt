package org.ilyatyamin.yacontesthelper.security.service

import org.ilyatyamin.yacontesthelper.security.dao.Role
import org.ilyatyamin.yacontesthelper.security.dao.UserDao
import org.ilyatyamin.yacontesthelper.security.dto.LoginRequest
import org.ilyatyamin.yacontesthelper.security.dto.RegisterRequest
import org.ilyatyamin.yacontesthelper.security.dto.TokenResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthService {
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var jwtTokenService: JwtTokenService

    fun registerUser(request : RegisterRequest): TokenResponse {
        val user = UserDao(request.username, request.email, request.password, Role.ROLE_USER)
        userService.createUser(user)

        val jwt = jwtTokenService.generateToken(user);
        return TokenResponse(jwt);
    }

    fun loginUser(request: LoginRequest): TokenResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )

        val user : UserDetails = userService.userDetailsService().loadUserByUsername(request.username)

        val jwt = jwtTokenService.generateToken(user)
        return TokenResponse(jwt)
    }
}
