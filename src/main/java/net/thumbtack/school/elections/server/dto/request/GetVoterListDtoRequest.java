package net.thumbtack.school.elections.server.dto.request;

public class GetVoterListDtoRequest {
    private final String token;

    public GetVoterListDtoRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
