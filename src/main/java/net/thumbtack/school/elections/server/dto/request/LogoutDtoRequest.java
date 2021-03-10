package net.thumbtack.school.elections.server.dto.request;

public class LogoutDtoRequest {
    private final String token;

    public LogoutDtoRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}