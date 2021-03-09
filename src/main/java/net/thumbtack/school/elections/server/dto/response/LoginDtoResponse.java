package net.thumbtack.school.elections.server.dto.response;

public class LoginDtoResponse {
    private final String token;

    public LoginDtoResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
