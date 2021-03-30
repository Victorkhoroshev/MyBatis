package net.thumbtack.school.elections.server.daoimpl;

import net.thumbtack.school.elections.server.dao.SessionDao;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.exeption.ExceptionErrorCode;
import net.thumbtack.school.elections.server.exeption.ServerException;
import net.thumbtack.school.elections.server.model.Commissioner;
import net.thumbtack.school.elections.server.model.Session;
import net.thumbtack.school.elections.server.model.Voter;

public class SessionDaoImpl implements SessionDao {
    private final Database database = Database.getInstance();

    @Override
    public Session getVoterSession(Voter voter) throws ServerException {
        if(!database.getVoterSessions().containsKey(voter)) {
            throw new ServerException(ExceptionErrorCode.LOGOUT);
        }
        return database.getVoterSessions().get(voter);
    }

    @Override
    public Session getCommissionerSession(Commissioner commissioner) throws ServerException {
        if(!database.getCommissionerSessions().containsKey(commissioner)) {
            throw new ServerException(ExceptionErrorCode.LOGOUT);
        }
        return database.getCommissionerSessions().get(commissioner);
    }

    @Override
    public boolean isLogin(String token) {
        for(Session session : database.getVoterSessions().values()) {
            if (session.getToken().equals(token)) {
                return true;
            }
        }
        for(Session session : database.getCommissionerSessions().values()) {
            if (session.getToken().equals(token)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Session loginVoter(Voter voter, Session session) {
        database.getVoterSessions().put(voter, session);
        return session;
    }

    @Override
    public Session loginCommissioner(Commissioner commissioner, Session session) {
        database.getCommissionerSessions().put(commissioner, session);
        return session;
    }

    @Override
    public void logoutVoter(Voter voter) {
        database.getVoterSessions().remove(voter);
    }

    @Override
    public void logoutCommissioner(Commissioner commissioner) {
        database.getCommissionerSessions().remove(commissioner);
    }
}
