package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dao.SessionDao;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.*;
import net.thumbtack.school.elections.server.exeption.ExceptionErrorCode;
import net.thumbtack.school.elections.server.exeption.ServerException;
import net.thumbtack.school.elections.server.model.Commissioner;
import net.thumbtack.school.elections.server.model.Session;
import net.thumbtack.school.elections.server.model.Voter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SessionServiceTest {
    private final Gson gson = new Gson();
    @Mock
    private SessionDao dao;
    @InjectMocks
    private SessionService sessionService;

    @Test
    public void getVoterSessionTest_Success() throws ServerException {
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        when(sessionService.getDao().getVoterSession(any())).thenReturn(new Session("1"));
        assertEquals(gson.toJson(new GetVoterSessionDtoResponse(new Session("1"))),
                sessionService.getVoterSession(gson.toJson(new GetVoterSessionDtoRequest(voter))));
    }

    @Test
    public void getVoterSessionTest_Logout() throws ServerException {
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        when(sessionService.getDao().getVoterSession(any())).thenThrow(new ServerException(ExceptionErrorCode.LOGOUT));
        try {
            sessionService.getVoterSession(gson.toJson(new GetVoterSessionDtoRequest(voter)));
        } catch (ServerException ex) {
            assertEquals(ex.getErrorCode(), ExceptionErrorCode.LOGOUT);
        }
    }

    @Test
    public void getCommissionerSessionTest_Success() throws ServerException {
        Commissioner commissioner = new Commissioner("login", "password", true);
        when(sessionService.getDao().getCommissionerSession(any())).thenThrow(new ServerException(ExceptionErrorCode.LOGOUT));
        try {
            sessionService.getCommissionerSession(gson.toJson(new GetCommissionerSessionDtoRequest(commissioner)));
        } catch (ServerException ex) {
            assertEquals(ex.getErrorCode(), ExceptionErrorCode.LOGOUT);
        }
    }

    @Test
    public void getCommissionerSessionTest_Logout() {
        Commissioner commissioner = new Commissioner("login", "password", true);
        try {
            sessionService.getCommissionerSession(gson.toJson(new GetCommissionerSessionDtoRequest(commissioner)));
        } catch (ServerException ex) {
            assertEquals(ex.getErrorCode(), ExceptionErrorCode.LOGOUT);
        }
    }

    @Test
    public void loginVoterTest_Success() {
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        when(sessionService.getDao().loginVoter(any(), any())).thenReturn(new Session("1"));
        assertEquals(gson.toJson(new LoginVoterDtoResponse("1")),
                sessionService.loginVoter(gson.toJson(new LoginVoterDtoRequest(voter))));
    }

    @Test
    public void loginCommissionerTest_Success() {
        Commissioner commissioner = new Commissioner("login", "password", true);
        when(sessionService.getDao().loginCommissioner(any(), any())).thenReturn(new Session("1"));
        assertEquals(gson.toJson(new LoginCommissionerDtoResponse("1")),
                sessionService.loginCommissioner(gson.toJson(new LoginCommissionerDtoRequest(commissioner))));
    }

    @Test
    public void isLoginTest_Success() {
        when(sessionService.getDao().isLogin(anyString())).thenReturn(true);
        assertEquals(gson.toJson(new IsLoginDtoResponse(true)),
                sessionService.isLogin(gson.toJson(new IsLoginDtoRequest(randomString()))));
    }

    @Test
    public void isLoginTest_Logout() {
        when(sessionService.getDao().isLogin(anyString())).thenReturn(false);
        assertEquals(gson.toJson(new IsLoginDtoResponse(false)),
                sessionService.isLogin(gson.toJson(new IsLoginDtoRequest(randomString()))));
    }

    @Test
    public void logoutVoterTest_Success() {
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
       assertEquals("", sessionService.logoutVoter(gson.toJson(new LogoutVoterDtoRequest(voter))));
       verify(sessionService.getDao(), times(1)).logoutVoter(voter);
    }


    @Test
    public void logoutCommissionerTest_Success() {
        Commissioner commissioner = new Commissioner(randomString(), randomString(), true);
        assertEquals("", sessionService
                .logoutCommissioner(gson.toJson(new LogoutCommissionerDtoRequest(commissioner))));
        verify(sessionService.getDao(), times(1)).logoutCommissioner(commissioner);
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