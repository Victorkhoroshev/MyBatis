package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.*;
import net.thumbtack.school.elections.server.model.Idea;
import net.thumbtack.school.elections.server.model.Voter;
import java.io.Serializable;
import java.util.*;

public class IdeaService implements Serializable {
    private final List<Idea> ideas;
    private final transient ContextService contextService;
    private final transient SessionService sessionService;
    private final transient Gson gson;
    private final transient Validation validation;

    private static final transient String EMPTY_JSON = "";
    private static final transient String NULL_VALUE = "Некорректный запрос.";

    public IdeaService(ContextService contextService, Gson gson, SessionService sessionService) {
        this.contextService = contextService;
        this.gson = gson;
        this.sessionService = sessionService;
        ideas = new ArrayList<>();
        validation = new Validation();
    }

    /**
     * Get all ideas sorted in descending order of rating.
     * @return A map of ideas and their rating.
     */
    public String getIdeas(String requestJsonString) {
        GetAllIdeasDtoRequest request = gson.fromJson(requestJsonString, GetAllIdeasDtoRequest.class);
        try {
            validation.validate(request.getToken());
            if (gson.fromJson(sessionService.isLogin(gson.toJson(new IsLoginDtoRequest(request.getToken()))), IsLoginDtoResponse.class).isLogin()) {
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
     * Generate new idea and put it in ideas list.
     * @param voter voter, who expressed his idea.
     * @param idea text of idea.
     * @throws ServerException if election already start.
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
            GetVoterDtoResponse getVoterDtoResponse = gson.fromJson(sessionService.getVoter(gson.toJson(
                    new GetVoterDtoRequest(request.getToken()))), GetVoterDtoResponse.class);
            Voter voter = getVoterDtoResponse.getVoter();
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
     * @param voter voter, who logout.
     */
    public void setIdeaCommunity(String login) {
        for (Idea idea: ideas) {
            if (!idea.isCommunity() && idea.getAuthor().getLogin().equals(login)) {
                idea.setCommunity(true);
            }
        }
    }

    /**
     * Estimate idea, indicating it's rating and key
     * a new rating is calculated based on the passed value.
     * @param ideaKey unique idea's id.
     * @param rating number in the range from 1 to 5.
     * @param voter voter, who wants to estimate an idea.
     * @throws ServerException if rating not range from 1 to 5 or election already start.
     */
    public String estimate(String requestJsonString) {
        if (contextService.isElectionStart()) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage()));
        }
        EstimateIdeaDtoRequest request = gson.fromJson(requestJsonString, EstimateIdeaDtoRequest.class);
        try {
            validation.validate(request.getIdeaKey(), request.getToken(), request.getRating());
            String ideaKey = request.getIdeaKey();
            GetVoterDtoResponse getVoterDtoResponse = gson.fromJson(sessionService.getVoter(gson.toJson(
                    new GetVoterDtoRequest(request.getToken()))), GetVoterDtoResponse.class);
            Voter voter = getVoterDtoResponse.getVoter();
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
     * Change idea's rating, by specifying your new rating and key.
     * If the user is not the author of the idea and has already voted for it, then
     * a new rating is calculated based on the passed value.
     * @param voter voter, who wants to change rating of idea.
     * @param ideaKey unique idea's id.
     * @param rating number in the range from 1 to 5.
     * @throws ServerException if rating not range from 1 to 5 or election already start.
     */
    public String changeRating(String requestJsonString) {
        if (contextService.isElectionStart()) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage()));
        }
        ChangeRatingDtoRequest request = gson.fromJson(requestJsonString, ChangeRatingDtoRequest.class);
        try {
            validation.validate(request.getIdeaKey(), request.getToken(), request.getRating());
            String ideaKey = request.getIdeaKey();
            GetVoterDtoResponse getVoterDtoResponse = gson.fromJson(sessionService.getVoter(gson.toJson(
                    new GetVoterDtoRequest(request.getToken()))), GetVoterDtoResponse.class);
            Voter voter = getVoterDtoResponse.getVoter();
            int rating = request.getRating();
            for (Idea idea : ideas) {
                if (idea.getKey().equals(ideaKey) && idea.getVotedVoters().containsKey(voter.getLogin()) && !idea.getAuthor().equals(voter)) {
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
     * Remove rating, if idea's author is not voter.
     * A new rating is calculated based on the voter's previous rating.
     * @param voter voter, who wants remove his rating.
     * @param ideaKey unique idea's id.
     * @throws ServerException if election already start.
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
            GetVoterDtoResponse getVoterDtoResponse = gson.fromJson(sessionService.getVoter(gson.toJson(
                    new GetVoterDtoRequest(request.getToken()))), GetVoterDtoResponse.class);
            Voter voter = getVoterDtoResponse.getVoter();
            for (Idea idea : ideas) {
                if (idea.getKey().equals(ideaKey) && idea.getVotedVoters().containsKey(voter.getLogin()) && !idea.getAuthor().equals(voter)) {
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
     * @param voter voter who logout.
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
     * @param voter candidate who want confirm candidacy.
     * @param ideas list of candidate's ideas text.
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
     * @param ideaKey unique idea's id.
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
     * Get all voters ideas from ideas list.
     * @param logins list of voter logins who popped up their ideas.
     * @return Ideas list.
     */
    public String getAllVotersIdeas(String requestJsonString) {
        GetAllVotersIdeasDtoRequest request = gson.fromJson(requestJsonString, GetAllVotersIdeasDtoRequest.class);
        try {
            validation.validate(request.getToken(), request.getLogins());
            if (gson.fromJson(sessionService.isLogin(gson.toJson(new IsLoginDtoRequest(request.getToken()))), IsLoginDtoResponse.class).isLogin()) {
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

    public ContextService getContextService() {
        return contextService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }
}