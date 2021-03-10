package net.thumbtack.school.elections.server.dto.request;

public class SetIsElectionStartDtoRequest {
    private final boolean isElectionStart;

    public SetIsElectionStartDtoRequest(boolean isElectionStart) {
        this.isElectionStart = isElectionStart;
    }

    public boolean isElectionStart() {
        return isElectionStart;
    }
}