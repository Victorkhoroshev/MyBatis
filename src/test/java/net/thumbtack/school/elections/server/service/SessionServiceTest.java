package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.*;
import net.thumbtack.school.elections.server.model.Commissioner;
import net.thumbtack.school.elections.server.model.Voter;
import org.junit.jupiter.api.Test;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class SessionServiceTest {
    private Gson gson = new Gson();
    private SessionService sessionService = new SessionService(gson);

    @Test
    public void getVoterSessionTest_Success() {
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        sessionService.getVoterSessions().put(voter, new Session("1"));
        assertEquals(gson.toJson(new GetVoterSessionDtoResponse(new Session("1"))), sessionService.getVoterSession(gson.toJson(new GetVoterSessionDtoRequest(voter))));
    }

    @Test
    public void getVoterSessionTest_Logout() {
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage())), sessionService.getVoterSession(gson.toJson(new GetVoterSessionDtoRequest(voter))));
    }

    @Test
    public void getCommissionerSessionTest_Success() {
        Commissioner commissioner = new Commissioner("login", "password", true);
        sessionService.getCommissionerSessions().put(commissioner, new Session("1"));
        assertEquals(gson.toJson(new GetCommissionerSessionDtoResponse(new Session("1"))), sessionService.getCommissionerSession(gson.toJson(new GetCommissionerSessionDtoRequest(commissioner))));
    }

    @Test
    public void getCommissionerSessionTest_Logout() {
        Commissioner commissioner = new Commissioner("login", "password", true);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage())), sessionService.getCommissionerSession(gson.toJson(new GetCommissionerSessionDtoRequest(commissioner))));
    }

    @Test
    public void getVoterTest_Success() throws ServerException {
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        Voter voter2 = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        Voter voter3 = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        String token = gson.fromJson(sessionService.loginVoter(gson.toJson(new LoginVoterDtoRequest(voter))), LoginVoterDtoResponse.class).getToken();
        sessionService.loginVoter(gson.toJson(new LoginVoterDtoRequest(voter2)));
        sessionService.loginVoter(gson.toJson(new LoginVoterDtoRequest(voter3)));
        assertEquals(gson.toJson(new GetVoterDtoResponse(voter)), sessionService.getVoter(gson.toJson(new GetVoterDtoRequest(token))));
    }

    @Test
    public void getVoterTest_Logout() {
        try {
            sessionService.getVoter(gson.toJson(new GetVoterDtoRequest("token")));
        } catch (ServerException ex) {
            assertEquals(ExceptionErrorCode.LOGOUT, ex.getErrorCode());
        }
    }

    @Test
    public void getCommissionerTest_Success() throws ServerException {
        Commissioner commissioner1 = new Commissioner(randomString(), randomString(), true);
        Commissioner commissioner2 = new Commissioner(randomString(), randomString(), true);
        Commissioner commissioner3 = new Commissioner(randomString(), randomString(), true);
        String token = gson.fromJson(sessionService.loginCommissioner(gson.toJson(new LoginCommissionerDtoRequest(commissioner1))), LoginCommissionerDtoResponse.class).getToken();
        sessionService.loginCommissioner(gson.toJson(new LoginCommissionerDtoRequest(commissioner2)));
        sessionService.loginCommissioner(gson.toJson(new LoginCommissionerDtoRequest(commissioner3)));
        assertEquals(gson.toJson(new GetCommissionerDtoResponse(commissioner1)), sessionService.getCommissioner(gson.toJson(new GetCommissionerDtoRequest(token))));
    }
    @Test
    public void getCommissionerTest_Logout() {
        try {
            sessionService.getCommissioner(gson.toJson(new GetCommissionerDtoRequest(randomString())));
        } catch (ServerException ex) {
            assertEquals(ExceptionErrorCode.LOGOUT, ex.getErrorCode());
        }
    }

    @Test
    public void isLoginTest_Voter_Success() {
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        Voter voter2 = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        Voter voter3 = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        String token = gson.fromJson(sessionService.loginVoter(gson.toJson(new LoginVoterDtoRequest(voter))), LoginVoterDtoResponse.class).getToken();
        sessionService.loginVoter(gson.toJson(new LoginVoterDtoRequest(voter2)));
        sessionService.loginVoter(gson.toJson(new LoginVoterDtoRequest(voter3)));
        assertEquals(gson.toJson(new IsLoginDtoResponse(true)), sessionService.isLogin(gson.toJson(new IsLoginDtoRequest(token))));
    }

    @Test
    public void isLoginTest_Commissioner_Success() {
        Commissioner commissioner1 = new Commissioner(randomString(), randomString(), true);
        Commissioner commissioner2 = new Commissioner(randomString(), randomString(), true);
        Commissioner commissioner3 = new Commissioner(randomString(), randomString(), true);
        String token = gson.fromJson(sessionService.loginCommissioner(gson.toJson(new LoginCommissionerDtoRequest(commissioner1))), LoginCommissionerDtoResponse.class).getToken();
        sessionService.loginCommissioner(gson.toJson(new LoginCommissionerDtoRequest(commissioner2)));
        sessionService.loginCommissioner(gson.toJson(new LoginCommissionerDtoRequest(commissioner3)));
        assertEquals(gson.toJson(new IsLoginDtoResponse(true)), sessionService.isLogin(gson.toJson(new IsLoginDtoRequest(token))));
    }

    @Test
    public void isLoginTest_Logout() {
        assertEquals(gson.toJson(new IsLoginDtoResponse(false)), sessionService.isLogin(gson.toJson(new IsLoginDtoRequest(randomString()))));
    }

    @Test
    public void logoutVoterTest_Success() throws ServerException {
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        String token = gson.fromJson(sessionService.loginVoter(gson.toJson(new LoginVoterDtoRequest(voter))), LoginVoterDtoResponse.class).getToken();
        assertEquals("", sessionService.logoutVoter(gson.toJson(new LogoutDtoRequest(token))));
        assertFalse(sessionService.getVoterSessions().containsKey(voter));
    }

    @Test
    public void logoutVoterTest_Logout() {
        try {
            sessionService.logoutVoter(gson.toJson(new LogoutDtoRequest(randomString())));
        } catch (ServerException ex) {
            assertEquals(ExceptionErrorCode.LOGOUT, ex.getErrorCode());
        }
    }

    @Test
    public void logoutCommissionerTest_Success() throws ServerException {
        Commissioner commissioner = new Commissioner(randomString(), randomString(), true);
        String token = gson.fromJson(sessionService.loginCommissioner(gson.toJson(new LoginCommissionerDtoRequest(commissioner))), LoginCommissionerDtoResponse.class).getToken();
        assertEquals("", sessionService.logoutCommissioner(gson.toJson(new LogoutDtoRequest(token))));
        assertFalse(sessionService.getCommissionerSessions().containsKey(commissioner));
    }

    @Test
    public void logoutCommissionerTest_Logout() {
        try {
            sessionService.logoutCommissioner(gson.toJson(new LogoutDtoRequest(randomString())));
        } catch (ServerException ex) {
            assertEquals(ExceptionErrorCode.LOGOUT, ex.getErrorCode());
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