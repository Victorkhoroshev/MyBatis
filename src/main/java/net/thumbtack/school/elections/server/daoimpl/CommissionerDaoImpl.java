package net.thumbtack.school.elections.server.daoimpl;

import net.thumbtack.school.elections.server.dao.CommissionerDao;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.model.Commissioner;
import net.thumbtack.school.elections.server.service.ServerException;
import java.util.ArrayList;
import java.util.List;

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
        List<String> logins = new ArrayList<>();
        for (Commissioner commissioner: database.getCommissionerSet()) {
            logins.add(commissioner.getLogin());
        }
        return logins;
    }

    /**
     * Get commissioner by his login from database.
     * @param login commissioner's login.
     * @return The commissioner who owns this login or null if commissioner not found.
     */
    public Commissioner get(String login) throws ServerException {
        for (Commissioner commissioner : database.getCommissionerSet()) {
            if (commissioner.getLogin().equals(login)) {
                return commissioner;
            }
        }
        return null;
    }

    @Override
    public boolean contain(String login) {
        return database.getCommissionerSet().stream()
                .anyMatch(commissioner -> commissioner.getLogin().equals(login));
    }
}