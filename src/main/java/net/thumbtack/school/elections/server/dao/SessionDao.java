package net.thumbtack.school.elections.server.dao;

import net.thumbtack.school.elections.server.exeption.ServerException;
import net.thumbtack.school.elections.server.model.Commissioner;
import net.thumbtack.school.elections.server.model.Session;
import net.thumbtack.school.elections.server.model.Voter;

public interface SessionDao {

    Session getVoterSession(Voter voter) throws ServerException;

    Session getCommissionerSession(Commissioner commissioner) throws ServerException;

    boolean isLogin(String token);

    Session loginVoter(Voter voter, Session session);

    Session loginCommissioner(Commissioner commissioner, Session session);

    void logoutVoter(Voter voter);

    void logoutCommissioner(Commissioner commissioner);

}
