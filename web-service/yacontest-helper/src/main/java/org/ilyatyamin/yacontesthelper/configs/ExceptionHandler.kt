package org.ilyatyamin.yacontesthelper.configs

import io.jsonwebtoken.ExpiredJwtException
import lombok.extern.slf4j.Slf4j
import org.ilyatyamin.yacontesthelper.error.AuthException
import org.ilyatyamin.yacontesthelper.error.ErrorResponse
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages
import org.ilyatyamin.yacontesthelper.error.YaContestException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Slf4j
class ExceptionHandler {
    private val logger = LoggerFactory.getLogger(ExceptionHandler::class.java)

    @org.springframework.web.bind.annotation.ExceptionHandler(value = [YaContestException::class])
    fun yaContestException(exception: YaContestException): ResponseEntity<ErrorResponse> {
        logException(exception)
        return ResponseEntity
            .status(exception.code)
            .body(ErrorResponse(exception.code, exception.message))
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = [Exception::class])
    fun baseException(exception: Exception): ResponseEntity<ErrorResponse> {
        if (exception is ExpiredJwtException) {
            return expiredTokenException(exception)
        }
        logException(exception)
        return ResponseEntity
            .status(500)
            .body(ErrorResponse(500, exception.message))
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = [AuthException::class])
    fun authException(exception: AuthException): ResponseEntity<ErrorResponse> {
        logException(exception)
        return ResponseEntity
            .status(exception.code)
            .body(ErrorResponse(exception.code, exception.message))
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = [ExpiredJwtException::class])
    fun expiredTokenException(exception: ExpiredJwtException): ResponseEntity<ErrorResponse> {
        logException(exception)
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED.value())
            .body(
                ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    ExceptionMessages.TOKEN_EXPIRED.message
                )
            )
    }

    private fun logException(exception: Exception) {
        logger.error(exception.message, exception)
    }
}
