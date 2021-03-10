package net.thumbtack.school.elections.server.dto.request;

public class GetCandidateDtoRequest {
    private final String login;

    public GetCandidateDtoRequest(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}