package net.thumbtack.school.elections.server.service;

import net.thumbtack.school.elections.server.exeption.ExceptionErrorCode;
import net.thumbtack.school.elections.server.exeption.ServerException;

import java.util.List;
import java.util.regex.Pattern;

public class Validation {

    public void validate(String firstName, String lastName, String patronymic, String street, Integer house,
                         int apartment, String login, String password) throws ServerException {
        if (firstName == null || lastName == null || street == null ||
                house == null || login == null || password == null) {
            throw new ServerException(ExceptionErrorCode.DATA_NOT_FIELD);
        }
        if (!isFirstNameValid(firstName)) {
            throw new ServerException(ExceptionErrorCode.FIRSTNAME_NOT_VALID);
        }
        if (!isLastNameValid(lastName)) {
            throw new ServerException(ExceptionErrorCode.LASTNAME_NOT_VALID);
        }
        if (!isPatronymicValid(patronymic)) {
            throw new ServerException(ExceptionErrorCode.PATRONYMIC_NOT_VALID);
        }
        if (!isStreetValid(street)) {
            throw new ServerException(ExceptionErrorCode.STREET_NOT_VALID);
        }
        if (!isHouseValid(house)) {
            throw new ServerException(ExceptionErrorCode.HOUSE_NOT_VALID);
        }
        if (!isApartmentValid(apartment)) {
            throw new ServerException(ExceptionErrorCode.APARTMENT_NOT_VALID);
        }
        if (!isLoginValid(login)) {
            throw new ServerException(ExceptionErrorCode.LOGIN_NOT_VALID);
        }
        if (!isPasswordValid(password)) {
            throw new ServerException(ExceptionErrorCode.PASSWORD_NOT_VALID);
        }
    }

    public void validate(String login, String password) throws ServerException {
        if (login == null || password == null) {
            throw new ServerException(ExceptionErrorCode.DATA_NOT_FIELD);
        }
        if (!isLoginValid(login)) {
            throw new ServerException(ExceptionErrorCode.LOGIN_NOT_VALID);
        }
        if (!isPasswordValid(password)) {
            throw new ServerException(ExceptionErrorCode.PASSWORD_NOT_VALID);
        }
    }

    public void validate(String s) throws ServerException {
        if (s == null) {
            throw new ServerException(ExceptionErrorCode.NULL_VALUE);
        }
    }

    public void validate(List<String> list) throws ServerException {
        if (list == null) {
            throw new ServerException(ExceptionErrorCode.NULL_VALUE);
        }
    }

    public void validate(Integer rating) throws ServerException {
        if (rating == null) {
            throw new ServerException(ExceptionErrorCode.NULL_VALUE);
        }
        if (rating > 5 || rating < 1) {
            throw new ServerException(ExceptionErrorCode.RATING_INCORRECT);
        }
    }

    public void validate(String ideaKey, String token, Integer rating) throws ServerException {
        validate(ideaKey);
        validate(token);
        validate(rating);
    }

    public void validate(String token, List<String> list) throws ServerException {
        validate(token);
        validate(list);
    }

    private boolean isFirstNameValid(String firstName) {
        return isStringValid(firstName);
    }

    private boolean isLastNameValid(String lastName) {
        return isStringValid(lastName);
    }

    private boolean isPatronymicValid(String patronymic) {
        return patronymic == null || isStringValid(patronymic);
    }

    private boolean isStreetValid(String street) {
        return isStringValid(street);
    }

    private boolean isStringValid(String s) {
        return s.matches("[а-яА-Я]+");
    }

    private boolean isHouseValid(int house) {
        return house > 0;
    }

    private boolean isApartmentValid(Integer apartment) {
        return apartment == null || apartment >= 0;
    }

    private boolean isLoginValid(String login) {
        return login.length() > 8;
    }

    private boolean isPasswordValid(String password) {
        return passwordValidation(password);
    }

    private boolean passwordValidation(String password) {
        Pattern[] patterns = new Pattern[] {Pattern.compile("^(?=.*[a-zа-я]).+$"),
                Pattern.compile("^(?=.*[A-ZА-Я]).+$"),
                Pattern.compile(".*\\d.*"), Pattern.compile(".*\\W.*")};
        for (Pattern pattern: patterns) {
            if (!pattern.matcher(password).matches() || password.length() < 9 || password.matches(".*\\s.*")) {
                return false;
            }
        }
        return true;
    }
}
