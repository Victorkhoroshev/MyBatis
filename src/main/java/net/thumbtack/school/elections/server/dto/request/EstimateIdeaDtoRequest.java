package net.thumbtack.school.elections.server.dto.request;

public class EstimateIdeaDtoRequest {
    private final String ideaKey;
    private final int rating;
    private final String token;

    public EstimateIdeaDtoRequest(String ideaKey, int rating, String token) {
        this.ideaKey = ideaKey;
        this.rating = rating;
        this.token = token;
    }

    public int getRating() {
        return rating;
    }

    public String getToken() {
        return token;
    }

    public String getIdeaKey() {
        return ideaKey;
    }

}
