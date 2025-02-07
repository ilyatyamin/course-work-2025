package org.ilyatyamin.yacontesthelper.error;

public record ErrorResponse(
        Integer code,
        String message
) {
}
