package org.ilyatyamin.yacontesthelper.configs;

import lombok.extern.slf4j.Slf4j;
import org.ilyatyamin.yacontesthelper.dto.ErrorResponse;
import org.ilyatyamin.yacontesthelper.exceptions.YaContestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
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

    private void logException(final Exception exception) {
        log.error(exception.getMessage(), exception);
    }
}
