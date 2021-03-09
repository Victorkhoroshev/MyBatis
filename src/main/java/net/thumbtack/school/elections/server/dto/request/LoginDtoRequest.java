package net.thumbtack.school.elections.server.dto.request;

import java.util.regex.Pattern;

public class LoginDtoRequest {
    private String login;
    private String password;

    public LoginDtoRequest(String login, String password) {
        setLogin(login);
        setPassword(password);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean requiredFieldsIsNotNull() {
        return login != null && password != null;
    }

    public boolean isLoginValid() {
        return login.length() > 8;
    }

    public boolean isPasswordValid() {
        return passwordValidation(password);
    }

    static boolean passwordValidation(String password) {
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