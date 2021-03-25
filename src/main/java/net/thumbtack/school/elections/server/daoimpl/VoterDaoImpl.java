package net.thumbtack.school.elections.server.daoimpl;

import net.thumbtack.school.elections.server.dao.VoterDao;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.model.Voter;
import net.thumbtack.school.elections.server.exeption.ServerException;
import net.thumbtack.school.elections.server.exeption.ExceptionErrorCode;
import java.util.*;

public class VoterDaoImpl implements VoterDao {
    private final Database database;

    public VoterDaoImpl() {
        database = Database.getInstance();
    }

    /**
     * Get voter by his login from database.
     * @param login the login voter, who already logged out from the server.
     * @return The voter who owns this login or null if voter not found.
     */
    @Override
    public Voter get(String login) {
        return database.getVoterByLogin(login);
    }

    /**
     * Get all voters from database.
     * @return Voter's set, who already register.
     */
    @Override
    public Set<Voter> getAll() {
        return new HashSet<>(database.getVoterMap().values());
    }

    /**
     * Put voter and his login in database.
     * @param voter new voter for database.
     * @throws ServerException if voter already contain in database or his login already exists.
     */
    @Override
    public Voter save(Voter voter) throws ServerException {
        if (database.getVoterMap().containsValue(voter)) {
            throw new ServerException(ExceptionErrorCode.ALREADY_EXISTS);
        }
        if (database.getVoterMap().containsKey(voter.getLogin()) ||
                database.getCommissionerMap().containsKey(voter.getLogin())) {
            throw new ServerException(ExceptionErrorCode.LOGIN_ALREADY_EXISTS);
        }
        return database.getVoterMap().put(voter.getLogin(), voter);
    }
}