package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dao.VoterDao;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.*;
import net.thumbtack.school.elections.server.exeption.ExceptionErrorCode;
import net.thumbtack.school.elections.server.exeption.ServerException;
import net.thumbtack.school.elections.server.model.Idea;
import net.thumbtack.school.elections.server.model.Voter;
import java.io.Serializable;
import java.util.*;

public class IdeaService implements Serializable {
    private final List<Idea> ideas;
    private final transient ContextService contextService;
    private final transient SessionService sessionService;
    private final transient VoterDao dao;
    private final transient Gson gson;
    private final transient Validation validation;

    private static final transient String EMPTY_JSON = "";
    private static final transient String NULL_VALUE = "Некорректный запрос.";

    public IdeaService(ContextService contextService, SessionService sessionService, VoterDao dao) {
        this.contextService = contextService;
        this.sessionService = sessionService;
        this.dao = dao;
        gson = new Gson();
        ideas = new ArrayList<>();
        validation = new Validation();
    }

    /**
     * User get all ideas.
     * @param requestJsonString gson element with field: String token (voter's of candidate's unique id).
     * @return If field is valid and if the method has not caught any exception: gson element with field:
     * Map<Idea, Float> ideas (ideas with their rating,
     * sorted by rating).
     * If user logout: gson element with field: String error: "Сессия пользователя не найдена.".
     * If field is not valid: gson element with field: String error: "Некорректный запрос.".
     */
    public String getIdeas(String requestJsonString) {
        GetAllIdeasDtoRequest request = gson.fromJson(requestJsonString, GetAllIdeasDtoRequest.class);
        try {
            validation.validate(request.getToken());
            if (gson.fromJson(sessionService.isLogin(gson.toJson(new IsLoginDtoRequest(
                    request.getToken()))), IsLoginDtoResponse.class).isLogin()) {
                Map<Idea, Float> sortedMap = new TreeMap<>();
                for (Idea idea : ideas) {
                    sortedMap.put(idea, idea.getRating());
                }
                return gson.toJson(new GetAllIdeasDtoResponse(sortedMap));
            }
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
        return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage()));
    }

    /**
     * User add new Idea.
     * If token belongs to the candidate: candidate add it idea into yourself program
     * @param requestJsonString gson element with fields: String idea (text of idea),
     * String token voter's unique id.
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
            String key = UUID.randomUUID().toString();
            Voter voter = dao.getVoterByToken(request.getToken());
            ideas.add(new Idea(key, voter, request.getIdea()));
            return gson.toJson(new AddIdeaDtoResponse(key));
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
    }

    /**
     * Set all ideas publicly owned by the logout voter.
     * @param login voter's login, who logout.
     */
    public void setIdeaCommunity(String login) {
        for (Idea idea: ideas) {
            if (!idea.isCommunity() && idea.getAuthor().getLogin().equals(login)) {
                idea.setCommunity(true);
            }
        }
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
    public String estimate(String requestJsonString) {
        if (contextService.isElectionStart()) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage()));
        }
        EstimateIdeaDtoRequest request = gson.fromJson(requestJsonString, EstimateIdeaDtoRequest.class);
        try {
            validation.validate(request.getIdeaKey(), request.getToken(), request.getRating());
            String ideaKey = request.getIdeaKey();
            Voter voter = dao.getVoterByToken(request.getToken());
            int rating = request.getRating();
            for (Idea idea : ideas) {
                if (idea.getKey().equals(ideaKey) && !idea.getVotedVoters().containsKey(voter.getLogin())) {
                    idea.getVotedVoters().put(voter.getLogin(), rating);
                    idea.setSum(idea.getSum() + rating);
                    float newRating = (float) idea.getSum() / idea.getVotedVoters().size();
                    idea.setRating(newRating);
                }
            }
            return EMPTY_JSON;
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
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
        if (contextService.isElectionStart()) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage()));
        }
        ChangeRatingDtoRequest request = gson.fromJson(requestJsonString, ChangeRatingDtoRequest.class);
        try {
            validation.validate(request.getIdeaKey(), request.getToken(), request.getRating());
            String ideaKey = request.getIdeaKey();
            Voter voter = dao.getVoterByToken(request.getToken());
            int rating = request.getRating();
            for (Idea idea : ideas) {
                if (idea.getKey().equals(ideaKey) && idea.getVotedVoters().containsKey(voter.getLogin()) &&
                        !idea.getAuthor().equals(voter)) {
                    idea.setSum(idea.getSum() + rating - idea.getVotedVoters().get(voter.getLogin()));
                    idea.getVotedVoters().put(voter.getLogin(), rating);
                    float newRating = (float) idea.getSum() / idea.getVotedVoters().size();
                    idea.setRating(newRating);
                }
            }
            return EMPTY_JSON;
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
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
        if (contextService.isElectionStart()) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage()));
        }
        RemoveRatingDtoRequest request = gson.fromJson(requestJsonString, RemoveRatingDtoRequest.class);
        try {
            validation.validate(request.getIdeaKey());
            validation.validate(request.getToken());
            String ideaKey = request.getIdeaKey();
            Voter voter = dao.getVoterByToken(request.getToken());
            for (Idea idea : ideas) {
                if (idea.getKey().equals(ideaKey) && idea.getVotedVoters().containsKey(voter.getLogin()) &&
                        !idea.getAuthor().equals(voter)) {
                    idea.setSum(idea.getSum() - idea.getVotedVoters().get(voter.getLogin()));
                    idea.getVotedVoters().remove(voter.getLogin());
                    float newRating = (float) idea.getSum() / idea.getVotedVoters().size();
                    idea.setRating(newRating);
                    break;
                }
            }
            return EMPTY_JSON;
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
    }

    /**
     * A new rating is calculated for all ideas evaluated by the voter, based on the voter's previous rating.
     * @param requestJsonString gson element with fields: Voter voter.
     */
    public void removeAllRating(String requestJsonString) {
        RemoveAllRatingDtoRequest request = gson.fromJson(requestJsonString, RemoveAllRatingDtoRequest.class);
        Voter voter = request.getVoter();
        for (Idea idea : ideas) {
            if (idea.getVotedVoters().containsKey(voter.getLogin()) && !idea.getAuthor().equals(voter)) {
                idea.setSum(idea.getSum() - idea.getVotedVoters().get(voter.getLogin()));
                idea.getVotedVoters().remove(voter.getLogin());
                float newRating = (float) idea.getSum() / idea.getVotedVoters().size();
                idea.setRating(newRating);
                idea.getVotedVoters().remove(voter.getLogin());
            }
        }
        setIdeaCommunity(voter.getLogin());
    }

    /**
     * Add all idea in ideas list.
     * @param requestJsonString gson element with fields: Candidate candidate (candidate who want confirm candidacy),
     * List ideas (list of candidate's ideas text).
     * @throws ServerException if election already start.
     */
    public void addAllIdeas(String requestJsonString) throws ServerException {
        AddAllIdeasDtoRequest addAllIdeasDtoRequest = gson.fromJson(requestJsonString, AddAllIdeasDtoRequest.class);
        for (String idea : addAllIdeasDtoRequest.getIdeas()) {
            GetVoterSessionDtoResponse getVoterSessionDtoResponse = gson.fromJson(sessionService.getVoterSession(
                    gson.toJson(new GetVoterSessionDtoRequest(addAllIdeasDtoRequest.getVoter()))),
                    GetVoterSessionDtoResponse.class);
            AddIdeaDtoRequest request = new AddIdeaDtoRequest(idea, getVoterSessionDtoResponse.getSession().getToken());
            addIdea(gson.toJson(request));
        }
    }

    /**
     * Get idea from ideas list.
     * @param requestJsonString gson element with fields: String key (unique idea's id).
     * @return The idea who owns this idea's key.
     * @throws ServerException if no idea has the given idea's key.
     */
    public String getIdea(String requestJsonString) throws ServerException {
        GetIdeaDtoRequest request = gson.fromJson(requestJsonString, GetIdeaDtoRequest.class);
        for (Idea idea : ideas) {
            if (idea.getKey().equals(request.getIdeaKey())) {
                return gson.toJson(new GetIdeaDtoResponse(idea));
            }
        }
        throw new ServerException(ExceptionErrorCode.IDEA_NOT_FOUND);
    }

    /**
     * User get all some voters ideas.
     * @param requestJsonString gson element with fields: String token (voter's of candidate's unique id),
     * List<String> logins (list of logins some voters).
     * @return If all fields is valid and if the method has not caught any exception: List<Idea> ideas
     * (list of some voters ideas).
     * If user logout: gson element with field: String error: "Сессия пользователя не найдена.".
     * If some field is not valid: gson element with field: String error: "Некорректный запрос.".
     */
    public String getAllVotersIdeas(String requestJsonString) {
        GetAllVotersIdeasDtoRequest request = gson.fromJson(requestJsonString, GetAllVotersIdeasDtoRequest.class);
        try {
            validation.validate(request.getToken(), request.getLogins());
            if (gson.fromJson(sessionService.isLogin(gson.toJson(new IsLoginDtoRequest(request.getToken()))),
                    IsLoginDtoResponse.class).isLogin()) {
                List<Idea> voterIdeas = new ArrayList<>();
                List<String> logins = request.getLogins();
                for (Idea idea : ideas) {
                    if (!idea.isCommunity() && logins.contains(idea.getAuthor().getLogin())) {
                        voterIdeas.add(idea);
                    }
                    if (idea.isCommunity() && logins.contains(null)) {
                        voterIdeas.add(idea);
                    }
                }
                return gson.toJson(new GetAllVotersIdeasDtoResponse(voterIdeas));
            }
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
        return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage()));
    }

    public List<Idea> getIdeas() {
        return ideas;
    }

    public VoterDao getDao() {
        return dao;
    }

    public ContextService getContextService() {
        return contextService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }
}