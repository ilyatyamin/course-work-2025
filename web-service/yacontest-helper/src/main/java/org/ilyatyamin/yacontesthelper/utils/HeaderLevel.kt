package org.ilyatyamin.yacontesthelper.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HeaderLevel {
    FIRST(1),
    SECOND(2),
    THIRD(3);

    private final Integer level;
}
