package net.thumbtack.school.elections.server.service;
import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dto.request.SetIsElectionStartDtoRequest;
import net.thumbtack.school.elections.server.dto.request.SetIsElectionStopDtoRequest;
import net.thumbtack.school.elections.server.dto.response.SetIsElectionStopDtoResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContextServiceTest {
    private final Gson gson = new Gson();
    private final ContextService contextService = new ContextService();

    @Test
    public void isElectionStartTest_False() {
       assertFalse(contextService.isElectionStop());
    }

    @Test
    public void setIsElectionStartTest_Success() {
        contextService.setIsElectionStart(gson.toJson(new SetIsElectionStartDtoRequest(true)));
        assertTrue(contextService.getContext().getElectionStart());
    }

    @Test
    public void setIsElectionStopTest_Success() {
        assertEquals(gson.toJson(new SetIsElectionStopDtoResponse(true)),
                contextService.setIsElectionStop(gson.toJson(new SetIsElectionStopDtoRequest(true))));
        assertTrue(contextService.getContext().getElectionStop());
    }
}