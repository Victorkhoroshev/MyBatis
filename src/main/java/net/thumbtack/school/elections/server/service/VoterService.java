package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dao.VoterDao;
import net.thumbtack.school.elections.server.daoimpl.VoterDaoImpl;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.*;
import net.thumbtack.school.elections.server.exeption.ExceptionErrorCode;
import net.thumbtack.school.elections.server.exeption.ServerException;
import net.thumbtack.school.elections.server.model.Voter;

public class VoterService {
    private final VoterDao dao;
    private final SessionService sessionService;
    private final IdeaService ideaService;
    private final ContextService contextService;
    private final Validation validation;
    private final Gson gson;

    private static final String NULL_VALUE = "Некорректный запрос.";

    public VoterService(SessionService sessionService, ContextService contextService,
                        Gson gson, IdeaService ideaService) {
        dao = new VoterDaoImpl();
        validation = new Validation();
        this.sessionService = sessionService;
        this.contextService = contextService;
        this.gson = gson;
        this.ideaService = ideaService;
    }

    /**
     * Register new voter.
     * @param requestJsonString gson element. Fields: String firstname, String lastname, @Nullable String patronymic,
     * String street, Integer house, @Nullable Integer apartment, String login, String password.
     * @return If all fields is valid and if the method has not caught any exception:
     * gson element with generated unique voter's id.
     * If first name, last name, patronymic, street, house, apartment, login or password is not valid: gson element with
     * field: String error:(some problem).
     * If election already start: gson element with field: String error: "Выборы уже проходят, действие невозможно.".
     * If voter already exist: gson element with field: String error: "Вы уже зарегестрированны.".
     * If login already exist: gson element with field: String error: "Такой логин уже используется.".
     */
    public String register(String requestJsonString) {
        if (contextService.isElectionStart()) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage()));
        }
        RegisterDtoRequest request = gson.fromJson(requestJsonString, RegisterDtoRequest.class);
        try {
            validation.validate(request.getFirstName(), request.getLastName(), request.getPatronymic(),
                    request.getStreet(), request.getHouse(), request.getApartment(), request.getLogin(),
                    request.getPassword());
            Voter voter = request.newVoter();
            dao.save(voter);
            LoginDtoResponse loginDtoResponse = gson.fromJson(login(gson.toJson(
                    new LoginDtoRequest(request.getLogin(), request.getPassword()))), LoginDtoResponse.class);
            return gson.toJson(new RegisterDtoResponse(loginDtoResponse.getToken()));
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
    }

    /**
     * Get all voters, which are registered on the server.
     * @param requestJsonString gson element with field: String token (voter's, candidate's or commissioner's unique id).
     * @return If field is valid: gson element with field: Set<Voter> voters.
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If voter logout: gson element with field: String error: "Сессия пользователя не найдена.".
     */
    public String getAll(String requestJsonString) {
        GetVoterListDtoRequest request = gson.fromJson(requestJsonString, GetVoterListDtoRequest.class);
        try {
            validation.validate(request.getToken());
            if (!gson.fromJson(sessionService.isLogin(gson.toJson(new IsLoginDtoRequest(request.getToken()))),
                    IsLoginDtoResponse.class).isLogin()) {
                return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage()));
            }
            return gson.toJson(new GetVotersListDtoResponse(dao.getAll()));
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
    }

    /**
     * Login voter.
     * @param requestJsonString gson element. Fields: String login, String password.
     * @return If all fields is valid and if the method has not caught any exception: gson element with generated
     * unique voter's id.
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
            Voter voter = dao.get(request.getLogin());
            if (voter == null) {
                return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NOT_FOUND.getMessage()));
            }
            if (!voter.getPassword().equals(request.getPassword())) {
                return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.WRONG_PASSWORD.getMessage()));
            }
            LoginVoterDtoResponse loginVoterDtoResponse = gson.fromJson(sessionService.loginVoter(
                    gson.toJson(new LoginVoterDtoRequest(voter))), LoginVoterDtoResponse.class);
            return gson.toJson(new LoginDtoResponse(loginVoterDtoResponse.getToken()));
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
    }

    /**
     * Logout user.
     * If requestJsonString contain token, owned candidate, checks: is the candidacy confirmed.
     * Set all User's ideas community(null)
     * @param requestJsonString gson element with field: String token voter's unique id.
     * @return If field is valid and if the method has not caught any exception: empty gson element.
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If voter already logout: gson element with field: String error: "Сессия пользователя не найдена.".
     */
    public String logout(String requestJsonString) {
        LogoutDtoRequest request = gson.fromJson(requestJsonString, LogoutDtoRequest.class);
        try {
            validation.validate(request.getToken());
            GetVoterDtoResponse getVoterDtoResponse = gson.fromJson(sessionService.getVoter(gson.toJson(
                    new GetVoterDtoRequest(request.getToken()))), GetVoterDtoResponse.class);
            Voter voter = getVoterDtoResponse.getVoter();
            ideaService.removeAllRating(gson.toJson(new RemoveAllRatingDtoRequest(voter)));
            return sessionService.logoutVoter(requestJsonString);
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
    }

    /**
     * Get voter by his login.
     * @param requestJsonString gson element with fields: String login the login voter, who already logged out from
     * the server.
     * @return The voter who owns this login.
     * @throws ServerException if login not found in database.
     */
    public String get(String requestJsonString) throws ServerException {
        GetVoterByLoginDtoRequest request = gson.fromJson(requestJsonString, GetVoterByLoginDtoRequest.class);
        Voter voter = dao.get(request.getLogin());
        if (voter == null) {
            throw new ServerException(ExceptionErrorCode.NOT_FOUND);
        }
        return gson.toJson(new GetVoterByLoginDtoResponse(voter));
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public VoterDao getDao() {
        return dao;
    }

    public ContextService getContextService() {
        return contextService;
    }

    public IdeaService getIdeaService() {
        return ideaService;
    }
}