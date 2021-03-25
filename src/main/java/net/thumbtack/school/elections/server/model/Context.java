package net.thumbtack.school.elections.server.model;

import net.thumbtack.school.elections.server.service.CandidateService;
import net.thumbtack.school.elections.server.service.ElectionService;
import net.thumbtack.school.elections.server.service.IdeaService;
import java.io.Serializable;
import java.util.Map;

public class Context implements Serializable {
    private static Context context;
    private Map<String, Candidate> candidateMap;
    private Map<String, Voter> voterMap;
    private CandidateService candidateService;
    private IdeaService ideaService;
    private ElectionService electionService;
    private Map<String, Commissioner> commissionerMap;
    private Boolean isElectionStart;
    private Boolean isElectionStop;

    public static Context getInstance()
    {
        if (context == null)
            context = new Context();
        return context;
    }

    public Boolean getElectionStop() {
        return isElectionStop;
    }

    public void setElectionStop(Boolean electionStop) {
        isElectionStop = electionStop;
    }

    public Boolean getElectionStart() {
        return isElectionStart;
    }

    public void setElectionStart(Boolean electionStart) {
        isElectionStart = electionStart;
    }

    public Map<String, Candidate> getCandidateMap() {
        return candidateMap;
    }

    public void setCandidateMap(Map<String, Candidate> candidateMap) {
        this.candidateMap = candidateMap;
    }

    public Map<String, Voter> getVoterMap() {
        return voterMap;
    }

    public void setVoterMap(Map<String, Voter> voterMap) {
        this.voterMap = voterMap;
    }

    public Map<String, Commissioner> getCommissionerMap() {
        return commissionerMap;
    }

    public void setCommissionerMap(Map<String, Commissioner> commissionerMap) {
        this.commissionerMap = commissionerMap;
    }

    public CandidateService getCandidateService() {
        return candidateService;
    }

    public void setCandidateService(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    public IdeaService getIdeaService() {
        return ideaService;
    }

    public void setIdeaService(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    public ElectionService getElectionService() {
        return electionService;
    }

    public void setElectionService(ElectionService electionService) {
        this.electionService = electionService;
    }
}