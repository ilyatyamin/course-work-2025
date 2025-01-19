package org.ilyatyamin.yacontesthelper.configs;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExceptionMessages {
    YACONTEST_WRONG_KEY("Provided wrong yandex auth key.");

    private final String message;
}
