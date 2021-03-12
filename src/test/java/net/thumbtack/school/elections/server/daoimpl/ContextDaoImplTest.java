package net.thumbtack.school.elections.server.daoimpl;

import net.thumbtack.school.elections.server.Server;
import net.thumbtack.school.elections.server.dao.ContextDao;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.model.Candidate;
import net.thumbtack.school.elections.server.model.Context;
import net.thumbtack.school.elections.server.model.Voter;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ContextDaoImplTest {
    private final Server server = new Server();
    private final ContextDao dao = new ContextDaoImpl();
    private final Database database = Database.getInstance();
    @Test
    public void syncTest() throws IOException, ClassNotFoundException {
        server.startServer(null);
        assertAll(
                () -> assertEquals(3, database.getLogins().size()),
                () -> assertEquals(0, database.getVoterSet().size()),
                () -> assertEquals(0, database.getCandidateSet().size())
        );
        Context context = new Context();
        Voter voter1 = new Voter("Виктор", "Хорошев",
                "Пригородная", 21, 188, "victor@khoroshev.net"," 1111");
        Voter voter2 = new Voter("Андрей", "Хорышев",
                "Пригородная", 21, 188, "victooroshev.net"," 1111");
        Candidate candidate1 = new Candidate(voter1);
        Candidate candidate2 = new Candidate(voter2);
        List<String> logins = new ArrayList<>(Arrays.asList("victor@khoroshev.net", "victooroshev.net"));
        Set<Voter> voterSet = new HashSet<>(Arrays.asList(voter1, voter2));
        Set<Candidate> candidateSet = new HashSet<>(Arrays.asList(candidate1, candidate2));
        database.setLogins(logins);
        database.setVoterSet(voterSet);
        database.setCandidateSet(candidateSet);
        dao.sync(context);
        assertAll(
                () -> assertEquals(voterSet, database.getVoterSet()),
                () -> assertEquals(logins, database.getLogins()),
                () -> assertEquals(candidateSet, database.getCandidateSet())
        );

        List<String> logins1 = new ArrayList<>();
        Set<Voter> voterSet1 = new HashSet<>();
        Set<Candidate> candidateSet1 = new HashSet<>();
        context.setLogins(logins1);
        context.setVoterSet(voterSet1);
        context.setCandidateSet(candidateSet1);
        ContextDao daoWithContext = new ContextDaoImpl(context);
        assertAll(
                () -> assertEquals(logins1, database.getLogins()),
                () -> assertEquals(voterSet1, database.getVoterSet()),
                () -> assertEquals(candidateSet1, database.getCandidateSet())
        );
        server.stopServer(null);
    }
}