package net.thumbtack.school.elections.server.dto.response;

public class LoginVoterDtoResponse {
    private String token;

    public LoginVoterDtoResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}