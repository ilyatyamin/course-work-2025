package org.ilyatyamin.yacontesthelper.security.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    val username: String,

    @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустыми")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    val email: String,

    @Size(min = 5, max = 255, message = "Длина пароля должна быть не более 255 символов и не менее 5 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    val password: String
)

data class LoginRequest(
    @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    val username: String,

    @Size(min = 5, max = 255, message = "Длина пароля должна быть от 5 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    val password: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class TokenResponse(
    val authToken: String,
    val refreshToken: String,
    val tokenType: String
)