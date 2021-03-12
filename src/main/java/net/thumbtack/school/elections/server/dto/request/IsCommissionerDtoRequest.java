package net.thumbtack.school.elections.server.dto.request;

public class IsCommissionerDtoRequest {
    private final String login;

    public IsCommissionerDtoRequest(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}