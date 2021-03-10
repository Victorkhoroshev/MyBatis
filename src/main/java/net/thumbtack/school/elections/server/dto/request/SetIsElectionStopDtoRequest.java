package net.thumbtack.school.elections.server.dto.request;

public class SetIsElectionStopDtoRequest {
    private final boolean isElectionStop;

    public SetIsElectionStopDtoRequest(boolean isElectionStop) {
        this.isElectionStop = isElectionStop;
    }

    public boolean isElectionStop() {
        return isElectionStop;
    }
}