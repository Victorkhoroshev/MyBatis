package net.thumbtack.school.elections.server.dto.request;

public class RemoveIdeaDtoRequest {
    private final String token;
    private final String ideaKey;

    public RemoveIdeaDtoRequest(String token, String ideaKey) {
        this.token = token;
        this.ideaKey = ideaKey;
    }

    public String getToken() {
        return token;
    }

    public String getIdeaKey() {
        return ideaKey;
    }
}
