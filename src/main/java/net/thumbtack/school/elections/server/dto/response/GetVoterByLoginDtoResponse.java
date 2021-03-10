package net.thumbtack.school.elections.server.dto.response;

import net.thumbtack.school.elections.server.model.Voter;

public class GetVoterByLoginDtoResponse {
    private final Voter voter;

    public GetVoterByLoginDtoResponse(Voter voter) {
        this.voter = voter;
    }

    public Voter getVoter() {
        return voter;
    }
}