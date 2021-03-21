package net.thumbtack.school.elections.server.service;
//REVU: нужно перенести в другой пакет
public enum ExceptionErrorCode {
    ALREADY_EXISTS("Вы уже зарегестрированны."),
    LOGOUT("Сессия пользователя не найдена."),
    RATING_INCORRECT("Оценка должна быть от 1 до 5."),
    NOT_FOUND("Пользователь не найден."),
    LOGIN_ALREADY_EXISTS("Такой логин уже используется."),
    WRONG_PASSWORD("Неверный пароль."),
    NOT_CHAIRMAN("Вы не председатель коммиссии."),
    CANDIDATE_NOT_FOUND("Кандидат не найден."),
    IDEA_NOT_FOUND("Идея не найдена."),
    ELECTION_START("Выборы уже проходят, действие невозможно."),
    ELECTION_NOT_START("Голосование не началось."),
    ELECTION_STOP("Голосование закончилось."),
    DATA_NOT_FIELD("Пожалуйста, заполните все данные."),
    FIRSTNAME_NOT_VALID("Имя должно быть на кириллице, без пробелов, спец. символов и цифр."),
    LASTNAME_NOT_VALID("Фамилия должна быть на кириллице, без пробелов, спец. символов и цифр."),
    PATRONYMIC_NOT_VALID("Отчество должно быть на кириллице, без пробелов, спец. символов и цифр."),
    STREET_NOT_VALID("Название улицы должно быть на кириллице, без пробелов, спец. символов и цифр."),
    HOUSE_NOT_VALID("Номер дома не может быть меньше единицы."),
    APARTMENT_NOT_VALID("Номер квартиры не может быть меньше нуля."),
    LOGIN_NOT_VALID("Длинна логина должна быть не меньше 9 символов."),
    PASSWORD_NOT_VALID("Пароль должен содержать хотя бы одну заглавную букву, одну строчную букву," +
            " цифру и один спец. символ, а его длинна не менее 9 символов, без пробелов."),
    NULL_VALUE("Некорректный запрос.");

    private final String message;

    ExceptionErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}