package net.thumbtack.school.elections.server.dto.request;

public class IsCandidateDtoRequest {
    private final String token;

    public IsCandidateDtoRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}