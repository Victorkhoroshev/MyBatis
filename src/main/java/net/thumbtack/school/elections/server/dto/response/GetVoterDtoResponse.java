package net.thumbtack.school.elections.server.dto.response;

import net.thumbtack.school.elections.server.model.Voter;

public class GetVoterDtoResponse {
    private final Voter voter;

    public GetVoterDtoResponse(Voter voter) {
        this.voter = voter;
    }

    public Voter getVoter() {
        return voter;
    }
}