package org.ilyatyamin.yacontesthelper.configs;

import org.ilyatyamin.yacontesthelper.dto.ErrorResponse;
import org.ilyatyamin.yacontesthelper.exceptions.YaContestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(value = YaContestException.class)
    public ResponseEntity<ErrorResponse> yaContestException(YaContestException exception) {
        return ResponseEntity
                .status(exception.getCode())
                .body(new ErrorResponse(exception.getCode(), exception.getMessage()));
    }
}
