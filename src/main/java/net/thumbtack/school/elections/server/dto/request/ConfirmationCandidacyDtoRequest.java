package net.thumbtack.school.elections.server.dto.request;

import java.util.List;

public class ConfirmationCandidacyDtoRequest {
    private final String token;
    private final List<String> candidateIdeas;

    public ConfirmationCandidacyDtoRequest(String token, List<String> candidateIdeas) {
        this.token = token;
        this.candidateIdeas = candidateIdeas;
    }

    public String getToken() {
        return token;
    }

    public List<String> getCandidateIdeas() {
        return candidateIdeas;
    }

}
