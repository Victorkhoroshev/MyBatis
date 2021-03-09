package net.thumbtack.school.elections.server.dto.response;

import net.thumbtack.school.elections.server.model.Idea;
import java.util.List;

public class GetAllVotersIdeasDtoResponse {
    private final List<Idea> ideas;

    public GetAllVotersIdeasDtoResponse(List<Idea> ideas) {
        this.ideas = ideas;
    }

    public List<Idea> getIdeas() {
        return ideas;
    }
}
