package net.thumbtack.school.elections.server.daoimpl;

import net.thumbtack.school.elections.server.dao.CommissionerDao;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.model.Commissioner;
import net.thumbtack.school.elections.server.service.ExceptionErrorCode;
import net.thumbtack.school.elections.server.service.ServerException;
import java.util.ArrayList;
import java.util.List;

public class CommissionerDaoImpl implements CommissionerDao<Commissioner> {

    public CommissionerDaoImpl() {
    }

    /**
     * Get all logins from database.
     * @return List of commissioner's login.
     */
    public List<String> getLogins() {
        List<String> logins = new ArrayList<>();
        for (Commissioner commissioner: Database.getCommissionerSet()) {
            logins.add(commissioner.getLogin());
        }
        return logins;
    }

    /**
     * Get commissioner by his login from database.
     * @param login commissioner's login.
     * @return The commissioner who owns this login.
     * @throws ServerException if login not found in database commissioner set.
     */
    public Commissioner get(String login) throws ServerException {
        for (Commissioner commissioner : Database.getCommissionerSet()) {
            if (commissioner.getLogin().equals(login)) {
                return commissioner;
            }
        }
        //REVU: это ошибка сервис уровня, дао просто должно вернуть null
        throw new ServerException(ExceptionErrorCode.NOT_FOUND);
    }
}
