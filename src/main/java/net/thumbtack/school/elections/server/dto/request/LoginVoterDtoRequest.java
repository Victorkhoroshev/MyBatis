package net.thumbtack.school.elections.server.dto.request;

import net.thumbtack.school.elections.server.model.Voter;

public class LoginVoterDtoRequest {
    private final Voter voter;

    public LoginVoterDtoRequest(Voter voter) {
        this.voter = voter;
    }

    public Voter getVoter() {
        return voter;
    }
}