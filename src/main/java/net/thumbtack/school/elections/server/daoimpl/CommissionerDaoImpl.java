package net.thumbtack.school.elections.server.daoimpl;

import net.thumbtack.school.elections.server.dao.CommissionerDao;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.exeption.ExceptionErrorCode;
import net.thumbtack.school.elections.server.exeption.ServerException;
import net.thumbtack.school.elections.server.model.Commissioner;
import net.thumbtack.school.elections.server.model.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommissionerDaoImpl implements CommissionerDao {
    private final Database database;

    public CommissionerDaoImpl() {
        database = Database.getInstance();
    }

    /**
     * Get all logins from database.
     * @return List of commissioner's login.
     */
    public List<String> getLogins() {
        return new ArrayList<>(database.getCommissionerMap().keySet());
    }

    /**
     * Get commissioner by his login from database.
     * @param login commissioner's login.
     * @return The commissioner who owns this login or null if commissioner not found.
     */
    public Commissioner get(String login) {
        return database.getCommissionerByLogin(login);
    }

    @Override
    public boolean contain(String login) {
        return database.getCommissionerMap().containsKey(login);
    }

    @Override
    public Commissioner getCommissionerByToken(String token) throws ServerException {
        for (Map.Entry<Commissioner, Session> entry : database.getCommissionerSessions().entrySet()) {
            if (entry.getValue().getToken().equals(token)) {
                return entry.getKey();
            }
        }
        throw new ServerException(ExceptionErrorCode.LOGOUT);
    }

}