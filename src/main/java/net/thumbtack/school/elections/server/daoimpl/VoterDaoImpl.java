package net.thumbtack.school.elections.server.daoimpl;

import net.thumbtack.school.elections.server.dao.VoterDao;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.model.Voter;
import net.thumbtack.school.elections.server.service.ServerException;
import net.thumbtack.school.elections.server.service.ExceptionErrorCode;
import java.util.*;

public class VoterDaoImpl implements VoterDao<Voter> {

    /**
     * Get voter by his login from database.
     * @param login the login voter, who already logged out from the server.
     * @return The voter who owns this login.
     * @throws ServerException if login not found in database voter set.
     */
    @Override
    public Voter get(String login) throws ServerException {
        Voter voter;
        for (Voter value : Database.getVoterSet()) {
            voter = value;
            if (voter.getLogin().equals(login)) {
                return voter;
            }
        }
        //REVU: это ошибка сервис уровня, дао просто должно вернуть null
        throw new ServerException(ExceptionErrorCode.NOT_FOUND);
    }

    /**
     * Get all voters from database.
     * @return Voter's set, who already register.
     */
    @Override
    public Set<Voter> getAll() {
        return Database.getVoterSet();
    }

    /**
     * Put voter and his login in database.
     * @param voter new voter for database.
     * @throws ServerException if voter already contain in database or his login already exists.
     */
    @Override
    public void save(Voter voter) throws ServerException {
        if (Database.getVoterSet().contains(voter)){
            throw new ServerException(ExceptionErrorCode.ALREADY_EXISTS);
        }
        for (String s : Database.getLogins()) {
            if (voter.getLogin().equals(s)) {
                throw new ServerException(ExceptionErrorCode.LOGIN_ALREADY_EXISTS);
            }
        }
        Database.getVoterSet().add(voter);
        Database.getLogins().add(voter.getLogin());
    }
}
