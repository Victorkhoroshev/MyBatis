package net.thumbtack.school.elections.server.dto.request;

public class VoteDtoRequest {
    private String token;
    private final String candidateLogin;

    public VoteDtoRequest(String token, String candidateLogin) {
        this.token = token;
        this.candidateLogin = candidateLogin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCandidateLogin() {
        return candidateLogin;
    }

    public boolean requiredFieldsIsNotNull() {
        return token != null;
    }
}