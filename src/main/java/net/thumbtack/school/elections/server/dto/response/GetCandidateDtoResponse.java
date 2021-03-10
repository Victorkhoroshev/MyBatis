package net.thumbtack.school.elections.server.dto.response;

import net.thumbtack.school.elections.server.model.Candidate;

public class GetCandidateDtoResponse {
    private final Candidate candidate;

    public GetCandidateDtoResponse(Candidate candidate) {
        this.candidate = candidate;
    }

    public Candidate getCandidate() {
        return candidate;
    }
}