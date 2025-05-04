package org.ilyatyamin.yacontesthelper.configs;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.ilyatyamin.yacontesthelper.error.AuthException;
import org.ilyatyamin.yacontesthelper.error.ErrorResponse;
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages;
import org.ilyatyamin.yacontesthelper.error.YaContestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(value = YaContestException.class)
    public ResponseEntity<ErrorResponse> yaContestException(YaContestException exception) {
        logException(exception);
        return ResponseEntity
                .status(exception.getCode())
                .body(new ErrorResponse(exception.getCode(), exception.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> baseException(Exception exception) {
        logException(exception);
        return ResponseEntity
                .status(500)
                .body(new ErrorResponse(500, exception.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = AuthException.class)
    public ResponseEntity<ErrorResponse> authException(AuthException exception) {
        logException(exception);
        return ResponseEntity
                .status(exception.getCode())
                .body(new ErrorResponse(exception.getCode(), exception.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> expiredTokenException(ExpiredJwtException exception) {
        logException(exception);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED.value())
                .body(new ErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        ExceptionMessages.TOKEN_EXPIRED.getMessage()
                ));
    }

    private void logException(final Exception exception) {
        log.error(exception.getMessage(), exception);
    }
}
