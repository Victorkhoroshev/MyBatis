package net.thumbtack.school.elections.server.dto.response;

import net.thumbtack.school.elections.server.model.Idea;
import java.util.Objects;

public class GetIdeaDtoResponse {
    private Idea idea;

    public GetIdeaDtoResponse(Idea idea) {
        this.idea = idea;
    }

    public Idea getIdea() {
        return idea;
    }

}