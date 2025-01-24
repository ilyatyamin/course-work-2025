package org.ilyatyamin.yacontesthelper.exceptions;

public class YaContestException extends RuntimeException {
    private final Integer code;

    public YaContestException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
