package net.thumbtack.school.elections.server;
import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.*;
import net.thumbtack.school.elections.server.model.Context;
import net.thumbtack.school.elections.server.service.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final String EMPTY_JSON = "";
    private static final String NULL_VALUE = "Некорректный запрос.";
    //REVU:private modifiers?
    Gson gson;
    ContextService contextService;
    SessionService sessionService;
    IdeaService ideaService;
    CandidateService candidateService;
    VoterService voterService;
    CommissionerService commissionerService;
    ElectionService electionService;

    public void startServer(String savedDataFileName) throws IOException, ClassNotFoundException {
        gson = new Gson();
        sessionService = new SessionService();
        if (savedDataFileName != null) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(
                    new FileInputStream(savedDataFileName))) {
                Context context = (Context) objectInputStream.readObject();
                ideaService = context.getIdeaService();
                candidateService = context.getCandidateService();
                electionService = context.getElectionService();
                contextService = new ContextService(context);
                commissionerService = new CommissionerService(sessionService, electionService, contextService);
            }
        } else {
            contextService = new ContextService();
            ideaService = new IdeaService(contextService);
            candidateService = new CandidateService(contextService);
            electionService = new ElectionService(contextService);
            commissionerService = new CommissionerService(sessionService, electionService, contextService);
        }
        voterService = new VoterService(sessionService, contextService);
    }

    public void stopServer(String saveDataFileName) throws IOException {
        gson = null;
        sessionService = null;
        voterService = null;
        if (saveDataFileName != null) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(saveDataFileName))) {
                Context context = contextService.getContext();
                contextService.sync();
                context.setCandidateService(candidateService);
                context.setIdeaService(ideaService);
                context.setElectionService(electionService);
                objectOutputStream.writeObject(context);
            }
        }
        ideaService = null;
        candidateService = null;
        contextService = null;
        electionService = null;
        commissionerService = null;
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
        String response = "";
        RegisterDtoRequest request = gson.fromJson(requestJsonString, RegisterDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (requestJsonString == null || !request.requiredFieldsIsNotNull()) {
            return gson.toJson(new ErrorDtoResponse("Пожалуйста, заполните все данные."));
        }
        if (!request.isFirstNameValid()) {
            response += "Имя должно быть на кириллице, без пробелов, спец. символов и цифр.\n";
        }
        if (!request.isLastNameValid()) {
            response += "Фамилия должна быть на кириллице, без пробелов, спец. символов и цифр.\n";
        }
        if (!request.isPatronymicValid()) {
            response += "Отчество должно быть на кириллице, без пробелов, спец. символов и цифр.\n";
        }
        if (!request.isStreetValid()) {
            response += "Название улицы должно быть на кириллице, без пробелов, спец. символов и цифр.\n";
        }
        if (!request.isHouseValid()) {
            response += "Номер дома не может быть меньше единицы.\n";
        }
        if (!request.isApartmentValid()) {
            response += "Номер квартиры не может быть меньше нуля.\n";
        }
        if (!request.isLoginValid()) {
            response += "Длинна логина должна быть не меньше 9 символо.\n";
        }
        if (!request.isPasswordValid()) {
            response += "Пароль должен содержать хотя бы одну заглавную букву, одну строчную букву," +
            " цифру и один спец. символ, а его длинна не менее 9 символов, без пробелов.\n";
        }
        if (response.length() <  1){
            try {
                return gson.toJson(new RegisterDtoResponse(voterService.register(request.newVoter())));
            } catch (ServerException ex) {
                return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
            }
        }
        return gson.toJson(new ErrorDtoResponse(response));
    }


    /**
     * Login user.
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
        String response = "";
        LoginDtoRequest request = gson.fromJson(requestJsonString, LoginDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request == null || !request.requiredFieldsIsNotNull()) {
            response += "Пожалуйста, введите логин и пароль.";
            return gson.toJson(new ErrorDtoResponse(response));
        }
        if(!request.isLoginValid()) {
            response += "Длинна логина должна быть не меньше 9 символо.\n";
        }
        if(!request.isPasswordValid()) {
            response += "Пароль должен содержать хотя бы одну заглавную букву, одну строчную букву," +
                    " цифру и один спец. символ, а его длинна не менее 9 символов, без пробелов.\n";
        }
        if (response.length() < 1) {
            try {
                if (commissionerService.getLogins().contains(request.getLogin())) {
                    return gson.toJson(new LoginDtoResponse(commissionerService.login(request.getLogin(),
                            request.getPassword())));
                }
                return gson.toJson(new LoginDtoResponse(voterService.login(request.getLogin(),
                        request.getPassword())));
            } catch (ServerException ex) {
                return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
            }
        }
        return gson.toJson(new ErrorDtoResponse(response));
    }

    /**
     * Logout user.
     * If requestJsonString contain token, owned candidate, checks: is the candidacy confirmed.
     * Set all User's ideas community(null)
     * @param requestJsonString gson element with field: String token (voter's, candidate's or commissioner's unique id).
     * @return If field is valid and if the method has not caught any exception: empty gson element.
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If voter already logout: gson element with field: String error: "Сессия пользователя не найдена.".
     * If voter is candidate and he is not withdraw yourself candidacy: gson element with field: String error:
     * "Невозможно разлогиниться, для начала, снимите свою кандидатуру с выборов.".
     */
    public String logout(String requestJsonString) {
        LogoutDtoRequest request = gson.fromJson(requestJsonString, LogoutDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request == null || !request.requiredFieldsIsNotNull()) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
        try {
            if (commissionerService.isCommissioner(request.getToken())){
                commissionerService.logout(request.getToken());
            }
            else if (candidateService.isCandidate(sessionService.getVoter(request.getToken()))) {
                return gson.toJson(new ErrorDtoResponse("Невозможно разлогиниться, для начала," +
                        " снимите свою кандидатуру с выборов."));
            } else {
                ideaService.setIdeaCommunity(sessionService.getVoter(request.getToken()));
                ideaService.removeAllRating(sessionService.getVoter(request.getToken()));
                voterService.logout(request.getToken());
            }
            return EMPTY_JSON;
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        }
    }

    /**
     * Get all voters, which are registered on the server.
     * @param requestJsonString gson element with field: String token (voter's, candidate's or commissioner's unique id).
     * @return If field is valid: gson element with field: Set<Voter> voters.
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If voter logout: gson element with field: String error: "Сессия пользователя не найдена.".
     */
    public String getVoterList(String requestJsonString) {
        GetVoterListDtoRequest request = gson.fromJson(requestJsonString, GetVoterListDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            if (!sessionService.isLogin(request.getToken())) {
                return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage()));
            }
            return gson.toJson(new GetVotersListDtoResponse(voterService.getAll()));
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
    }

    /**
     * Add new candidate if voter nas not own candidate.
     * @param requestJsonString gson element with field: String token (voter's unique id).
     * @return If all fields is valid and if the method has not caught any exception: empty gson element.
     * If election already start: gson element with field: String error: "Выборы уже проходят, действие невозможно.".
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If voter logout: gson element with field: String error: "Сессия пользователя не найдена.".
     * If database not contain voter with this login: gson element with field: String error: "Пользователь не найден.".
     */
    public String addCandidate(String requestJsonString) {
        AddCandidateDtoRequest request = gson.fromJson(requestJsonString, AddCandidateDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            try {
                candidateService.addCandidate(sessionService.getVoter(request.getToken()),
                        voterService.get(request.getCandidateLogin()));
                return EMPTY_JSON;
            } catch (ServerException ex) {
                return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
            }
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
    }

    /**
     * Voter confirmation yourself candidacy if has not own candidate and has ideas.
     * @param requestJsonString gson element with fields: String token (voter's unique id),
     * List<String> candidateIdeas (list with text of voter's ideas).
     * @return If all fields is valid and if the method has not caught any exception: empty gson element.
     * If election already start: gson element with field: String error: "Выборы уже проходят, действие невозможно.".
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If voter logout: gson element with field: String error: "Сессия пользователя не найдена.".
     */
    public String confirmationCandidacy(String requestJsonString) {
        ConfirmationCandidacyDtoRequest request = gson.fromJson(requestJsonString,
                ConfirmationCandidacyDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            try {
                ideaService.addAllIdeas(sessionService.getVoter(request.getToken()), request.getCandidateIdeas());
                List<String> list = new ArrayList<>();
                list.add(sessionService.getVoter(request.getToken()).getLogin());
                candidateService.confirmationCandidacy(sessionService.getVoter(request.getToken()),
                        ideaService.getAllVotersIdeas(list));
                return EMPTY_JSON;
            } catch (ServerException ex) {
                return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
            }
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
    }

    /**
     * Candidate withdraw yourself candidacy.
     * @param requestJsonString gson element with fields: String token (candidate's unique id).
     * @return If all fields is valid and if the method has not caught any exception: empty gson element.
     * If voter logout: gson element with field: String error: "Сессия пользователя не найдена.".
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If election already start: gson element with field: String error: "Выборы уже проходят, действие невозможно.".
     * If database not contains this candidate: gson element with field: String error: "Кандидат не найден.".
     */
    public String withdrawCandidacy(String requestJsonString) {
        WithdrawCandidacyRequest request = gson.fromJson(requestJsonString, WithdrawCandidacyRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            try {
                candidateService.withdrawCandidacy(sessionService.getVoter(request.getToken()));
                return EMPTY_JSON;
            } catch (ServerException ex) {
                return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
            }
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
    }

    /**
     * User add new Idea.
     * If token belongs to the candidate: candidate add it idea into yourself program
     * @param requestJsonString gson element with fields: String idea (text of idea),
     * String token (voter's or candidate's unique id).
     * @return If all fields is valid and if the method has not caught any exception: empty gson element.
     * If election already start: gson element with field: String error: "Выборы уже проходят, действие невозможно.".
     * If voter logout: gson element with field: String error: "Сессия пользователя не найдена.".
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If no idea has the given voter's login and text: gson element with field: String error: "Идея не найдена.".
     */
    public String addIdea(String requestJsonString) {
        AddIdeaDtoRequest request = gson.fromJson(requestJsonString, AddIdeaDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            try {
                if (candidateService.isCandidate(sessionService.getVoter(request.getToken()))) {
                    ideaService.addIdea(sessionService.getVoter(request.getToken()), request.getIdea());
                    candidateService.addIdea(sessionService.getVoter(request.getToken()), ideaService.getIdea(
                            ideaService.getKey(sessionService.getVoter(request.getToken()),request.getIdea())));
                    return EMPTY_JSON;
                } else {
                    ideaService.addIdea(sessionService.getVoter(request.getToken()), request.getIdea());
                    return EMPTY_JSON;
                }
            } catch (ServerException ex) {
                return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
            }
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
    }

    /**
     * User estimate some idea.
     * @param requestJsonString gson element with fields: String ideaKey (unique idea's key),
     * int rating (number for estimate), String token (voter's or candidate's unique id).
     * @return If all fields is valid and if the method has not caught any exception: empty gson element.
     * If rating not range from 1 to 5: gson element with field: String error: "Оценка должна быть от 1 до 5.".
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If election already start: gson element with field: String error: "Выборы уже проходят, действие невозможно.".
     * If no idea has the given idea's key: gson element with field: String error: "Идея не найдена.".
     * If voter logout: gson element with field: String error: "Сессия пользователя не найдена.".
     */
    public String estimateIdea(String requestJsonString) {
        EstimateIdeaDtoRequest request = gson.fromJson(requestJsonString, EstimateIdeaDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            try {
                ideaService.estimate(request.getIdeaKey(), request.getRating(),
                        sessionService.getVoter(request.getToken()));
                return EMPTY_JSON;
            } catch (ServerException ex) {
                return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
            }
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
    }

    /**
     * User changes the rating of an idea that was previously rated.
     * @param requestJsonString gson element with fields: String token (voter's or candidate's unique id),
     * String ideaKey (unique idea's key), int rating (number for change yourself rating).
     * @return If all fields is valid and if the method has not caught any exception: empty gson element.
     * If rating not range from 1 to 5: gson element with field: String error: "Оценка должна быть от 1 до 5.".
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If election already start: gson element with field: String error: "Выборы уже проходят, действие невозможно.".
     * If no idea has the given idea's key: gson element with field: String error: "Идея не найдена.".
     * If voter logout: gson element with field: String error: "Сессия пользователя не найдена.".
     */
    public String changeRating(String requestJsonString) {
        ChangeRatingDtoRequest request = gson.fromJson(requestJsonString, ChangeRatingDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            try {
                ideaService.changeRating(sessionService.getVoter(request.getToken()),
                        request.getIdeaKey(), request.getRating());
                return EMPTY_JSON;
            } catch (ServerException ex) {
                return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
            }
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
    }

    /**
     * User remove yourself rating.
     * @param requestJsonString gson element with fields: String token (voter's or candidate's unique id),
     * String ideaKey (unique idea's key).
     * @return If all fields is valid and if the method has not caught any exception: empty gson element.
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If election already start: gson element with field: String error: "Выборы уже проходят, действие невозможно.".
     * If no idea has the given idea's key: gson element with field: String error: "Идея не найдена.".
     * If voter logout: gson element with field: String error: "Сессия пользователя не найдена.".
     */
    public String removeRating(String requestJsonString) {
        RemoveRatingDtoRequest request = gson.fromJson(requestJsonString, RemoveRatingDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            try {
                ideaService.removeRating(sessionService.getVoter(request.getToken()), request.getIdeaKey());
                return EMPTY_JSON;
            } catch (ServerException ex) {
                return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
            }
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
    }

    /**
     * Candidate take some idea into yourself program.
     * @param requestJsonString gson element with fields: String token (candidate's unique id),
     * String ideaKey (unique idea's key).
     * @return If all fields is valid and if the method has not caught any exception: empty gson element.
     * If database not contains this candidate: gson element with field: String error: "Кандидат не найден.".
     * If election already start: gson element with field: String error: "Выборы уже проходят, действие невозможно.".
     * If no idea has the given idea's key: gson element with field: String error: "Идея не найдена.".
     * If voter logout: gson element with field: "error: String error: "Сессия пользователя не найдена.".
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     */
    public String takeIdea(String requestJsonString) {
        TakeIdeaDtoRequest request = gson.fromJson(requestJsonString, TakeIdeaDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            try {
                candidateService.addIdea(sessionService.getVoter(request.getToken()),
                        ideaService.getIdea(request.getIdeaKey()));
                return EMPTY_JSON;
            } catch (ServerException ex) {
                return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
            }
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
    }

    /**
     * Candidate remove not yourself idea
     * @param requestJsonString gson element with fields: String token (candidate's unique id),
     * String ideaKey (unique idea's key).
     * @return If all fields is valid and if the method has not caught any exception: empty gson element.
     * If voter logout: gson element with field: String error: "Сессия пользователя не найдена.".
     * If database not contain candidate: gson element with field: String error: "Кандидат не найден.".
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If election already start: gson element with field: String error: "Выборы уже проходят, действие невозможно.".
     */
    public String removeIdea(String requestJsonString) {
        RemoveIdeaDtoRequest request = gson.fromJson(requestJsonString, RemoveIdeaDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            try {
                candidateService.removeIdea(sessionService.getVoter(request.getToken()),
                        ideaService.getIdea(request.getIdeaKey()));
                return EMPTY_JSON;
            } catch (ServerException ex) {
                return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
            }
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
    }

    /**
     * User get candidates map.
     * @param requestJsonString gson element with field: String token (voter's of candidate's unique id).
     * @return If field is valid and if the method has not caught any exception: gson element with field: Map<Candidate, List<Idea>> candidateMap(candidates map with
     * their program).
     * If user logout: gson element with field: String error: "Сессия пользователя не найдена.".
     * If field is not valid: gson element with field: "String error: "Некорректный запрос.".
     */
    public String getCandidatesMap(String requestJsonString) {
        GetCandidateMapDtoRequest request = gson.fromJson(requestJsonString, GetCandidateMapDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            if (!sessionService.isLogin(request.getToken())) {
                return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage()));
            }
            return gson.toJson(new GetCandidateMapDtoResponse(candidateService.getCandidateMap()));
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
    }

    /**
     * User get all ideas.
     * @param requestJsonString gson element with field: String token (voter's of candidate's unique id).
     * @return If field is valid and if the method has not caught any exception: gson element with field: Map<Idea, Float> ideas (ideas with their rating,
     * sorted by rating).
     * If user logout: gson element with field: String error: "Сессия пользователя не найдена.".
     * If field is not valid: gson element with field: String error: "Некорректный запрос.".
     */
    public String getAllIdeas(String requestJsonString) {
        GetAllIdeasDtoRequest request = gson.fromJson(requestJsonString, GetAllIdeasDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            if (!sessionService.isLogin(request.getToken())) {
                return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage()));
            }
            return gson.toJson(new GetAllIdeasDtoResponse(ideaService.getIdeas()));
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
    }

    /**
     * User get all some voters ideas.
     * @param requestJsonString gson element with fields: String token (voter's of candidate's unique id),
     * List<String> logins (list of logins some voters).
     * @return If all fields is valid and if the method has not caught any exception: List<Idea> ideas (list of some voters ideas).
     * If user logout: gson element with field: String error: "Сессия пользователя не найдена.".
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     */
    public String getAllVotersIdeas(String requestJsonString) {
        GetAllVotersIdeasDtoRequest request = gson.fromJson(requestJsonString, GetAllVotersIdeasDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            if (!sessionService.isLogin(request.getToken())) {
                return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage()));
            }
            return gson.toJson(new GetAllVotersIdeasDtoResponse(ideaService.getAllVotersIdeas(request.getLogins())));
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
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
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            try {
                commissionerService.startElection(request.getToken(), candidateService.getCandidateSet());
                return EMPTY_JSON;
            } catch (ServerException ex) {
                return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
            }
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
    }

    /**
     * Voter vote for a someone candidate if election start or not stop.
     * @param requestJsonStrong gson element with fields: String token (voter's unique id),
     * String candidateLogin (candidate's login).
     * @return If all fields is valid and if the method has not caught any exception: empty gson element.
     * If election is not start: gson element with field: String error: "Голосование не началось.".
     * If election already stop: gson element with field: String error: "Голосование закончилось.".
     * If voter logout: gson element with field: String error: "Сессия пользователя не найдена.".
     * If in ideas map not contains candidate with this login: gson element with field: String error: "Кандидат не найден.".
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     */
    public String vote(String requestJsonStrong) {
        VoteDtoRequest request = gson.fromJson(requestJsonStrong, VoteDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            try {
                electionService.vote(sessionService.getVoter(request.getToken()),
                        candidateService.getCandidate(request.getCandidateLogin()));
                return EMPTY_JSON;
            } catch (ServerException ex) {
                return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
            }
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
    }

    /**
     * Commissioner get election result.
     * @param requestJsonString gson element with field: String token(commissioner's unique id).
     * @return If field is valid and if the method has not caught any exception: gson element with field: Set<Candidate> candidateSet(candidates set).
     * If the token does not belong to chairman: gson element with field: String error: "Вы не председатель коммиссии." .
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     */
    public String getElectionResult(String requestJsonString) {
        GetElectionResultDtoRequest request = gson.fromJson(requestJsonString, GetElectionResultDtoRequest.class);
        //REVU: вынесите логику в слой сервиса. Сервер принимает запрос, передает его сервису и получает ответ.
        if (request != null && request.requiredFieldsIsNotNull()) {
            try {
                return gson.toJson(new GetElectionResultDtoResponse(
                        commissionerService.getElectionResult(request.getToken())));
            } catch (ServerException ex) {
                return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
            }
        }
        return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
    }
}
