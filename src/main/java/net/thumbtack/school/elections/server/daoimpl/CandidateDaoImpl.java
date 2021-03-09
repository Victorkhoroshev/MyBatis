package net.thumbtack.school.elections.server.daoimpl;

import net.thumbtack.school.elections.server.dao.CandidateDao;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.model.Candidate;
import net.thumbtack.school.elections.server.service.ExceptionErrorCode;
import net.thumbtack.school.elections.server.service.ServerException;

public class CandidateDaoImpl implements CandidateDao {
    private final Database database = Database.getInstance();
    /**
     * Get candidate by his login from database.
     * @param login the login candidate, who already logged out from the server.
     * @return The candidate who owns this login or null if candidate not found.
     */
    @Override
    public Candidate get(String login) {
        Candidate candidate;
        for (Candidate value : database.getCandidateSet()) {
            candidate = value;
            if (candidate.getVoter().getLogin().equals(login)) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * Save candidate in database.
     * @param candidate new candidate for election.
     */
    @Override
    public Candidate save(Candidate candidate) {
        database.getCandidateSet().add(candidate);
        return get(candidate.getVoter().getLogin());
    }

    /**
     * Checks: does database store candidate with this login.
     * @param login the login candidate, who already logged out from the server.
     * @return If database contain candidate with this login: true.
     * If database not contain candidate with this login: false.
     */
    @Override
    public boolean contains(String login) {
        for (Candidate candidate : database.getCandidateSet()) {
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
        database.getCandidateSet().remove(candidate);
    }
}