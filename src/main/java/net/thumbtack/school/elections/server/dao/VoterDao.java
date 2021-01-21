package net.thumbtack.school.elections.server.dao;

import net.thumbtack.school.elections.server.service.ServerException;
import java.util.Set;

public interface VoterDao<T> {
    T getVoter(String s) throws ServerException;

    Set<T> getAllVoters();

    void saveVoter(T t) throws ServerException;
}
