package net.thumbtack.school.elections.server.dto.request;

public class RemoveRatingDtoRequest {
    private final String token;
    private final String ideaKey;

    public RemoveRatingDtoRequest(String token, String ideaKey) {
        this.token = token;
        this.ideaKey = ideaKey;
    }

    public String getIdeaKey() {
        return ideaKey;
    }

    public String getToken() {
        return token;
    }
}
