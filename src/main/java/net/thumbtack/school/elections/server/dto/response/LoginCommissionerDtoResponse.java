package net.thumbtack.school.elections.server.dto.response;

public class LoginCommissionerDtoResponse {
    private final String token;

    public LoginCommissionerDtoResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}