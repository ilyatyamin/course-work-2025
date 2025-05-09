package org.ilyatyamin.yacontesthelper.error

enum class ExceptionMessages(val message: String) {
    YACONTEST_WRONG_KEY("Ключ авторизации к Яндекс.Контесту неверный, нет доступа"),

    EMPTY_BODY("Яндекс.Контест отправил пустое тело ответа"),

    NO_ACCESS_TO_CONTEST("Нет доступа к этому контесту, проверьте данные"),
    CONTEST_NOT_FOUND("Контест с данным ID не найден"),
    GRADES_TABLE_NOT_FOUND("Таблица оценок с данным ID не найдена"),
    GOOGLE_SHEETS_NOT_FOUND("Google Таблица с данным ID не найдена"),
    UPDATE_TASK_NOT_FOUND("Джоб по обновлению оценок с данным ID не найден"),

    AUTH_FIELD_IS_NULL("Логин или пароль не могут быть пустыми"),

    NO_SUCH_USER("Не нашли такого пользователя. Повторите попытку"),

    USERNAME_ALREADY_EXISTS("Пользователь с таким username уже существует"),
    USER_EMAIL_ALREADY_EXISTS("Пользователь с такой почтой уже существует"),

    TOKEN_EXPIRED("Your token has expired. Please refresh it."),
    PROVIDED_AUTH_MUST_REFRESH("You provided auth token but method need refresh token."),
    PROVIDED_REFRESH_MUST_AUTH("You provided refress token but method need auth token."),
    TOKEN_DOES_NOT_EXIST("Your token does not exist.")
}
