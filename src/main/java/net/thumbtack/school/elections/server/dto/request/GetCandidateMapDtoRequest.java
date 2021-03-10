package net.thumbtack.school.elections.server.dto.request;

public class GetCandidateMapDtoRequest {
    private final String token;

    public GetCandidateMapDtoRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
