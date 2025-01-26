package org.ilyatyamin.yacontesthelper.exceptions;

import lombok.Getter;

@Getter
public class YaContestException extends RuntimeException {
    private final Integer code;

    public YaContestException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
