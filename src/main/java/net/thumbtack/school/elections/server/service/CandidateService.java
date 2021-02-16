package net.thumbtack.school.elections.server.service;

import net.thumbtack.school.elections.server.dao.CandidateDao;
import net.thumbtack.school.elections.server.daoimpl.CandidateDaoImpl;
import net.thumbtack.school.elections.server.model.Candidate;
import net.thumbtack.school.elections.server.model.Idea;
import net.thumbtack.school.elections.server.model.Voter;
import java.io.Serializable;
import java.util.*;

public class CandidateService implements Serializable {
    private final transient CandidateDao<Candidate> dao;
    private final Map<Candidate, List<Idea>> ideas;
    private final transient ContextService contextService;

    public CandidateService(ContextService contextService) {
        this.contextService = contextService;
        dao = new CandidateDaoImpl();
        ideas = new HashMap<>();
    }
    /**
     * Add new not confirmed candidate in database, if voter has not own candidate.
     * @param voter the voter who want to nominate.
     * @param candidate the voter they want to nominate.
     * @throws ServerException if election start.
     */
    public void addCandidate(Voter voter, Voter candidate) throws ServerException {
        //REVU: поменяйте местами, если голосование начато, то кидаем эксепшн. Else тогда можно будет убрать
        if (!contextService.isElectionStart()) {
            if (!voter.isHasOwnCandidate() && !dao.contains(candidate.getLogin())) {
                dao.save(new Candidate(candidate));
            }
            voter.setHasOwnCandidate(true);
        } else {
            throw new ServerException(ExceptionErrorCode.ELECTION_START);
        }
    }

    /**
     * Voter confirm yourself candidacy and presents his program.
     * @param voter voter, who wants to become a candidate.
     * @param candidateIdeas list with text of idea.
     * @throws ServerException if database not contains voter's login or election start.
     */
    public void confirmationCandidacy (Voter voter, List<Idea> candidateIdeas) throws ServerException {
        //REVU: поменяйте местами, если голосование начато, то кидаем эксепшн. Else тогда можно будет убрать
        if (!contextService.isElectionStart()) {
            if (dao.contains(voter.getLogin())) {
                ideas.put(dao.get(voter.getLogin()), candidateIdeas);
                voter.setHasOwnCandidate(true);
            } else if (!voter.isHasOwnCandidate()) {
                Candidate candidate = new Candidate(voter);
                dao.save(candidate);
                ideas.put(candidate, candidateIdeas);
                voter.setHasOwnCandidate(true);
            }
        } else {
            throw new ServerException(ExceptionErrorCode.ELECTION_START);
        }
    }

    /**
     * Candidate withdraw yourself candidacy.
     * @param voter candidate for withdraw candidacy.
     * @throws ServerException if database not contains voter's login or election start.
     */
    public void withdrawCandidacy (Voter voter) throws ServerException {
        //REVU: поменяйте местами, если голосование начато, то кидаем эксепшн. Else тогда можно будет убрать
        if (!contextService.isElectionStart()) {
            ideas.remove(dao.get(voter.getLogin()));
            dao.delete(dao.get(voter.getLogin()));
        } else {
            throw new ServerException(ExceptionErrorCode.ELECTION_START);
        }
    }

    /**
     * Candidate verification.
     * @param voter candidate.
     * @return If ideas map contains this candidate: true.
     * If ideas map not contains this candidate: false.
     */
    public boolean isCandidate(Voter voter) {
        return ideas.containsKey(new Candidate(voter));
    }

    /**
     * Candidate add new idea into ideas map.
     * @param voter candidate.
     * @param idea new candidate's Idea.
     * @throws ServerException if database not contains this voter or election start.
     */
    public void addIdea(Voter voter, Idea idea) throws ServerException {
        //REVU: поменяйте местами, если голосование начато, то кидаем эксепшн. Else тогда можно будет убрать
        if (!contextService.isElectionStart()) {
            ideas.get(dao.get(voter.getLogin())).add(idea);
        } else {
            throw new ServerException(ExceptionErrorCode.ELECTION_START);
        }
    }

    /**
     * Candidate remove not yourself idea.
     * @param voter candidate.
     * @param idea that will later be deleted.
     * @throws ServerException if database not contains this voter or election start.
     */
    public void removeIdea(Voter voter, Idea idea) throws ServerException {
        //REVU: поменяйте местами, если голосование начато, то кидаем эксепшн. Else тогда можно будет убрать
        if (!contextService.isElectionStart()) {
            if (idea.getAuthor() != voter) {
                ideas.get(dao.get(voter.getLogin())).remove(idea);
            }
        } else {
            throw new ServerException(ExceptionErrorCode.ELECTION_START);
        }
    }

    public Map<Candidate, List<Idea>> getCandidateMap() {
        return ideas;
    }

    public Set<Candidate> getCandidateSet() {
        return ideas.keySet();
    }

    /**
     * Get candidate into candidate's map by login.
     * @param login unique candidate's field.
     * @return If ideas map contains candidate with this login: candidate.
     * If login equals null : null.
     * @throws ServerException if in ideas map not contains candidate with this login.
     */
    public Candidate getCandidate(String login) throws ServerException {
        if (login != null) {
            for (Candidate candidate : ideas.keySet()) {
                if (candidate.getVoter().getLogin().equals(login)) {
                    return candidate;
                }
            }
            throw new ServerException(ExceptionErrorCode.CANDIDATE_NOT_FOUND);
        }
        return null;
    }

    //REVU: думаю этот метод здесь нам не нужен
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CandidateService that = (CandidateService) o;
        return ideas.equals(that.ideas);
    }
    //REVU: думаю этот метод здесь нам не нужен
    @Override
    public int hashCode() {
        return Objects.hash(dao, ideas);
    }
}
