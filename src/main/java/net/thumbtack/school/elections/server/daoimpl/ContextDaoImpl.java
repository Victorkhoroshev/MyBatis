package net.thumbtack.school.elections.server.daoimpl;

import net.thumbtack.school.elections.server.dao.ContextDao;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.model.Commissioner;
import net.thumbtack.school.elections.server.model.Context;
import java.util.*;

public class ContextDaoImpl implements ContextDao {
    private final Database database = Database.getInstance();

    /**
     * Create new candidate's set, new voter's set, new login's set and already filled with three commissioner: commissioner's set.
     */
    public ContextDaoImpl() {
        Map<String, Commissioner> commissionerMap = new HashMap<>();
        commissionerMap.put("victor.net", new Commissioner("victor.net", "25345Qw&&", true));
        commissionerMap.put("egor.net", new Commissioner("egor.net", "3456eR&21", false));
        commissionerMap.put("igor.net", new Commissioner("igor.net", "77??SDSw23", false));
        database.setCandidateMap(new HashMap<>());
        database.setVoterMap(new HashMap<>());
        database.setCommissionerMap(commissionerMap);
    }

    /**
     * Set new state for program.
     * @param context previous state of program.
     */
    public ContextDaoImpl(Context context) {
        database.setCandidateMap(context.getCandidateMap());
        database.setVoterMap(context.getVoterMap());
        database.setCommissionerMap(context.getCommissionerMap());
    }

    /**
     * Set candidate's, voter's, login's and commissioner's set.
     * @param context state of program.
     */
    @Override
    public void sync(Context context) {
        context.setCandidateMap(database.getCandidateMap());
        context.setVoterMap(database.getVoterMap());
        context.setCommissionerMap(database.getCommissionerMap());
    }
}