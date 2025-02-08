package org.ilyatyamin.yacontesthelper.configs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExceptionMessages {
    YACONTEST_WRONG_KEY("Provided wrong yandex auth key."),
    EMPTY_BODY("YaContest provided empty body."),
    NO_ACCESS_TO_CONTEST("No access to this contest."),
    CONTEST_NOT_FOUND("Contest with this id not found."),
    GRADES_TABLE_NOT_FOUND("Grades table with this id not found."),
    GOOGLE_SHEETS_NOT_FOUND("Google sheets with this id not found."),
    UPDATE_TASK_NOT_FOUND("Update task with this id not found."),
    ;

    private final String message;
}
