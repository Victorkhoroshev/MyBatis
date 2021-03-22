package net.thumbtack.school.elections.server.database;

import net.thumbtack.school.elections.server.model.Candidate;
import net.thumbtack.school.elections.server.model.Commissioner;
import net.thumbtack.school.elections.server.model.Voter;
import java.util.*;

public class Database {
    private Set<Candidate> candidateSet;
    private Set<Voter> voterSet;
    private List<String> logins;
    private Set<Commissioner> commissionerSet;
    private static Database instance;

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
            instance.setCandidateSet(new HashSet<>());
            instance.setVoterSet(new HashSet<>());
            instance.setLogins(new ArrayList<>());
            instance.setCommissionerSet(new HashSet<>());
        }
        return instance;
    }

    public Set<Commissioner> getCommissionerSet() {
        return commissionerSet;
    }

    //REVU: возвращать весь Set и перебирать в нем каждый элемент не самое лучшее решение. Сделайте Map<Login, Candidate>
    // и добавьте метод getCandidateByLogin.
    public Set<Candidate> getCandidateSet() {
        return candidateSet;
    }

    //REVU: тоже самое
    public Set<Voter> getVoterSet() {
        return voterSet;
    }

    //REVU: нужен ли будет нам этот список, если у нас уже будут мапы? Причем мапы сразу будут говорить о наличии дубликатов.
    public List<String> getLogins() {
        return logins;
    }

    public void setCommissionerSet(Set<Commissioner> commissionerSet) {
        this.commissionerSet = commissionerSet;
    }

    public void setCandidateSet(Set<Candidate> candidateSet) {
        this.candidateSet = candidateSet;
    }

    public void setVoterSet(Set<Voter> voterSet) {
        this.voterSet = voterSet;
    }

    public void setLogins(List<String> logins) {
        this.logins = logins;
    }
}