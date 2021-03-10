package net.thumbtack.school.elections.server.dto.request;

import java.util.List;

public class GetAllVotersIdeasDtoRequest {
    private final String token;
    private final List<String> logins;

    public GetAllVotersIdeasDtoRequest(String token, List<String> logins) {
        this.token = token;
        this.logins = logins;
    }

    public String getToken() {
        return token;
    }

    public List<String> getLogins() {
        return logins;
    }

}
