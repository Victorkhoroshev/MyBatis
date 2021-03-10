package net.thumbtack.school.elections.server.dto.request;

public class LoginDtoRequest {
    private final String login;
    private final String password;

    public LoginDtoRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

}