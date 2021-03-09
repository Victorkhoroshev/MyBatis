package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.*;
import net.thumbtack.school.elections.server.model.Voter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class VoterServiceTest {

    @Mock
    private SessionService sessionService;
    @Mock
    private ContextService contextService;
    @Mock
    private IdeaService ideaService;
    private final Gson gson = new Gson();
    private final VoterService voterService;

    public VoterServiceTest() {
        MockitoAnnotations.initMocks(this);
        voterService = new VoterService(sessionService, contextService, gson, ideaService);
    }

    @Test
    public void registerVoterTest_Success() throws ServerException {
        when(voterService.getContextService().isElectionStart()).thenReturn(false);
        when(voterService.getSessionService().loginVoter(anyString())).thenReturn(
                gson.toJson(new LoginVoterDtoResponse("1")));
        assertEquals(gson.toJson(new RegisterDtoResponse("1")) ,voterService.register(gson.toJson(
                new RegisterDtoRequest(randomString(), randomString(), null, randomString(),
                        1, 1, "login111111", "Pas&77123"))));
        verify(voterService.getSessionService(), times(1)).loginVoter(anyString());
        assertNotNull(voterService.getDao().get("login111111"));
    }

    @Test
    public void registerVoterTest_ElectionStart() throws ServerException {
        when(voterService.getContextService().isElectionStart()).thenReturn(true);
        assertEquals(gson.toJson(new ErrorDtoResponse("Выборы уже проходят, действие невозможно.")),
                voterService.register(gson.toJson(new RegisterDtoRequest(
                        randomString(), randomString(), null, randomString(),
                        1, 1, "login22222222", "Pas&77123"))));
        verify(voterService.getSessionService(), times(0)).loginVoter(anyString());
        assertNull(voterService.getDao().get("login22222222"));
    }

    @Test
    public void registerVoterTest_Field_Not_Valid() throws ServerException {
        when(voterService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(
                        "Имя должно быть на кириллице, без пробелов, спец. символов и цифр.")),
                voterService.register(gson.toJson(new RegisterDtoRequest("qweesaf", randomString(),
                        null, randomString(), 1, 1, "login33333",
                        "Pas&77123"))));
        verify(voterService.getSessionService(), times(0)).loginVoter(anyString());
        assertNull(voterService.getDao().get("login33333"));
    }

    @Test
    public void registerVoterTest_Request_Is_Null() {
        when(voterService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse("Некорректный запрос.")),
                voterService.register(gson.toJson(null)));
        verify(voterService.getSessionService(), times(0)).loginVoter(anyString());
    }

    @Test
    public void getAllTest_Success() {
        when(voterService.getSessionService().isLogin(anyString())).thenReturn(gson.toJson(new IsLoginDtoResponse(true)));
        assertEquals(gson.toJson(new GetVotersListDtoResponse(voterService.getDao().getAll())),
                voterService.getAll(gson.toJson(new GetVoterListDtoRequest(anyString()))));
    }

    @Test
    public void getAllTest_Voter_Logout() {
        when(voterService.getSessionService().isLogin(anyString())).thenReturn(gson.toJson(new IsLoginDtoResponse(false)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage())),
                voterService.getAll(gson.toJson(new GetVoterListDtoRequest(anyString()))));
    }

    @Test
    public void getAllTest_Request_Is_Null() {
        when(voterService.getSessionService().isLogin(anyString())).thenReturn(gson.toJson(new IsLoginDtoResponse(true)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                voterService.getAll(gson.toJson(null)));
    }

    @Test
    public void getAllTest_Request_Some_Field_Is_Null() {
        when(voterService.getSessionService().isLogin(anyString())).thenReturn(gson.toJson(new IsLoginDtoResponse(true)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                voterService.getAll(gson.toJson(new GetVoterListDtoRequest(null))));
    }

    @Test
    public void loginTest_Success() throws ServerException {
        when(voterService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, "login1111111", "Pas&77123");
        when(voterService.getSessionService().loginVoter(gson.toJson(new LoginVoterDtoRequest(voter)))).thenReturn(gson.toJson(new LoginVoterDtoResponse("1")));
        voterService.getDao().save(voter);
        assertEquals(gson.toJson(new LoginDtoResponse("1")), voterService.login(gson.toJson(new LoginDtoRequest("login1111111", "Pas&77123"))));
        verify(voterService.getSessionService(), times(1)).loginVoter(anyString());
    }

    @Test
    public void loginTest_Election_Start() throws ServerException {
        when(voterService.getContextService().isElectionStart()).thenReturn(true);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, "login1222222", "Pas&77123");
        voterService.getDao().save(voter);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage())), voterService.login(gson.toJson(new LoginDtoRequest("login1222222", "Pas&77123"))));
        verify(voterService.getSessionService(), times(0)).loginVoter(anyString());
    }

    @Test
    public void loginTest_Voter_Not_Register() {
        when(voterService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NOT_FOUND.getMessage())), voterService.login(gson.toJson(new LoginDtoRequest("login14444444", "Pas&77123"))));
        verify(voterService.getSessionService(), times(0)).loginVoter(anyString());
    }

    @Test
    public void loginTest_Pass_Not_Valid() throws ServerException {
        when(voterService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, "login1232222", "Pas&77123");
        voterService.getDao().save(voter);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.WRONG_PASSWORD.getMessage())), voterService.login(gson.toJson(new LoginDtoRequest("login1232222", "Pas&7sqwef3"))));
        verify(voterService.getSessionService(), times(0)).loginVoter(anyString());
    }

    @Test
    public void loginTest_Field_Not_Valid() throws ServerException {
        when(voterService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        voterService.getDao().save(voter);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.DATA_NOT_FIELD.getMessage())), voterService.login(gson.toJson(new LoginDtoRequest(null , "Pas&7sqwef3"))));
        verify(voterService.getSessionService(), times(0)).loginVoter(anyString());
    }

    @Test
    public void loginTest_Json_Not_Valid() throws ServerException {
        when(voterService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        voterService.getDao().save(voter);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())), voterService.login(null));
        verify(voterService.getSessionService(), times(0)).loginVoter(anyString());
    }

    @Test
    public void logoutTest_Success() throws ServerException {
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        when(voterService.getSessionService().getVoter(gson.toJson(new GetVoterDtoRequest(anyString())))).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(voterService.getSessionService().logoutVoter(gson.toJson(new LogoutDtoRequest(anyString())))).thenReturn("");
        assertEquals("", voterService.logout(gson.toJson(new LogoutDtoRequest(anyString()))));
        verify(voterService.getSessionService(), times(1)).logoutVoter(anyString());
        verify(voterService.getIdeaService(), times(1)).removeAllRating(anyString());
    }

    @Test
    public void logoutTest_Field_Not_Valid() throws ServerException {
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        when(voterService.getSessionService().getVoter(gson.toJson(new GetVoterDtoRequest(anyString())))).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())), voterService.logout(gson.toJson(new LogoutDtoRequest(null))));
        verify(voterService.getSessionService(), times(0)).logoutVoter(anyString());
        verify(voterService.getIdeaService(), times(0)).removeAllRating(anyString());
    }

    @Test
    public void logoutTest_Json_Not_Valid() throws ServerException {
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        when(voterService.getSessionService().getVoter(gson.toJson(new GetVoterDtoRequest(anyString())))).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())), voterService.logout(null));
        verify(voterService.getSessionService(), times(0)).logoutVoter(anyString());
        verify(voterService.getIdeaService(), times(0)).removeAllRating(anyString());
    }

    @Test
    public void getTest_Success() throws ServerException {
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, "login55555555", "Pas&77123");
        voterService.getDao().save(voter);
        assertEquals(gson.toJson(new GetVoterByLoginDtoResponse(voter)), voterService.get(gson.toJson(new GetVoterByLoginDtoRequest("login55555555"))));
    }

    @Test
    public void getTest_Not_Found() {
        try {
            voterService.get(gson.toJson(new GetVoterByLoginDtoRequest(randomString())));
        } catch (ServerException ex) {
            assertEquals(ExceptionErrorCode.NOT_FOUND, ex.getErrorCode());
        }
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