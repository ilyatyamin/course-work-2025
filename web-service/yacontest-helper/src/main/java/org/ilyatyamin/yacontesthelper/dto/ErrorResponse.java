package org.ilyatyamin.yacontesthelper.dto;

public record ErrorResponse(
        Integer code,
        String message
) {
}
