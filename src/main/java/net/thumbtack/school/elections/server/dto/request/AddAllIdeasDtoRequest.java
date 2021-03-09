package net.thumbtack.school.elections.server.dto.request;

import net.thumbtack.school.elections.server.model.Voter;

import java.util.List;

public class AddAllIdeasDtoRequest {
    private Voter voter;
    private List<String> ideas;

    public AddAllIdeasDtoRequest(Voter voter, List<String> ideas) {
        this.voter = voter;
        this.ideas = ideas;
    }

    public Voter getVoter() {
        return voter;
    }

    public List<String> getIdeas() {
        return ideas;
    }
}