package net.thumbtack.school.elections.server.dto.request;

import net.thumbtack.school.elections.server.model.Voter;

public class GetKeyDtoRequest {
    private Voter voter;
    private String text;

    public GetKeyDtoRequest(Voter voter, String text) {
        this.voter = voter;
        this.text = text;
    }

    public Voter getVoter() {
        return voter;
    }

    public String getText() {
        return text;
    }
}