package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.*;
import net.thumbtack.school.elections.server.model.Commissioner;
import net.thumbtack.school.elections.server.model.Voter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionService {
    private final Map<Voter, Session> voterSessions;
    private final Map<Commissioner, Session> commissionerSessions;
    private final Gson gson;

    private static final String EMPTY_JSON = "";

    public SessionService(Gson gson) {
        this.gson = gson;
        voterSessions = new HashMap<>();
        commissionerSessions = new HashMap<>();
    }

    /**
     * Get voter session.
     * @param requestJsonString gson element with fields: Voter voter, from whom we want to get a session.
     * @return Voter session.
     */
    public String getVoterSession(String requestJsonString) {
        GetVoterSessionDtoRequest request = gson.fromJson(requestJsonString, GetVoterSessionDtoRequest.class);
        if (!voterSessions.containsKey(request.getVoter())) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage()));
        }
        return gson.toJson(new GetVoterSessionDtoResponse(voterSessions.get(request.getVoter())));
    }

    /**
     * Get commissioner session.
     * @param requestJsonString gson element with fields: Commissioner commissioner, from whom we want to get a session.
     * @return Commissioner's session.
     */
    public String getCommissionerSession(String requestJsonString) {
        GetCommissionerSessionDtoRequest request = gson.fromJson(requestJsonString,
                GetCommissionerSessionDtoRequest.class);
        if (!commissionerSessions.containsKey(request.getCommissioner())) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage()));
        }
        return gson.toJson(new GetCommissionerSessionDtoResponse(commissionerSessions.get(request.getCommissioner())));
    }

    /**
     * Get voter by token.
     * @param requestJsonString gson element with fields: String token (unique voter's id).
     * @return Voter who owns this token.
     * @throws ServerException if voter logout.
     */
    public String getVoter(String requestJsonString) throws ServerException {
        GetVoterDtoRequest request = gson.fromJson(requestJsonString, GetVoterDtoRequest.class);
        for (Map.Entry<Voter, Session> entry : voterSessions.entrySet()) {
            if (entry.getValue().getToken().equals(request.getToken())) {
                return gson.toJson(new GetVoterDtoResponse(entry.getKey()));
            }
        }
        throw new ServerException(ExceptionErrorCode.LOGOUT);
    }

    /**
     * Get commissioner by token.
     * @param requestJsonString gson element with fields: String token (unique commissioner's id).
     * @return If commissioner login: commissioner who owns this token.
     * If commissioner logout: null.
     */
    public String getCommissioner(String requestJsonString) throws ServerException {
        GetCommissionerDtoRequest request = gson.fromJson(requestJsonString, GetCommissionerDtoRequest.class);
        for (Map.Entry<Commissioner, Session> entry : commissionerSessions.entrySet()) {
            if (entry.getValue().getToken().equals(request.getToken())) {
                return gson.toJson(new GetCommissionerDtoResponse(entry.getKey()));
            }
        }
        throw new ServerException(ExceptionErrorCode.LOGOUT);
    }

    /**
     * Checks: login voter or commissioner.
     * @param requestJsonString gson element with fields: String token (unique user id).
     * @return If voter or commissioner login: true.
     * If voter or commissioner logout: false.
     */
    public String isLogin(String requestJsonString) {
        IsLoginDtoRequest request = gson.fromJson(requestJsonString, IsLoginDtoRequest.class);
        for (Map.Entry<Voter, Session> entry : voterSessions.entrySet()) {
            if (entry.getValue().getToken().equals(request.getToken())) {
                return gson.toJson(new IsLoginDtoResponse(true));
            }
        }
        for (Map.Entry<Commissioner, Session> entry : commissionerSessions.entrySet()) {
            if (entry.getValue().getToken().equals(request.getToken())) {
                return gson.toJson(new IsLoginDtoResponse(true));
            }
        }
        return gson.toJson(new IsLoginDtoResponse(false));
    }

    /**
     * Put voter in voter's map and his generated unique id.
     * @param requestJsonString gson element with fields: Voter voter who hasn't logged in yet.
     * @return generated unique id.
     */
    public String loginVoter(String requestJsonString) {
        LoginVoterDtoRequest request = gson.fromJson(requestJsonString, LoginVoterDtoRequest.class);
        String token = UUID.randomUUID().toString();
        voterSessions.put(request.getVoter(), new Session(token));
        return gson.toJson(new LoginVoterDtoResponse(token));
    }

    /**
     * Put commissioner in commissioner's map and his generated unique id.
     * @param requestJsonString gson element with fields: Commissioner commissioner who hasn't logged in yet.
     * @return Generated unique id.
     */
    public String loginCommissioner(String requestJsonString) {
        LoginCommissionerDtoRequest request = gson.fromJson(requestJsonString, LoginCommissionerDtoRequest.class);
        String token = UUID.randomUUID().toString();
        commissionerSessions.put(request.getCommissioner(), new Session(token));
        return gson.toJson(new LoginCommissionerDtoResponse(token));
    }

    /**
     * Remove from voter's map voter.
     * @param requestJsonString gson element with fields: String token (unique voter's id).
     * @throws ServerException if voter logout.
     */
    public String logoutVoter(String requestJsonString) throws ServerException {
        LogoutDtoRequest request = gson.fromJson(requestJsonString, LogoutDtoRequest.class);
        GetVoterDtoResponse getVoterDtoResponse = gson.fromJson(getVoter(gson.toJson(
                new GetVoterDtoRequest(request.getToken()))), GetVoterDtoResponse.class);
        voterSessions.remove(getVoterDtoResponse.getVoter());
        return EMPTY_JSON;
    }

    /**
     * Remove commissioner from commissioner's map.
     * @param requestJsonString gson element with fields: String token (unique commissioner's id).
     */
    public String logoutCommissioner(String requestJsonString) throws ServerException {
        LogoutDtoRequest request = gson.fromJson(requestJsonString, LogoutDtoRequest.class);
        GetCommissionerDtoResponse getCommissionerDtoResponse = gson.fromJson(getCommissioner(gson.toJson(
                new GetCommissionerDtoRequest(request.getToken()))), GetCommissionerDtoResponse.class);
        commissionerSessions.remove(getCommissionerDtoResponse.getCommissioner());
        return EMPTY_JSON;
    }

    public Map<Voter, Session> getVoterSessions() {
        return voterSessions;
    }

    public Map<Commissioner, Session> getCommissionerSessions() {
        return commissionerSessions;
    }
}