package net.thumbtack.school.elections.server.dto.request;

import net.thumbtack.school.elections.server.model.Voter;

public class GetVoterSessionDtoRequest {
    private Voter voter;

    public GetVoterSessionDtoRequest(Voter voter) {
        this.voter = voter;
    }

    public Voter getVoter() {
        return voter;
    }
}