package org.ilyatyamin.yacontesthelper.error

import io.jsonwebtoken.ExpiredJwtException
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestControllerAdvice
@Slf4j
class ExceptionHandler {
    private val logger = LoggerFactory.getLogger(ExceptionHandler::class.java)

    @org.springframework.web.bind.annotation.ExceptionHandler(value = [YaContestException::class])
    fun yaContestException(exception: YaContestException): ResponseEntity<ErrorResponse> {
        logError(exception.message, HttpStatus.valueOf(exception.code), exception.code)
        return ResponseEntity
            .status(exception.code)
            .body(ErrorResponse(exception.code, exception.message))
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = [Exception::class])
    fun baseException(exception: Exception): ResponseEntity<ErrorResponse> {
        if (exception is ExpiredJwtException) {
            return expiredTokenException(exception)
        }
        val code = 500

        logError(exception.message, HttpStatus.valueOf(code), code)
        return ResponseEntity
            .status(code)
            .body(ErrorResponse(code, exception.message))
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = [AuthException::class])
    fun authException(exception: AuthException): ResponseEntity<ErrorResponse> {
        logError(exception.message, HttpStatus.valueOf(exception.code), exception.code)
        return ResponseEntity
            .status(exception.code)
            .body(ErrorResponse(exception.code, exception.message))
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = [ExpiredJwtException::class])
    fun expiredTokenException(exception: ExpiredJwtException): ResponseEntity<ErrorResponse> {
        logError(exception.message, HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value())

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED.value())
            .body(
                ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    ExceptionMessages.TOKEN_EXPIRED.message
                )
            )
    }

    private fun logError(message: String?,
                         httpStatus: HttpStatus,
                         internalStatus: Int?) {
        if (internalStatus != null) {
            logger.error("[EXCEPTION][http = ${httpStatus.name}, internal = $internalStatus][${getBeautifiedDateTime()}] $message")
        } else {
            logger.error("[EXCEPTION][http = ${httpStatus.name}][${getBeautifiedDateTime()}] $message")
        }
    }

    private fun getBeautifiedDateTime(): String {
        val currentTimestamp = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        return currentTimestamp.format(formatter)
    }
}
