package net.thumbtack.school.elections.server.dto.response;

public class SetIsElectionStartDtoResponse {
    private boolean isElectionStart;

    public SetIsElectionStartDtoResponse(boolean isElectionStart) {
        this.isElectionStart = isElectionStart;
    }

    public boolean isElectionStart() {
        return isElectionStart;
    }
}