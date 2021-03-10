package net.thumbtack.school.elections.server.dto.request;

public class Session {
    private final String token;

    public Session(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
