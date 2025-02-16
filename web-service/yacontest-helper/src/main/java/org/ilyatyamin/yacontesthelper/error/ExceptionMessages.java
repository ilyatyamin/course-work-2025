package org.ilyatyamin.yacontesthelper.error;

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

    AUTH_FIELD_IS_NULL("Your username or password cannot be null."),
    NO_SUCH_USER("No such user found in database."),
    USERNAME_ALREADY_EXISTS("User with this username already exists."),
    USER_EMAIL_ALREADY_EXISTS("User with this email already exists."),
    TOKEN_EXPIRED("Your token has expired. Please refresh it."),
    PROVIDED_AUTH_MUST_REFRESH("You provided auth token but method need refresh token."),
    TOKEN_DOES_NOT_EXIST("Your token does not exist."),
    ;

    private final String message;
}
