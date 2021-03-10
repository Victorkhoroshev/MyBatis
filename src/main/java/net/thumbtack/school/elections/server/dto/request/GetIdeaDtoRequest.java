package net.thumbtack.school.elections.server.dto.request;

public class GetIdeaDtoRequest {
    private final String ideaKey;

    public GetIdeaDtoRequest(String ideaKey) {
        this.ideaKey = ideaKey;
    }

    public String getIdeaKey() {
        return ideaKey;
    }

}