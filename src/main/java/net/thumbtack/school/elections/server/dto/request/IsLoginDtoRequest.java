package net.thumbtack.school.elections.server.dto.request;

public class IsLoginDtoRequest {
    private String token;

    public IsLoginDtoRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}