package net.thumbtack.school.elections.server;
import com.google.gson.Gson;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.model.Candidate;
import net.thumbtack.school.elections.server.model.Context;
import net.thumbtack.school.elections.server.service.*;
import java.io.*;
import java.util.HashSet;

public class Server {
    private Gson gson;
    private ContextService contextService;
    private SessionService sessionService;
    private IdeaService ideaService;
    private CandidateService candidateService;
    private VoterService voterService;
    private CommissionerService commissionerService;
    private ElectionService electionService;

    public void startServer(String savedDataFileName) throws IOException, ClassNotFoundException {
        gson = new Gson();
        sessionService = new SessionService(gson);
        if (savedDataFileName != null) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(
                    new FileInputStream(savedDataFileName))) {
                Context context = (Context) objectInputStream.readObject();
                ideaService = context.getIdeaService();
                candidateService = context.getCandidateService();
                electionService = context.getElectionService();
                contextService = new ContextService(context);
                commissionerService = new CommissionerService(sessionService, electionService, contextService, gson, candidateService);
                voterService = new VoterService(sessionService, contextService, gson, ideaService);
            }
        } else {
            contextService = new ContextService();
            ideaService = new IdeaService(contextService, gson, sessionService);
            voterService = new VoterService(sessionService, contextService, gson, ideaService);
            candidateService = new CandidateService(contextService, gson, sessionService, voterService, ideaService);
            electionService = new ElectionService(contextService, gson, sessionService, candidateService);
            commissionerService = new CommissionerService(sessionService, electionService, contextService, gson, candidateService);
        }
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
        return voterService.register(requestJsonString);
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
        if (commissionerService.isCommissioner(requestJsonString)) {
            return commissionerService.login(requestJsonString);
        }
        return voterService.login(requestJsonString);
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
        if (commissionerService.isCommissioner(requestJsonString)) {
            return commissionerService.logout(requestJsonString);
        }
        if (candidateService.isCandidate(requestJsonString)) {
            return candidateService.logout(requestJsonString);
        }
        return voterService.logout(requestJsonString);
    }

    /**
     * Get all voters, which are registered on the server.
     * @param requestJsonString gson element with field: String token (voter's, candidate's or commissioner's unique id).
     * @return If field is valid: gson element with field: Set<Voter> voters.
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If voter logout: gson element with field: String error: "Сессия пользователя не найдена.".
     */
    public String getVoterList(String requestJsonString) {
        return voterService.getAll(requestJsonString);
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
        return candidateService.addCandidate(requestJsonString);
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
        return candidateService.confirmationCandidacy(requestJsonString);
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
        return candidateService.withdrawCandidacy(requestJsonString);
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
        if (candidateService.isCandidate(requestJsonString)) {
            return candidateService.addIdea(requestJsonString);
        }
        return ideaService.addIdea(requestJsonString);
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
        return ideaService.estimate(requestJsonString);
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
        return ideaService.changeRating(requestJsonString);
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
        return ideaService.removeRating(requestJsonString);
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
        return candidateService.addIdea(requestJsonString);
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
        return candidateService.removeIdea(requestJsonString);
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
        return candidateService.getCandidateMap(requestJsonString);
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
        return ideaService.getIdeas(requestJsonString);
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
        return ideaService.getAllVotersIdeas(requestJsonString);
    }

    /**
     * Commissioner start election.
     * @param requestJsonString gson element with field: String token(commissioner's unique id).
     * @return If field is valid and if the method has not caught any exception: empty gson element.
     * If the token does not belong to chairman: gson element with field: String error: "Вы не председатель коммиссии.".
     * If field is not valid: gson element with field: String error: "Некорректный запрос.".
     */
    public String startElection(String requestJsonString) {
        return commissionerService.startElection(requestJsonString);
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
        return electionService.vote(requestJsonStrong);
    }

    /**
     * Commissioner get election result.
     * @param requestJsonString gson element with field: String token(commissioner's unique id).
     * @return If field is valid and if the method has not caught any exception: gson element with field: Set<Candidate> candidateSet(candidates set).
     * If the token does not belong to chairman: gson element with field: String error: "Вы не председатель коммиссии." .
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     */
    public String getElectionResult(String requestJsonString) {
        return commissionerService.getElectionResult(requestJsonString);
    }

    public Gson getGson() {
        return gson;
    }

    public ContextService getContextService() {
        return contextService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public IdeaService getIdeaService() {
        return ideaService;
    }

    public CandidateService getCandidateService() {
        return candidateService;
    }

    public VoterService getVoterService() {
        return voterService;
    }

    public CommissionerService getCommissionerService() {
        return commissionerService;
    }

    public ElectionService getElectionService() {
        return electionService;
    }
}