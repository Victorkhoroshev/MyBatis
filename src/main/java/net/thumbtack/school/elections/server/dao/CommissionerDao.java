package net.thumbtack.school.elections.server.dao;

import net.thumbtack.school.elections.server.model.Commissioner;
import net.thumbtack.school.elections.server.exeption.ServerException;
import java.util.List;

public interface CommissionerDao {

    List<String> getLogins();

    Commissioner get(String login) throws ServerException;

    boolean contain(String login);
}