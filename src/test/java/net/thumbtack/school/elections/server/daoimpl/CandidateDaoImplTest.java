package net.thumbtack.school.elections.server.daoimpl;

import net.thumbtack.school.elections.server.Server;
import net.thumbtack.school.elections.server.dao.CandidateDao;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.model.Candidate;
import net.thumbtack.school.elections.server.model.Voter;
import net.thumbtack.school.elections.server.service.ExceptionErrorCode;
import net.thumbtack.school.elections.server.service.ServerException;
import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CandidateDaoImplTest {
    private final Server server = new Server();
    private final Database database = Database.getInstance();
    private final CandidateDao dao = new CandidateDaoImpl();

    @Test
    public void saveTest() throws IOException, ClassNotFoundException {
        server.startServer(null);
        Candidate candidate = new Candidate(new Voter("Виктор", "Хорошев",
                "Пригородная", 21, 188, "victor@khoroshev.net"," 1111"));
        Candidate candidate1 = new Candidate(new Voter("Виктор", "Хорошев",
                "Пригородная", 21, 188, "victor.net","121231"));
        dao.save(candidate);
        dao.save(candidate1);
        assertAll(
                () ->assertEquals(1, database.getCandidateSet().size()),
                () ->assertTrue(database.getCandidateSet().contains(candidate))
        );
        server.stopServer(null);
    }

    @Test
    public void getTest() throws IOException, ClassNotFoundException, ServerException {
        server.startServer(null);
        Candidate candidate = new Candidate(new Voter("Виктор", "Хорошев",
                "Пригородная", 21, 188, "victor@khoroshev.net"," 1111"));
        dao.save(candidate);
        assertEquals(candidate, dao.get("victor@khoroshev.net"));
        assertNull(dao.get("1"));
        server.stopServer(null);
    }

    @Test
    public void containsTest() throws IOException, ClassNotFoundException {
        server.startServer(null);
        Candidate candidate = new Candidate(new Voter("Виктор", "Хорошев",
                "Пригородная", 21, 188, "victor@khoroshev.net"," 1111"));
        dao.save(candidate);
        assertTrue(dao.contains("victor@khoroshev.net"));
        assertFalse(dao.contains("1"));
        server.stopServer(null);
    }

    @Test
    public void deleteTest() throws IOException, ClassNotFoundException {
        server.startServer(null);
        Candidate candidate = new Candidate(new Voter("Виктор", "Хорошев",
                "Пригородная", 21, 188, "victor@khoroshev.net"," 1111"));
        Candidate candidate1 = new Candidate(new Voter("Вир", "Хорошев",
                "Пригородная", 21, 188, "victor@khoroshev.net"," 1111"));
        dao.save(candidate);
        dao.delete(candidate1);
        assertEquals(1, database.getCandidateSet().size());
        assertTrue(database.getCandidateSet().contains(candidate));
        dao.delete(candidate);
        assertFalse(database.getCandidateSet().contains(candidate));
        assertEquals(0, database.getCandidateSet().size());
        server.stopServer(null);
    }
}