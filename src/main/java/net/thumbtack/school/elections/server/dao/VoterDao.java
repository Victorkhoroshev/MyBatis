package net.thumbtack.school.elections.server.dao;

import net.thumbtack.school.elections.server.model.Voter;
import net.thumbtack.school.elections.server.exeption.ServerException;
import java.util.Set;

public interface VoterDao {

    Voter get(String s) throws ServerException;

    Set<Voter> getAll();

    Voter save(Voter voter) throws ServerException;

    Voter getVoterByToken(String token) throws ServerException;
}