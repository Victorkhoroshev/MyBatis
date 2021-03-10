package net.thumbtack.school.elections.server.dto.request;

public class WithdrawCandidacyDtoRequest {
    private final String token;

    public WithdrawCandidacyDtoRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}