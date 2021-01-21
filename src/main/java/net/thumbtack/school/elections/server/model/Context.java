package net.thumbtack.school.elections.server.model;

import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.service.CandidateService;
import net.thumbtack.school.elections.server.service.IdeaService;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Set;

public class Context {
    private static Context context;
    private Set<Candidate> candidateSet;
    private Set<Voter> voterSet;
    private List<String> logins;
    private CandidateService candidateService;
    private IdeaService ideaService;

    public static Context getInstance()
    {
        if (context == null)
            context = new Context();
        return context;
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
}
