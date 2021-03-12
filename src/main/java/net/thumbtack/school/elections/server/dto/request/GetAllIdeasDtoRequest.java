package net.thumbtack.school.elections.server.dto.request;

public class GetAllIdeasDtoRequest {
    private final String token;

    public GetAllIdeasDtoRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
