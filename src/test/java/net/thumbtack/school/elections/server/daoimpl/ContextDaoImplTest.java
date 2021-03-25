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
                () -> assertEquals(0, database.getVoterMap().size()),
                () -> assertEquals(0, database.getCandidateMap().size())
        );
        Context context = new Context();
        Voter voter1 = new Voter("Виктор", "Хорошев",
                "Пригородная", 21, 188, "victor@khoroshev.net"," 1111");
        Voter voter2 = new Voter("Андрей", "Хорышев",
                "Пригородная", 21, 188, "victooroshev.net"," 1111");
        Candidate candidate1 = new Candidate(voter1);
        Candidate candidate2 = new Candidate(voter2);
        Map<String, Voter> voterMap = new HashMap<>();
        Map<String, Candidate> candidateMap = new HashMap<>();
        voterMap.put(voter1.getLogin(), voter1);
        voterMap.put(voter2.getLogin(), voter2);
        candidateMap.put(candidate1.getLogin(), candidate1);
        candidateMap.put(candidate2.getLogin(), candidate2);
        database.setVoterMap(voterMap);
        database.setCandidateMap(candidateMap);
        dao.sync(context);
        assertAll(
                () -> assertEquals(voterMap, database.getVoterMap()),
                () -> assertEquals(candidateMap, database.getCandidateMap())
        );

        Map<String, Voter> voterMap1 = new HashMap<>();
        Map<String, Candidate> candidateMap1 = new HashMap<>();
        context.setVoterMap(voterMap1);
        context.setCandidateMap(candidateMap1);
        ContextDao daoWithContext = new ContextDaoImpl(context);
        assertAll(
                () -> assertEquals(voterMap1, database.getVoterMap()),
                () -> assertEquals(candidateMap1, database.getCandidateMap())
        );
        server.stopServer(null);
    }
}