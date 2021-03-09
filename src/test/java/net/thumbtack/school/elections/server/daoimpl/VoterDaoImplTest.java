package net.thumbtack.school.elections.server.daoimpl;
import net.thumbtack.school.elections.server.Server;
import net.thumbtack.school.elections.server.dao.VoterDao;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.model.Voter;
import net.thumbtack.school.elections.server.service.ServerException;
import net.thumbtack.school.elections.server.service.ExceptionErrorCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class VoterDaoImplTest {
    private VoterDao dao;

    @BeforeEach
    void setUp() {
        dao = new VoterDaoImpl();
        Database.getInstance().setVoterSet(new HashSet<>());
    }

    @Test
    public void saveTest() throws ServerException {
        Voter voter = new Voter("Виктор", "Хорошеев",
                "Пригородная", 21, 188, "victoroshev.net"," 1111");
        Voter voter1 = new Voter("Виктор", "Хорошеев",
                "Пригородная", 21, 188, "victor.1net","121231");
        Voter voter2 = new Voter("Андрей", "Хорошев",
                "Пригородная", 21, 188, "victor@khoroshev.net"," 1111");
        Voter voter3 = new Voter("Андрей", "Хорышев",
                "Пригородная", 21, 188, "victooroshev.net"," 1111");
        dao.save(voter);
        try {
            dao.save(voter1);
            fail();
        } catch (ServerException ex) {
            assertEquals(ExceptionErrorCode.ALREADY_EXISTS, ex.getErrorCode());
        }
        dao.save(voter3);
        assertAll(
                () -> assertTrue(dao.getAll().contains(voter)),
                () -> assertTrue(dao.getAll().contains(voter3)),
                () -> assertFalse(dao.getAll().contains(voter2)),
                () -> assertEquals(2, dao.getAll().size())
        );
    }

    @Test
    public void getTest() throws ServerException {
        Voter voter = new Voter(randomString(), randomString(),
                randomString(), 21, 188, randomString()," 1111");
        Voter voter1 = new Voter(randomString(), randomString(),
                randomString(), 21, 188, "victor.1net","qQ%34231111");
        dao.save(voter);
        dao.save(voter1);
        assertEquals(voter1, dao.get("victor.1net"));
        assertNull(dao.get("1"));
    }

    @Test
    public void getAllTest() throws ServerException {
        Voter voter = new Voter(randomString(), randomString(),
                randomString(), 21, 188, randomString()," 1111");
        dao.save(voter);
        Set<Voter> voterSet = new HashSet<>(){{
            add(voter);
        }};
        assertEquals(voterSet, dao.getAll());
    }

    private String randomString() {
        Random random = new Random();
        char[] sAlphabet = "АБВГДЕЖЗИКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзиклмнопрстуфхцчшщъыьэюя".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 60; i++) {
            stringBuilder.append(sAlphabet[random.nextInt(sAlphabet.length)]);
        }
        return stringBuilder.toString();
    }
}