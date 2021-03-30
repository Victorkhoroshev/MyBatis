package net.thumbtack.school.elections.server.dto.request;

import net.thumbtack.school.elections.server.model.Voter;

public class LogoutVoterDtoRequest {
    private final Voter voter;

    public LogoutVoterDtoRequest(Voter voter) {
        this.voter = voter;
    }

    public Voter getVoter() {
        return voter;
    }
}
