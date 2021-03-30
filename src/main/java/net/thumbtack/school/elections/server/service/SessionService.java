package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dao.SessionDao;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.*;
import net.thumbtack.school.elections.server.exeption.ServerException;
import net.thumbtack.school.elections.server.model.Session;
import java.util.UUID;

public class SessionService {
    private final Gson gson;
    private  SessionDao dao;

    private static final String EMPTY_JSON = "";

    public SessionService(SessionDao dao) {
        this.dao = dao;
        gson = new Gson();
    }

    /**
     * Get voter session.
     * @param requestJsonString gson element with fields: Voter voter, from whom we want to get a session.
     * @return Gson element with voter's session.
     */
    public String getVoterSession(String requestJsonString) throws ServerException {
        GetVoterSessionDtoRequest request = gson.fromJson(requestJsonString, GetVoterSessionDtoRequest.class);
        return gson.toJson(new GetVoterSessionDtoResponse(dao.getVoterSession(request.getVoter())));
    }

    /**
     * Get commissioner session.
     * @param requestJsonString gson element with fields: Commissioner commissioner, from whom we want to get a session.
     * @return Gson element with commissioner's session.
     */
    public String getCommissionerSession(String requestJsonString) throws ServerException {
        GetCommissionerSessionDtoRequest request = gson.fromJson(requestJsonString,
                GetCommissionerSessionDtoRequest.class);
        return gson.toJson(new GetCommissionerSessionDtoResponse(dao.getCommissionerSession(request.getCommissioner())));
    }

    /**
     * Checks: login voter or commissioner.
     * @param requestJsonString gson element with fields: String token (unique user id).
     * @return If voter or commissioner login: gson element with field: boolean true.
     * If voter or commissioner logout: gson element with field: boolean false.
     */
    public String isLogin(String requestJsonString) {
        IsLoginDtoRequest request = gson.fromJson(requestJsonString, IsLoginDtoRequest.class);
        return gson.toJson(new IsLoginDtoResponse(dao.isLogin(request.getToken())));
    }

    /**
     * Put voter and his generated session into database.
     * @param requestJsonString gson element with fields: Voter voter who hasn't logged in yet.
     * @return gson element with generated unique id.
     */
    public String loginVoter(String requestJsonString) {
        LoginVoterDtoRequest request = gson.fromJson(requestJsonString, LoginVoterDtoRequest.class);
        return gson.toJson(new LoginVoterDtoResponse(dao.loginVoter(request.getVoter(),
                new Session(UUID.randomUUID().toString())).getToken()));
    }

    /**
     * Put commissione rand his generated session into database.
     * @param requestJsonString gson element with fields: Commissioner commissioner who hasn't logged in yet.
     * @return gson element with generated unique id.
     */
    public String loginCommissioner(String requestJsonString) {
        LoginCommissionerDtoRequest request = gson.fromJson(requestJsonString, LoginCommissionerDtoRequest.class);
        return gson.toJson(new LoginCommissionerDtoResponse(dao.loginCommissioner(request.getCommissioner(),
                new Session(UUID.randomUUID().toString())).getToken()));
    }

    /**
     * Remove voter's session from database.
     * @param requestJsonString gson element with fields: Voter voter.
     */
    public String logoutVoter(String requestJsonString) {
        LogoutVoterDtoRequest request = gson.fromJson(requestJsonString, LogoutVoterDtoRequest.class);
        dao.logoutVoter(request.getVoter());
        return EMPTY_JSON;
    }

    /**
     * Remove commissioner's session from database.
     * @param requestJsonString gson element with fields: Commissioner commissioner.
     */
    public String logoutCommissioner(String requestJsonString) {
        LogoutCommissionerDtoRequest request = gson.fromJson(requestJsonString, LogoutCommissionerDtoRequest.class);
        dao.logoutCommissioner(request.getCommissioner());
        return EMPTY_JSON;
    }

    public SessionDao getDao() {
        return dao;
    }
}