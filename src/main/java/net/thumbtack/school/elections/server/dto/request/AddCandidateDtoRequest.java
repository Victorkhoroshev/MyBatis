package net.thumbtack.school.elections.server.dto.request;

public class AddCandidateDtoRequest {
    private final String candidateLogin;
    private final String token;

    public AddCandidateDtoRequest(String token, String candidateLogin) {
        this.candidateLogin = candidateLogin;
        this.token = token;
    }

    public String getCandidateLogin() {
        return candidateLogin;
    }

    public String getToken() {
        return token;
    }
}