package net.thumbtack.school.elections.server.daoimpl;

import net.thumbtack.school.elections.server.dao.ContextDao;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.model.Commissioner;
import net.thumbtack.school.elections.server.model.Context;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContextDaoImpl implements ContextDao {
    private final Database database = Database.getInstance();

    /**
     * Create new candidate's set, new voter's set, new login's set and already filled with three commissioner: commissioner's set.
     */
    public ContextDaoImpl() {
        Set<Commissioner> commissionerSet = new HashSet<>();
        List<String> logins = new ArrayList<>();
        commissionerSet.add(new Commissioner("victor.net", "25345Qw&&", true));
        commissionerSet.add(new Commissioner("egor.net", "3456eR&21", false));
        commissionerSet.add(new Commissioner("igor.net", "77??SDSw23", false));
        logins.add("victor@khoroshev.net");
        logins.add("egor@khoroshev.net");
        logins.add("igor@khoroshev.net");
        database.setCandidateSet(new HashSet<>());
        database.setVoterSet(new HashSet<>());
        database.setLogins(logins);
        database.setCommissionerSet(commissionerSet);
    }

    /**
     * Set new state for program.
     * @param context previous state of program.
     */
    public ContextDaoImpl(Context context) {
        database.setCandidateSet(context.getCandidateSet());
        database.setVoterSet(context.getVoterSet());
        database.setLogins(context.getLogins());
        database.setCommissionerSet(context.getCommissionerSet());
    }

    /**
     * Set candidate's, voter's, login's and commissioner's set.
     * @param context state of program.
     */
    @Override
    public void sync(Context context) {
        context.setCandidateSet(database.getCandidateSet());
        context.setVoterSet(database.getVoterSet());
        context.setLogins(database.getLogins());
        context.setCommissionerSet(database.getCommissionerSet());
    }
}