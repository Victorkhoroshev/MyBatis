package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dao.CandidateDao;
import net.thumbtack.school.elections.server.daoimpl.CandidateDaoImpl;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.*;
import net.thumbtack.school.elections.server.exeption.ExceptionErrorCode;
import net.thumbtack.school.elections.server.exeption.ServerException;
import net.thumbtack.school.elections.server.model.Candidate;
import net.thumbtack.school.elections.server.model.Idea;
import net.thumbtack.school.elections.server.model.Voter;
import java.io.Serializable;
import java.util.*;

public class CandidateService implements Serializable {
    private final transient CandidateDao dao;
    private final transient Validation validation;
    private final transient VoterService voterService;
    private final Map<Candidate, List<Idea>> ideas;
    private final transient ContextService contextService;
    private final transient SessionService sessionService;
    private final transient IdeaService ideaService;
    private final transient Gson gson;

    private static final transient String EMPTY_JSON = "";
    private static final transient String NULL_VALUE = "Некорректный запрос.";

    public CandidateService(ContextService contextService, Gson gson, SessionService sessionService,
                            VoterService voterService, IdeaService ideaService) {
        this.voterService = voterService;
        this.contextService = contextService;
        this.gson = gson;
        this.sessionService = sessionService;
        this.ideaService = ideaService;
        validation = new Validation();
        dao = new CandidateDaoImpl();
        ideas = new HashMap<>();
    }

    /**
     * Add new candidate if voter nas not own candidate.
     * @param requestJsonString gson element with field: String token (candidate's unique id).
     * @return If all fields is valid and if the method has not caught any exception: empty gson element.
     * If election already start: gson element with field: String error: "Выборы уже проходят, действие невозможно.".
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If voter logout: gson element with field: String error: "Сессия пользователя не найдена.".
     * If database not contain voter with this login: gson element with field: String error: "Пользователь не найден.".
     */
    public String addCandidate(String requestJsonString) {
        if (contextService.isElectionStart()) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage()));
        }
        AddCandidateDtoRequest request = gson.fromJson(requestJsonString, AddCandidateDtoRequest.class);
        try {
            validation.validate(request.getToken());
            validation.validate(request.getCandidateLogin());
            Voter voter = gson.fromJson(sessionService.getVoter(gson.toJson(
                    new GetVoterDtoRequest(request.getToken()))), GetVoterDtoResponse.class).getVoter();
            if (!voter.isHasOwnCandidate() && !dao.contains(request.getCandidateLogin())) {
                gson.toJson(new GetVoterByLoginDtoRequest(request.getCandidateLogin()));
                dao.save(new Candidate(gson.fromJson(voterService.get(gson.toJson(new GetVoterByLoginDtoRequest(
                        request.getCandidateLogin()))), GetVoterByLoginDtoResponse.class).getVoter()));
                voter.setHasOwnCandidate(true);
            }
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
        return EMPTY_JSON;
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
    public String confirmationCandidacy (String requestJsonString) {
        if (contextService.isElectionStart()) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage()));
        }
        ConfirmationCandidacyDtoRequest request = gson.fromJson(requestJsonString,
                ConfirmationCandidacyDtoRequest.class);
        try {
            validation.validate(request.getToken());
            validation.validate(request.getCandidateIdeas());
            GetVoterDtoResponse getVoterDtoResponse = gson.fromJson(sessionService.getVoter(gson.toJson(
                    new GetVoterDtoRequest(request.getToken()))), GetVoterDtoResponse.class);
            Voter voter = getVoterDtoResponse.getVoter();
            ideaService.addAllIdeas(gson.toJson(new AddAllIdeasDtoRequest(voter, request.getCandidateIdeas())));
            GetAllVotersIdeasDtoResponse response = gson.fromJson(ideaService.getAllVotersIdeas(gson.toJson(
                    new GetAllIdeasDtoRequest(request.getToken()))), GetAllVotersIdeasDtoResponse.class);
            if (dao.contains(voter.getLogin())) {
                ideas.put(dao.get(voter.getLogin()), response.getIdeas());
                voter.setHasOwnCandidate(true);
            } else if (!voter.isHasOwnCandidate()) {
                Candidate candidate = new Candidate(voter);
                dao.save(candidate);
                ideas.put(candidate, response.getIdeas());
                voter.setHasOwnCandidate(true);
            }
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
        return EMPTY_JSON;
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
    public String withdrawCandidacy (String requestJsonString) {
        if (contextService.isElectionStart()) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage()));
        }
        WithdrawCandidacyDtoRequest request = gson.fromJson(requestJsonString, WithdrawCandidacyDtoRequest.class);
        try {
            validation.validate(request.getToken());
            GetVoterDtoResponse getVoterDtoResponse = gson.fromJson(sessionService.getVoter(gson.toJson(
                    new GetVoterDtoRequest(request.getToken()))), GetVoterDtoResponse.class);
            Voter voter = getVoterDtoResponse.getVoter();
            Candidate candidate = dao.get(voter.getLogin());
            if (candidate == null) {
                return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.CANDIDATE_NOT_FOUND.getMessage()));
            }
            ideas.remove(candidate);
            dao.delete(candidate);
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
        return EMPTY_JSON;
    }

    /**
     * Logout candidate.
     * If requestJsonString contain token, owned candidate, checks: is the candidacy confirmed.
     * Set all User's ideas community(null)
     * @param requestJsonString gson element with field: String token candidate's unique id.
     * @return If field is valid and if the method has not caught any exception: empty gson element.
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If voter already logout: gson element with field: String error: "Сессия пользователя не найдена.".
     * If voter is candidate and he is not withdraw yourself candidacy: gson element with field: String error:
     * "Невозможно разлогиниться, для начала, снимите свою кандидатуру с выборов.".
     */
    public String logout(String requestJsonString) {
        LogoutDtoRequest request = gson.fromJson(requestJsonString, LogoutDtoRequest.class);
        try {
            validation.validate(request.getToken());
            return gson.toJson(new ErrorDtoResponse("Невозможно разлогиниться, для начала," +
                    " снимите свою кандидатуру с выборов."));
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
    }

    /**
     * Candidate verification.
     * @param requestJsonString json element with field: String token(unique candidate's id).
     * @return If ideas map contains this candidate: true.
     * If ideas map not contains this candidate: false.
     */
    public boolean isCandidate(String requestJsonString) {
        IsCandidateDtoRequest request = gson.fromJson(requestJsonString, IsCandidateDtoRequest.class);
        try {
            GetVoterDtoResponse getVoterDtoResponse = gson.fromJson(sessionService.getVoter(gson.toJson(
                    new GetVoterDtoRequest(request.getToken()))), GetVoterDtoResponse.class);
            Voter voter = getVoterDtoResponse.getVoter();
            return ideas.containsKey(new Candidate(voter));
        } catch (ServerException ex) {
            return false;
        }
    }

    /**
     * User add new Idea.
     * If token belongs to the candidate: candidate add it idea into yourself program
     * @param requestJsonString gson element with fields: String idea (text of idea),
     * String token candidate's unique id.
     * @return If all fields is valid and if the method has not caught any exception: empty gson element.
     * If election already start: gson element with field: String error: "Выборы уже проходят, действие невозможно.".
     * If voter logout: gson element with field: String error: "Сессия пользователя не найдена.".
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     * If no idea has the given voter's login and text: gson element with field: String error: "Идея не найдена.".
     */
    public String addIdea(String requestJsonString) {
        if (contextService.isElectionStart()) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage()));
        }
        AddIdeaDtoRequest request = gson.fromJson(requestJsonString, AddIdeaDtoRequest.class);
        try {
            validation.validate(request.getIdea());
            validation.validate(request.getToken());
            GetVoterDtoResponse getVoterDtoResponse = gson.fromJson(sessionService.getVoter(gson.toJson(
                    new GetVoterDtoRequest(request.getToken()))), GetVoterDtoResponse.class);
            Voter voter = getVoterDtoResponse.getVoter();
            Candidate candidate = dao.get(voter.getLogin());
            if (candidate == null) {
                return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.CANDIDATE_NOT_FOUND.getMessage()));
            }
            String key = gson.fromJson(ideaService.addIdea(requestJsonString), AddIdeaDtoResponse.class).getKey();
            GetIdeaDtoResponse getIdeaDtoResponse = gson.fromJson(ideaService.getIdea(
                    gson.toJson(new GetIdeaDtoRequest(key))), GetIdeaDtoResponse.class);
            ideas.get(candidate).add(getIdeaDtoResponse.getIdea());
            return gson.toJson(new AddIdeaDtoResponse(key));
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
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
        if (contextService.isElectionStart()) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage()));
        }
        RemoveIdeaDtoRequest request = gson.fromJson(requestJsonString, RemoveIdeaDtoRequest.class);
        try {
            validation.validate(request.getIdeaKey());
            validation.validate(request.getToken());
            GetVoterDtoResponse getVoterDtoResponse = gson.fromJson(sessionService.getVoter(gson.toJson(
                    new GetVoterDtoRequest(request.getToken()))), GetVoterDtoResponse.class);
            Voter voter = getVoterDtoResponse.getVoter();
            GetIdeaDtoResponse getIdeaDtoResponse = gson.fromJson(ideaService.getIdea(gson.toJson(
                    new GetIdeaDtoRequest(request.getIdeaKey()))), GetIdeaDtoResponse.class);
            Idea idea = getIdeaDtoResponse.getIdea();
            Candidate candidate = dao.get(voter.getLogin());
            if (candidate == null) {
                return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.CANDIDATE_NOT_FOUND.getMessage()));
            }
            if (!idea.getAuthor().equals(voter)) {
                ideas.get(candidate).remove(idea);
            }
            return EMPTY_JSON;
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
    }

    /**
     * User get candidates map.
     * @param requestJsonString gson element with field: String token (voter's of candidate's unique id).
     * @return If field is valid and if the method has not caught any exception: gson element with field:
     * Map<Candidate, List<Idea>> candidateMap(candidates map with
     * their program).
     * If user logout: gson element with field: String error: "Сессия пользователя не найдена.".
     * If field is not valid: gson element with field: "String error: "Некорректный запрос.".
     */
    public String getCandidateMap(String requestJsonString) {
        GetCandidateMapDtoRequest request = gson.fromJson(requestJsonString, GetCandidateMapDtoRequest.class);
        try {
            validation.validate(request.getToken());
            if (gson.fromJson(sessionService.isLogin(gson.toJson(new IsLoginDtoRequest(request.getToken()))),
                    IsLoginDtoResponse.class).isLogin()) {
                return gson.toJson(new GetCandidateMapDtoResponse(ideas));
            }
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
        return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage()));
    }

    public Set<Candidate> getCandidateSet() {
        return ideas.keySet();
    }

    /**
     * Get candidate into candidate's map by login.
     * @param requestJsonString json element with field: String token(unique candidate's id).
     * @return If ideas map contains candidate with this login: candidate.
     * If login equals null : null.
     * @throws ServerException if in ideas map not contains candidate with this login.
     */
    public String getCandidate(String requestJsonString) throws ServerException {
        GetCandidateDtoRequest request = gson.fromJson(requestJsonString, GetCandidateDtoRequest.class);
        if (request.getLogin() != null) {
            for (Candidate candidate : ideas.keySet()) {
                if (candidate.getLogin().equals(request.getLogin())) {
                    return gson.toJson(new GetCandidateDtoResponse(candidate));
                }
            }
            throw new ServerException(ExceptionErrorCode.CANDIDATE_NOT_FOUND);
        }
        return null;
    }

    public Map<Candidate, List<Idea>> getIdeas() {
        return ideas;
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

    public VoterService getVoterService() {
        return voterService;
    }

    public CandidateDao getDao() {
        return dao;
    }
}