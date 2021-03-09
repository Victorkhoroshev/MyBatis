package net.thumbtack.school.elections.server.dto.request;

public class AddCandidateDtoRequest {
    private String candidateLogin;
    private String token;

    public AddCandidateDtoRequest(String token, String candidateLogin) {
        setCandidateLogin(candidateLogin);
        setToken(token);
    }

    public String getCandidateLogin() {
        return candidateLogin;
    }

    public void setCandidateLogin(String candidateLogin) {
        this.candidateLogin = candidateLogin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean requiredFieldsIsNotNull() {
        return candidateLogin != null && token != null;
    }
}