package net.thumbtack.school.elections.server.model;

import net.thumbtack.school.elections.server.service.CandidateService;
import net.thumbtack.school.elections.server.service.ElectionService;
import net.thumbtack.school.elections.server.service.IdeaService;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class Context implements Serializable {
    private static Context context;
    private Set<Candidate> candidateSet;
    private Set<Voter> voterSet;
    private List<String> logins;
    private CandidateService candidateService;
    private IdeaService ideaService;
    private ElectionService electionService;
    private Set<Commissioner> commissionerSet;
    //REVU: нужен ли нам вообще это класс, если мы перенесем переменные isElectionStart и isElectionStop в ElectionService?
    // Из-за дто в сервисах появилась перекрестная зависимость между сервисами, для сохранения состояния сервера нужен контекст
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

    public Set<Commissioner> getCommissionerSet() {
        return commissionerSet;
    }

    public void setCommissionerSet(Set<Commissioner> commissionerSet) {
        this.commissionerSet = commissionerSet;
    }

    public Set<Candidate> getCandidateSet() {
        return candidateSet;
    }

    public void setCandidateSet(Set<Candidate> candidateSet) {
        this.candidateSet = candidateSet;
    }

    public Set<Voter> getVoterSet() {
        return voterSet;
    }

    public void setVoterSet(Set<Voter> voterSet) {
        this.voterSet = voterSet;
    }

    public List<String> getLogins() {
        return logins;
    }

    public void setLogins(List<String> logins) {
        this.logins = logins;
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