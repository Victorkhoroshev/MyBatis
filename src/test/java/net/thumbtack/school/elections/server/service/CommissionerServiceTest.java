package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.*;
import net.thumbtack.school.elections.server.model.Commissioner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
    private final Gson gson;
    private final CommissionerService commissionerService;

    public CommissionerServiceTest() {
        MockitoAnnotations.initMocks(this);
        gson = new Gson();
        Set<Commissioner> commissionerSet = new HashSet<>();
        commissionerSet.add(new Commissioner("victor.net", "25345Qw&&", true));
        commissionerSet.add(new Commissioner("egor.net", "25345Qw&&", false));
        commissionerSet.add(new Commissioner("igor.net", "25345Qw&&", false));
        commissionerService = new CommissionerService(sessionService, electionService, contextService, gson,
                candidateService);
        Database.getInstance().setCommissionerSet(commissionerSet);
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
    public void logoutTest_Success() throws ServerException {
        when(commissionerService.getSessionService().logoutCommissioner(anyString())).thenReturn("");
        assertEquals("", commissionerService.logout(gson.toJson(new LogoutDtoRequest(randomString()))));
        verify(commissionerService.getSessionService(), times(1)).logoutCommissioner(anyString());
    }

    @Test
    public void logoutTest_Field_Not_Valid() throws ServerException {
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                commissionerService.logout(gson.toJson(new LogoutDtoRequest(null))));
        verify(commissionerService.getSessionService(), times(0)).logoutCommissioner(anyString());
    }

    @Test
    public void logoutTest_Json_Is_Null() throws ServerException {
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                commissionerService.logout(null));
        verify(commissionerService.getSessionService(), times(0)).logoutCommissioner(anyString());
    }

    @Test
    public void startElectionTest_Success() throws ServerException {
        Commissioner commissioner = new Commissioner("victor.net", "25345Qw&&", true);
        when(commissionerService.getSessionService().getCommissioner(anyString())).thenReturn(
                gson.toJson(new GetCommissionerDtoResponse(commissioner)));
        when(commissionerService.getElectionService().startElection(anyString())).thenReturn("");
        assertEquals("", commissionerService.startElection(gson.toJson(new StartElectionDtoRequest("1"))));
        verify(commissionerService.getElectionService(), times(1)).startElection(anyString());
    }

    @Test
    public void startElectionTest_Not_Chairman() throws ServerException {
        Commissioner commissioner = new Commissioner("victor.net", "25345Qw&&", false);
        when(commissionerService.getSessionService().getCommissioner(anyString())).thenReturn(
                gson.toJson(new GetCommissionerDtoResponse(commissioner)));
        when(commissionerService.getElectionService().startElection(anyString())).thenReturn("");
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NOT_CHAIRMAN.getMessage())),
                commissionerService.startElection(gson.toJson(new StartElectionDtoRequest(randomString()))));
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
    public void getElectionResultTest_Success() throws ServerException {
        Commissioner commissioner = new Commissioner("victor.net", "25345Qw&&", true);
        when(commissionerService.getSessionService().getCommissioner(anyString())).thenReturn(
                gson.toJson(new GetCommissionerDtoResponse(commissioner)));
        when(commissionerService.getElectionService().getElectionResult()).thenReturn(
                gson.toJson(new GetElectionResultDtoResponse(new HashSet<>())));
        assertEquals(gson.toJson(new GetElectionResultDtoResponse(new HashSet<>())),
                commissionerService.getElectionResult(gson.toJson(new GetElectionResultDtoRequest(randomString()))));
        verify(commissionerService.getElectionService(), times(1)).getElectionResult();
    }

    @Test
    public void getElectionResultTest_Not_Chairman() throws ServerException {
        Commissioner commissioner = new Commissioner("victor.net", "25345Qw&&", false);
        when(commissionerService.getSessionService().getCommissioner(anyString())).thenReturn(
                gson.toJson(new GetCommissionerDtoResponse(commissioner)));
        when(commissionerService.getElectionService().getElectionResult()).thenReturn(
                gson.toJson(new GetElectionResultDtoResponse(new HashSet<>())));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NOT_CHAIRMAN.getMessage())),
                commissionerService.getElectionResult(gson.toJson(new GetElectionResultDtoRequest(randomString()))));
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