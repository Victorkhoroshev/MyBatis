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
        Voter voter;
        for (Voter value : database.getVoterSet()) {
            voter = value;
            if (voter.getLogin().equals(login)) {
                return voter;
            }
        }
        return null;
    }

    /**
     * Get all voters from database.
     * @return Voter's set, who already register.
     */
    @Override
    public Set<Voter> getAll() {
        return database.getVoterSet();
    }

    /**
     * Put voter and his login in database.
     * @param voter new voter for database.
     * @throws ServerException if voter already contain in database or his login already exists.
     */
    @Override
    public Voter save(Voter voter) throws ServerException {
        if (database.getVoterSet().contains(voter)){
            throw new ServerException(ExceptionErrorCode.ALREADY_EXISTS);
        }
        for (String s : database.getLogins()) {
            if (voter.getLogin().equals(s)) {
                throw new ServerException(ExceptionErrorCode.LOGIN_ALREADY_EXISTS);
            }
        }
        database.getVoterSet().add(voter);
        database.getLogins().add(voter.getLogin());
        return voter;
    }
}