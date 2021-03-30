package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.*;
import net.thumbtack.school.elections.server.exeption.ExceptionErrorCode;
import net.thumbtack.school.elections.server.exeption.ServerException;
import net.thumbtack.school.elections.server.model.Commissioner;
import net.thumbtack.school.elections.server.model.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CommissionerServiceTest {
    @Mock
    private SessionService sessionService;
    @Mock
    private ElectionService electionService;
    @Mock
    private ContextService contextService;
    @Mock
    private CandidateService candidateService;

    private final Gson gson = new Gson();
    @InjectMocks
    private CommissionerService commissionerService;

    public CommissionerServiceTest() {
        MockitoAnnotations.initMocks(this);
        Map<String, Commissioner> commissionerMap = new HashMap<>();
        commissionerMap.put("victor.net", new Commissioner("victor.net", "25345Qw&&", true));
        commissionerMap.put("egor.net", new Commissioner("egor.net", "25345Qw&&", false));
        commissionerMap.put("igor.net", new Commissioner("igor.net", "25345Qw&&", false));
        Database.getInstance().setCommissionerMap(commissionerMap);
        Database.getInstance().getCommissionerSessions()
                .put(new Commissioner("victor.net", "25345Qw&&", true), new Session("1"));
        Database.getInstance().getCommissionerSessions()
                .put(new Commissioner("egor.net", "25345Qw&&", false), new Session("2"));
    }

    @Test
    public void loginTest_Success() {
        when(commissionerService.getContextService().isElectionStart()).thenReturn(false);
        when(commissionerService.getSessionService().loginCommissioner(anyString())).thenReturn(
                gson.toJson(new LoginCommissionerDtoResponse("1")));
        assertEquals(gson.toJson(new LoginCommissionerDtoResponse("1")),
                commissionerService.login(gson.toJson(new LoginDtoRequest("victor.net", "25345Qw&&"))));
    }

    @Test
    public void loginTest_Field_Not_Valid() {
        when(commissionerService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGIN_NOT_VALID.getMessage())),
                commissionerService.login(gson.toJson(new LoginDtoRequest("net", "25345Qw&&"))));
    }

    @Test
    public void loginTest_Not_Found() {
        when(commissionerService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NOT_FOUND.getMessage())),
                commissionerService.login(gson.toJson(new LoginDtoRequest("1111111111", "25345Qw&&"))));
    }

    @Test
    public void loginTest_Pass_Not_Valid() {
        when(commissionerService.getContextService().isElectionStart()).thenReturn(false);
        when(commissionerService.getSessionService().loginCommissioner(anyString())).thenReturn(
                gson.toJson(new LoginCommissionerDtoResponse("1")));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.WRONG_PASSWORD.getMessage())),
                commissionerService.login(gson.toJson(new LoginDtoRequest("victor.net", "25345Q1@q2423"))));
    }

    @Test
    public void loginTest_Json_Is_Null() {
        when(commissionerService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                commissionerService.login(null));
    }

    @Test
    public void loginTest_Election_Already_Start() {
        when(commissionerService.getContextService().isElectionStart()).thenReturn(true);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage())),
                commissionerService.login(randomString()));
    }

    @Test
    public void isCommissionerTest_Success() {
        assertTrue(commissionerService.isCommissioner(gson.toJson(new IsCommissionerDtoRequest("victor.net"))));
    }

    @Test
    public void logoutTest_Success() {
        when(commissionerService.getSessionService().logoutCommissioner(anyString())).thenReturn("");
        assertEquals("", commissionerService.logout(gson.toJson(new LogoutDtoRequest("1"))));
        verify(commissionerService.getSessionService(), times(1)).logoutCommissioner(anyString());
    }

    @Test
    public void logoutTest_Field_Not_Valid() {
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                commissionerService.logout(gson.toJson(new LogoutDtoRequest(null))));
        verify(commissionerService.getSessionService(), times(0)).logoutCommissioner(anyString());
    }

    @Test
    public void logoutTest_Json_Is_Null() {
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                commissionerService.logout(null));
        verify(commissionerService.getSessionService(), times(0)).logoutCommissioner(anyString());
    }

    @Test
    public void startElectionTest_Success() {
        when(commissionerService.getElectionService().startElection(anyString())).thenReturn("");
        assertEquals("", commissionerService.startElection(gson.toJson(new StartElectionDtoRequest("1"))));
        verify(commissionerService.getElectionService(), times(1)).startElection(anyString());
    }

    @Test
    public void startElectionTest_Not_Chairman() {
        when(commissionerService.getElectionService().startElection(anyString())).thenReturn("");
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NOT_CHAIRMAN.getMessage())),
                commissionerService.startElection(gson.toJson(new StartElectionDtoRequest("2"))));
        verify(commissionerService.getElectionService(), times(0)).startElection(anyString());
    }

    @Test
    public void startElectionTest_Field_Not_Valid() {
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                commissionerService.startElection(gson.toJson(new StartElectionDtoRequest(null))));
        verify(commissionerService.getElectionService(), times(0)).startElection(anyString());
    }

    @Test
    public void startElectionTest_Json_Is_Null() {
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                commissionerService.startElection(null));
        verify(commissionerService.getElectionService(), times(0)).startElection(anyString());
    }

    @Test
    public void getElectionResultTest_Success() {
        when(commissionerService.getElectionService().getElectionResult()).thenReturn(
                gson.toJson(new GetElectionResultDtoResponse(new HashSet<>())));
        assertEquals(gson.toJson(new GetElectionResultDtoResponse(new HashSet<>())),
                commissionerService.getElectionResult(gson.toJson(new GetElectionResultDtoRequest("1"))));
        verify(commissionerService.getElectionService(), times(1)).getElectionResult();
    }

    @Test
    public void getElectionResultTest_Not_Chairman() {
        when(commissionerService.getElectionService().getElectionResult()).thenReturn(
                gson.toJson(new GetElectionResultDtoResponse(new HashSet<>())));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NOT_CHAIRMAN.getMessage())),
                commissionerService.getElectionResult(gson.toJson(new GetElectionResultDtoRequest("2"))));
        verify(commissionerService.getElectionService(), times(0)).getElectionResult();
    }

    @Test
    public void getElectionResultTest_Field_Not_Valid() {
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                commissionerService.getElectionResult(gson.toJson(new GetElectionResultDtoRequest(null))));
        verify(commissionerService.getElectionService(), times(0)).getElectionResult();
    }

    @Test
    public void getElectionResultTest_Json_Is_Null() {
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                commissionerService.getElectionResult(null));
        verify(commissionerService.getElectionService(), times(0)).getElectionResult();
    }

    private String randomString() {
        Random random = new Random();
        char[] sAlphabet = "АБВГДЕЖЗИКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзиклмнопрстуфхцчшщъыьэюя".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 60; i++) {
            stringBuilder.append(sAlphabet[random.nextInt(sAlphabet.length)]);
        }
        return stringBuilder.toString();
    }
}