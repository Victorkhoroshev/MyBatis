package net.thumbtack.school.elections.server.model;

import java.io.Serializable;
import java.util.*;

public class Candidate extends Voter implements Serializable {
    private static final long serialVersionUID = 2L;

    private final Voter voter;

    public Candidate(Voter candidate) {
        super(candidate.getFirstName(), candidate.getLastName(), candidate.getPatronymic(),
                candidate.getStreet(), candidate.getHouse(), candidate.getApartment(),
                candidate.getLogin(), candidate.getPassword());
        this.voter = candidate;
    }

    public Voter getVoter() {
        return voter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate = (Candidate) o;
        return Objects.equals(voter, candidate.voter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(voter);
    }
}