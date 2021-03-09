package net.thumbtack.school.elections.server.dto.response;

public class SetIsElectionStopDtoResponse {
    private boolean isElectionStopDtoResponse;

    public SetIsElectionStopDtoResponse(boolean isElectionStopDtoResponse) {
        this.isElectionStopDtoResponse = isElectionStopDtoResponse;
    }

    public boolean isElectionStopDtoResponse() {
        return isElectionStopDtoResponse;
    }
}