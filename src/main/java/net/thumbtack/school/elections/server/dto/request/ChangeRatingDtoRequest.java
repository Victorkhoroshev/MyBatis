package net.thumbtack.school.elections.server.dto.request;

public class ChangeRatingDtoRequest {
    private final String token;
    private final String ideaKey;
    private final int rating;

    public ChangeRatingDtoRequest(String token, String ideaKey, int rating) {
        this.token = token;
        this.ideaKey = ideaKey;
        this.rating = rating;
    }

    public String getIdeaKey() {
        return ideaKey;
    }

    public String getToken() {
        return token;
    }

    public int getRating() {
        return rating;
    }

}
