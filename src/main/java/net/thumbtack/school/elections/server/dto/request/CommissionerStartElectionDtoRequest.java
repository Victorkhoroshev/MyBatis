package net.thumbtack.school.elections.server.dto.request;

import net.thumbtack.school.elections.server.model.Candidate;
import java.util.Set;

public class CommissionerStartElectionDtoRequest {
    private final Set<Candidate> candidateSet;

    public CommissionerStartElectionDtoRequest(Set<Candidate> candidateSet) {
        this.candidateSet = candidateSet;
    }

    public Set<Candidate> getCandidateSet() {
        return candidateSet;
    }
}