package org.ilyatyamin.yacontesthelper.configs;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExceptionMessages {
    YACONTEST_WRONG_KEY("Provided wrong yandex auth key."),
    EMPTY_BODY("YaContest provided empty body."),
    NO_ACCESS_TO_CONTEST("No access to this contest."),
    CONTEST_NOT_FOUND("Contest with this id not found."),;

    private final String message;
}
