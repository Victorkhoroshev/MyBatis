package net.thumbtack.school.elections.server.dto.request;

public class StartElectionDtoRequest {
    private final String token;

    public StartElectionDtoRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
