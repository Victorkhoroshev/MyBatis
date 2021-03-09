package net.thumbtack.school.elections.server.dto.response;

public class RegisterDtoResponse {
    private final String token;

    public RegisterDtoResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
