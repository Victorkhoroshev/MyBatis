package net.thumbtack.school.elections.server.service;

import net.thumbtack.school.elections.server.model.Idea;
import net.thumbtack.school.elections.server.model.Voter;
import java.io.Serializable;
import java.util.*;

public class IdeaService implements Serializable {
    private final List<Idea> ideas;
    private final transient ContextService contextService;

    public IdeaService(ContextService contextService) {
        ideas = new ArrayList<>();
        this.contextService = contextService;
    }

    /**
     * Get all ideas sorted in descending order of rating.
     * @return A map of ideas and their rating.
     */
    public Map<Idea, Float> getIdeas() {
        Map<Idea, Float> sortedMap = new TreeMap<>();
        for (Idea idea : ideas) {
            sortedMap.put(idea, idea.getRating());
        }
        return sortedMap;
    }

    /**
     * Generate new idea and put it in ideas list.
     * @param voter voter, who expressed his idea.
     * @param idea text of idea.
     * @throws ServerException if election already start.
     */
    public void addIdea(Voter voter, String idea) throws ServerException {
        if (!contextService.isElectionStart()) {
            ideas.add(new Idea(UUID.randomUUID().toString(), voter, idea));
        } else {
            throw new ServerException(ExceptionErrorCode.ELECTION_START);
        }
    }

    /**
     * Set all ideas publicly owned by the logout voter.
     * @param voter voter, who logout.
     */
    public void setIdeaCommunity(Voter voter) {
        for (Idea idea: ideas) {
            if (idea.getAuthor() != null && idea.getAuthor().equals(voter)) {
                idea.setAuthor(null);
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
    public void estimate(String ideaKey, int rating, Voter voter) throws ServerException {
        if (!contextService.isElectionStart()) {
            if (rating < 1 || rating > 5) {
                throw new ServerException(ExceptionErrorCode.RATING_INCORRECT);
            }
            for (Idea idea : ideas) {
                if (idea.getKey().equals(ideaKey) && !idea.getVotedVoters().containsKey(voter)) {
                    idea.getVotedVoters().put(voter, rating);
                    idea.setSum(idea.getSum() + rating);
                    float newRating = (float) idea.getSum() / idea.getVotedVoters().size();
                    idea.setRating(newRating);
                }
            }
        } else {
            throw new ServerException(ExceptionErrorCode.ELECTION_START);
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
    public void changeRating(Voter voter, String ideaKey, int rating) throws ServerException {
        if (!contextService.isElectionStart()) {
            if (rating < 1 || rating > 5) {
                throw new ServerException(ExceptionErrorCode.RATING_INCORRECT);
            }
            for (Idea idea : ideas) {
                if (idea.getKey().equals(ideaKey) && idea.getVotedVoters().containsKey(voter) && idea.getAuthor() != voter) {
                    idea.setSum(idea.getSum() + rating - idea.getVotedVoters().get(voter));
                    idea.getVotedVoters().put(voter, rating);
                    float newRating = (float) idea.getSum() / idea.getVotedVoters().size();
                    idea.setRating(newRating);
                    break;
                }
            }
        } else {
            throw new ServerException(ExceptionErrorCode.ELECTION_START);
        }
    }

    /**
     * Remove rating, if idea's author is not voter.
     * A new rating is calculated based on the voter's previous rating.
     * @param voter voter, who wants remove his rating.
     * @param ideaKey unique idea's id.
     * @throws ServerException if election already start.
     */
    public void removeRating(Voter voter, String ideaKey) throws ServerException {
        if (!contextService.isElectionStart()) {
            for (Idea idea : ideas) {
                if (idea.getKey().equals(ideaKey) && idea.getVotedVoters().containsKey(voter) && idea.getAuthor() != voter) {
                    idea.setSum(idea.getSum() - idea.getVotedVoters().get(voter));
                    idea.getVotedVoters().remove(voter);
                    float newRating = (float) idea.getSum() / idea.getVotedVoters().size();
                    idea.setRating(newRating);
                    break;
                }
            }
        } else {
            throw new ServerException(ExceptionErrorCode.ELECTION_START);
        }
    }

    /**
     * A new rating is calculated for all ideas evaluated by the voter, based on the voter's previous rating.
     * @param voter voter who logout.
     */
    public void removeAllRating(Voter voter) {
        for (Idea idea : ideas) {
            if (idea.getVotedVoters().containsKey(voter) && idea.getAuthor() != voter) {
                idea.setSum(idea.getSum() - idea.getVotedVoters().get(voter));
                idea.getVotedVoters().remove(voter);
                float newRating = (float) idea.getSum() / idea.getVotedVoters().size();
                idea.setRating(newRating);
                idea.getVotedVoters().remove(voter);
            }
        }
    }

    /**
     * Add all idea in ideas list.
     * @param voter candidate who want confirm candidacy.
     * @param ideas list of candidate's ideas text.
     * @throws ServerException if election already start.
     */
    public void addAllIdeas(Voter voter, List<String> ideas) throws ServerException {
        for (String idea : ideas) {
            addIdea(voter, idea);
        }
    }

    /**
     * Get idea from ideas list.
     * @param ideaKey unique idea's id.
     * @return The idea who owns this idea's key.
     * @throws ServerException if no idea has the given idea's key.
     */
    public Idea getIdea(String ideaKey) throws ServerException {
        for (Idea idea : ideas) {
            if (idea.getKey().equals(ideaKey)) {
                return idea;
            }
        }
        throw new ServerException(ExceptionErrorCode.IDEA_NOT_FOUND);
    }

    /**
     * Get all voters ideas from ideas list.
     * @param logins list of voter logins who popped up their ideas.
     * @return Ideas list.
     */
    public List<Idea> getAllVotersIdeas(List<String> logins) {
        List<Idea> voterIdeas = new ArrayList<>();
        for (Idea idea : ideas) {
            if (idea.getAuthor() != null && logins.contains(idea.getAuthor().getLogin())) {
                voterIdeas.add(idea);
            }
            if (idea.getAuthor() == null && logins.contains(null)) {
                voterIdeas.add(idea);
            }
        }
        return voterIdeas;
    }

    /**
     * Get unique idea id from idea, with a specific text and author.
     * @param voter idea's author.
     * @param text text of idea.
     * @return Unique idea id.
     * @throws ServerException if no idea has such an author and text.
     */
    public String getKey(Voter voter, String text) throws ServerException {
        for (Idea idea : ideas) {
            if (idea.getAuthor() == voter && idea.getTextOfIdea().equals(text)) {
                return idea.getKey();
            }
        }
        throw new ServerException(ExceptionErrorCode.IDEA_NOT_FOUND);
    }
    //REVU: думаю этот метод здесь нам не нужен
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdeaService that = (IdeaService) o;
        return ideas.equals(that.ideas);
    }
    //REVU: думаю этот метод здесь нам не нужен
    @Override
    public int hashCode() {
        return Objects.hash(ideas);
    }
}
