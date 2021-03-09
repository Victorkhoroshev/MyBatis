package net.thumbtack.school.elections.server.dto.request;

public class WithdrawCandidacyDtoRequest {
    private String token;

    public WithdrawCandidacyDtoRequest(String token) {
        setToken(token);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public boolean requiredFieldsIsNotNull() {
        return token != null;
    }
}