package net.thumbtack.school.elections.server.database;

import net.thumbtack.school.elections.server.model.Candidate;
import net.thumbtack.school.elections.server.model.Commissioner;
import net.thumbtack.school.elections.server.model.Voter;
import java.util.*;

public class Database {
    private Map<String, Candidate> candidateMap;
    private Map<String,Voter> voterMap;
    private Map<String, Commissioner> commissionerMap;
    private static Database instance;

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
            instance.setCandidateMap(new HashMap<>());
            instance.setVoterMap(new HashMap<>());
            instance.setCommissionerMap(new HashMap<>());
        }
        return instance;
    }

    public Map<String, Commissioner> getCommissionerMap() {
        return commissionerMap;
    }

    public Commissioner getCommissionerByLogin(String login) {
        return getCommissionerMap().get(login);
    }

    public Map<String, Candidate> getCandidateMap() {
        return candidateMap;
    }

    public Candidate getCandidateByLogin(String login) {
        return getCandidateMap().get(login);
    }

    public Map<String, Voter> getVoterMap() {
        return voterMap;
    }

    public Voter getVoterByLogin(String login) {
        return getVoterMap().get(login);
    }

    public void setCommissionerMap(Map<String, Commissioner> commissionerMap) {
        this.commissionerMap = commissionerMap;
    }

    public void setCandidateMap(Map<String, Candidate> candidateMap) {
        this.candidateMap = candidateMap;
    }

    public void setVoterMap(Map<String, Voter> voterMap) {
        this.voterMap = voterMap;
    }
}