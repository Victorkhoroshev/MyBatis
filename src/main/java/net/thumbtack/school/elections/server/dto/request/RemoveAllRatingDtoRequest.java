package net.thumbtack.school.elections.server.dto.request;

import net.thumbtack.school.elections.server.model.Voter;

public class RemoveAllRatingDtoRequest {
    private Voter voter;

    public RemoveAllRatingDtoRequest(Voter voter) {
        this.voter = voter;
    }

    public Voter getVoter() {
        return voter;
    }
}