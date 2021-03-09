package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dao.CommissionerDao;
import net.thumbtack.school.elections.server.daoimpl.CommissionerDaoImpl;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.ErrorDtoResponse;
import net.thumbtack.school.elections.server.dto.response.GetCommissionerDtoResponse;
import net.thumbtack.school.elections.server.dto.response.GetElectionResultDtoResponse;
import net.thumbtack.school.elections.server.model.Commissioner;

import java.util.List;

public class CommissionerService {
    private final CommissionerDao dao;
    private final SessionService sessionService;
    private final ElectionService electionService;
    private final ContextService contextService;
    private final Validation validation;
    private final Gson gson;
    private final CandidateService candidateService;

    private static final String NULL_VALUE = "Некорректный запрос.";

    public CommissionerService(SessionService sessionService, ElectionService electionService, ContextService contextService, Gson gson, CandidateService candidateService) {
        this.sessionService = sessionService;
        this.electionService = electionService;
        this.contextService = contextService;
        this.gson = gson;
        this.candidateService = candidateService;
        dao = new CommissionerDaoImpl();
        validation = new Validation();
    }

    /**
     * Login commissioner.
     * @param login the login voter, who already logged out from the server.
     * @param password the password commissioner, who already logged out from the server.
     * @return Unique commissioner's id.
     * @throws ServerException if login not found or password incorrect for login or election start.
     */
    public String login(String requestJsonString) {
        if (contextService.isElectionStart()) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage()));
        }
        LoginDtoRequest request = gson.fromJson(requestJsonString, LoginDtoRequest.class);
        try {
            validation.validate(request.getLogin(), request.getPassword());
            Commissioner commissioner = dao.get(request.getLogin());
            if (commissioner == null) {
                return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NOT_FOUND.getMessage()));
            }
            if (!commissioner.getPassword().equals(request.getPassword())) {
                return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.WRONG_PASSWORD.getMessage()));
            }
            return sessionService.loginCommissioner(gson.toJson(new LoginCommissionerDtoRequest(commissioner)));
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
    }

    /**
     * Commissioner verification.
     * @param token unique commissioner's id.
     * @return If commissioner's session exist: true.
     * If commissioner's session not exist: false.
     */
    public boolean isCommissioner(String requestJsonString) {
        IsCommissionerDtoRequest request = gson.fromJson(requestJsonString, IsCommissionerDtoRequest.class);
        return dao.contain(request.getLogin());
    }

    /**
     * Logout commissioner.
     * @param token unique commissioner's id.
     */
    public String logout(String requestJsonString) {
        LogoutDtoRequest request = gson.fromJson(requestJsonString, LogoutDtoRequest.class);
        try {
            validation.validate(request.getToken());
            return sessionService.logoutCommissioner(requestJsonString);
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
    }

    /**
     * Start election.
     * @param token commissioner's unique id.
     * @param candidateSet set candidates, who confirmed their candidacy.
     * @throws ServerException if the token does not belong to chairman.
     */
    public String startElection(String requestJsonString) {
        StartElectionDtoRequest request = gson.fromJson(requestJsonString, StartElectionDtoRequest.class);
        try {
            validation.validate(request.getToken());
            GetCommissionerDtoResponse getCommissionerDtoResponse = gson.fromJson(sessionService.getCommissioner(gson.toJson(
                    new GetCommissionerDtoRequest(request.getToken()))), GetCommissionerDtoResponse.class);
            if (!getCommissionerDtoResponse.getCommissioner().isChairman()) {
                return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NOT_CHAIRMAN.getMessage()));
            }
            return  electionService.startElection(gson.toJson(
                    new CommissionerStartElectionDtoRequest(candidateService.getCandidateSet())));
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
    }

    /**
     * Get election result.
     * If result contains only one candidate, than election stop.
     * @param token commissioner's unique id.
     * @return Candidates set.
     * @throws ServerException if the token does not belong to chairman.
     */
    public String getElectionResult(String requestJsonString) {
        GetElectionResultDtoRequest request = gson.fromJson(requestJsonString, GetElectionResultDtoRequest.class);
        try {
            validation.validate(request.getToken());
            GetCommissionerDtoResponse getCommissionerDtoResponse = gson.fromJson(sessionService.getCommissioner(gson.toJson(
                    new GetCommissionerDtoRequest(request.getToken()))), GetCommissionerDtoResponse.class);
            if (!getCommissionerDtoResponse.getCommissioner().isChairman()) {
                return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NOT_CHAIRMAN.getMessage()));
            }
            return electionService.getElectionResult();
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public ElectionService getElectionService() {
        return electionService;
    }

    public ContextService getContextService() {
        return contextService;
    }
}