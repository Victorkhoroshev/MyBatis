package net.thumbtack.school.elections.server.daoimpl;

import net.thumbtack.school.elections.server.dao.CandidateDao;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.model.Candidate;
import net.thumbtack.school.elections.server.service.ExceptionErrorCode;
import net.thumbtack.school.elections.server.service.ServerException;

public class CandidateDaoImpl implements CandidateDao<Candidate> {

    /**
     * Get candidate by his login from database.
     * @param login the login candidate, who already logged out from the server.
     * @return The candidate who owns this login.
     * @throws ServerException if login not found in database's candidate's set.
     */
    @Override
    public Candidate get(String login) throws ServerException {
        Candidate candidate;
        //REVU: вынесите Database в отдельную переменную класса
        for (Candidate value : Database.getCandidateSet()) {
            candidate = value;
            if (candidate.getVoter().getLogin().equals(login)) {
                return candidate;
            }
        }
        throw new ServerException(ExceptionErrorCode.CANDIDATE_NOT_FOUND);
    }

    /**
     * Save candidate in database.
     * @param candidate new candidate for election.
     */
    @Override
    public void save(Candidate candidate) {
        //REVU: вынесите Database в отдельную переменную класса
        Database.getCandidateSet().add(candidate);
    }

    /**
     * Checks: does database store candidate with this login.
     * @param login the login candidate, who already logged out from the server.
     * @return If database contain candidate with this login: true.
     * If database not contain candidate with this login: false.
     */
    @Override
    public boolean contains(String login) {
        //REVU: вынесите Database в отдельную переменную класса
        for (Candidate candidate : Database.getCandidateSet()) {
            if (candidate.getVoter().getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Delete candidate from database.
     * @param candidate candidate for remove from database.
     */
    @Override
    public void delete(Candidate candidate) {
        //REVU: вынесите Database в отдельную переменную класса
        Database.getCandidateSet().remove(candidate);
    }
}
