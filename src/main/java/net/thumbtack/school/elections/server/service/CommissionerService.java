package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dao.CommissionerDao;
import net.thumbtack.school.elections.server.daoimpl.CommissionerDaoImpl;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.ErrorDtoResponse;
import net.thumbtack.school.elections.server.dto.response.GetCommissionerDtoResponse;
import net.thumbtack.school.elections.server.model.Commissioner;

public class CommissionerService {
    private final CommissionerDao dao;
    private final SessionService sessionService;
    private final ElectionService electionService;
    private final ContextService contextService;
    private final Validation validation;
    private final Gson gson;
    private final CandidateService candidateService;

    private static final String NULL_VALUE = "Некорректный запрос.";

    public CommissionerService(SessionService sessionService, ElectionService electionService,
                               ContextService contextService, Gson gson, CandidateService candidateService) {
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
     * @param requestJsonString gson element. Fields: String login, String password.
     * @return If all fields is valid and if the method has not caught any exception: gson element with generated
     * unique commissioner's id.
     * If some field or request is not null: gson element with field: String error:
     * "Пожалуйста, введите логин и пароль.".
     * If login/password in not valid: gson element with field: String error:(some problem).
     * If pass is not correct: gson element with field: String error: "Неверный пароль.".
     * If election already start: gson element with field: String error: "Выборы уже проходят, действие невозможно.".
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
     * @param requestJsonString requestJsonString gson element with field: String token (commissioner's unique id).
     * @return If commissioner's session exist: true.
     * If commissioner's session not exist: false.
     */
    public boolean isCommissioner(String requestJsonString) {
        IsCommissionerDtoRequest request = gson.fromJson(requestJsonString, IsCommissionerDtoRequest.class);
        return dao.contain(request.getLogin());
    }

    /**
     * Logout commissioner.
     * If requestJsonString contain token, owned candidate, checks: is the candidacy confirmed.
     * Set all User's ideas community(null)
     * @param requestJsonString gson element with field: String token commissioner's unique id.
     * @return If field is valid and if the method has not caught any exception: empty gson element.
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If voter already logout: gson element with field: String error: "Сессия пользователя не найдена.".
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
     * Commissioner start election.
     * @param requestJsonString gson element with field: String token(commissioner's unique id).
     * @return If field is valid and if the method has not caught any exception: empty gson element.
     * If the token does not belong to chairman: gson element with field: String error: "Вы не председатель коммиссии.".
     * If field is not valid: gson element with field: String error: "Некорректный запрос.".
     */
    public String startElection(String requestJsonString) {
        StartElectionDtoRequest request = gson.fromJson(requestJsonString, StartElectionDtoRequest.class);
        try {
            validation.validate(request.getToken());
            GetCommissionerDtoResponse getCommissionerDtoResponse = gson.fromJson(sessionService.getCommissioner(
                    gson.toJson(new GetCommissionerDtoRequest(request.getToken()))), GetCommissionerDtoResponse.class);
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
     * Commissioner get election result.
     * @param requestJsonString gson element with field: String token(commissioner's unique id).
     * @return If field is valid and if the method has not caught any exception: gson element with field:
     * Set<Candidate> candidateSet(candidates set).
     * If the token does not belong to chairman: gson element with field: String error: "Вы не председатель коммиссии.".
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     */
    public String getElectionResult(String requestJsonString) {
        GetElectionResultDtoRequest request = gson.fromJson(requestJsonString, GetElectionResultDtoRequest.class);
        try {
            validation.validate(request.getToken());
            GetCommissionerDtoResponse getCommissionerDtoResponse = gson.fromJson(sessionService.getCommissioner(
                    gson.toJson(new GetCommissionerDtoRequest(request.getToken()))), GetCommissionerDtoResponse.class);
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