package net.thumbtack.school.elections.server.dto.request;

public class AddIdeaDtoRequest {
    private final String idea;
    private final String token;

    public AddIdeaDtoRequest(String idea, String token) {
        this.idea = idea;
        this.token = token;
    }

    public String getIdea() {
        return idea;
    }

    public String getToken() {
        return token;
    }

}
