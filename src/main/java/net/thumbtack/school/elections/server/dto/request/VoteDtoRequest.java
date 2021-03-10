package net.thumbtack.school.elections.server.dto.request;

public class VoteDtoRequest {
    private final String token;
    private final String candidateLogin;

    public VoteDtoRequest(String token, String candidateLogin) {
        this.token = token;
        this.candidateLogin = candidateLogin;
    }

    public String getToken() {
        return token;
    }

    public String getCandidateLogin() {
        return candidateLogin;
    }
}