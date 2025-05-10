package org.ilyatyamin.yacontesthelper.security.controller

import org.ilyatyamin.yacontesthelper.security.dto.LoginRequest
import org.ilyatyamin.yacontesthelper.security.dto.RefreshTokenRequest
import org.ilyatyamin.yacontesthelper.security.dto.RegisterRequest
import org.ilyatyamin.yacontesthelper.security.dto.TokenResponse
import org.ilyatyamin.yacontesthelper.security.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class AuthController(
    private var authService: AuthService
) {
    @PostMapping("/register")
    fun registerUser(@RequestBody registerRequest: RegisterRequest): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(authService.registerUser(registerRequest))
    }

    @PostMapping("/login")
    fun authUser(@RequestBody authRequest: LoginRequest): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(authService.loginUser(authRequest))
    }

    @PostMapping("/login/refreshToken")
    fun refreshToken(@RequestBody refreshTokenRequest: RefreshTokenRequest): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest))
    }
}
