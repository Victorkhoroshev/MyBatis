package net.thumbtack.school.elections.server.dao;

import net.thumbtack.school.elections.server.model.Candidate;
import net.thumbtack.school.elections.server.exeption.ServerException;

public interface CandidateDao {

    Candidate get(String s) throws ServerException;

    Candidate save(Candidate candidate);

    boolean contains(String s);

    void delete(Candidate t);
}