package net.thumbtack.school.elections.server.dto.request;

public class GetVoterByLoginDtoRequest {
    private final String login;

    public GetVoterByLoginDtoRequest(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}