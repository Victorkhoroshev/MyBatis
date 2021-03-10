package net.thumbtack.school.elections.server.dto.request;

public class IsCommissionerDtoRequest {
    private final String login;
    private final String password;

    public IsCommissionerDtoRequest(String login, String password) {
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