package net.thumbtack.school.elections.server.dto.request;

public class GetVoterDtoRequest {
    private final String token;

    public GetVoterDtoRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}