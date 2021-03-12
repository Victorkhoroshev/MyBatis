package net.thumbtack.school.elections.server.dto.request;

public class GetElectionResultDtoRequest {
    private final String token;

    public GetElectionResultDtoRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
