package net.thumbtack.school.elections.server.daoimpl;

import net.thumbtack.school.elections.server.dao.SessionDao;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.exeption.ExceptionErrorCode;
import net.thumbtack.school.elections.server.exeption.ServerException;
import net.thumbtack.school.elections.server.model.Commissioner;
import net.thumbtack.school.elections.server.model.Session;
import net.thumbtack.school.elections.server.model.Voter;
import org.junit.jupiter.api.Test;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class SessionDaoImplTest {
    private SessionDao dao = new SessionDaoImpl();

    @Test
    public void getVoterSessionTest_Success() throws ServerException {
        Voter voter = new Voter("Виктор", "Хорошеев",
                "Пригородная", 21, 188, "victoroshev.net"," 1111");
        Voter voter2 = new Voter("Вктор", "Хорошеев",
                "Пригородная", 21, 188, "victoroshev.net"," 1111");
        Database.getInstance().getVoterSessions().put(voter, new Session("11"));
        Database.getInstance().getVoterSessions().put(voter2, new Session("2"));
        assertEquals(new Session("11"), dao.getVoterSession(voter));
        Database.getInstance().setVoterSessions(new HashMap<>());
    }

    @Test
    public void getVoterSessionTest_Logout() {
        Voter voter = new Voter("Викто", "Хорошеев",
                "Пригородная", 21, 188, "victorshev.net"," 1111");
        try {
            dao.getVoterSession(voter);
        } catch (ServerException ex) {
            assertEquals(ex.getErrorCode(), ExceptionErrorCode.LOGOUT);
        }
    }

    @Test
    public void getCommissionerSessionTest_Success() throws ServerException {
        Commissioner commissioner = new Commissioner("victor.net", "25345Qw&&", true);
        Commissioner commissioner2 = new Commissioner("vict.net", "25345Qw&&", true);
        Database.getInstance().getCommissionerSessions().put(commissioner, new Session("13"));
        Database.getInstance().getCommissionerSessions().put(commissioner2, new Session("2"));
        assertEquals(new Session("13"), dao.getCommissionerSession(commissioner));
        Database.getInstance().setCommissionerSessions(new HashMap<>());
    }

    @Test
    public void getCommissionerSessionTest_Logout() {
        Commissioner commissioner = new Commissioner("victor.net", "25345Qw&&", true);
        try {
            dao.getCommissionerSession(commissioner);
        } catch (ServerException ex) {
            assertEquals(ex.getErrorCode(), ExceptionErrorCode.LOGOUT);
        }
    }

    @Test
    public void isLoginTest_True() {
        Commissioner commissioner = new Commissioner("victor.net", "25345Qw&&", true);
        Commissioner commissioner1 = new Commissioner("victor.ne", "25345Qw&&", true);
        Commissioner commissioner2 = new Commissioner("victor.n", "25345Qw&&", true);
        Voter voter = new Voter("Виктор", "Хорошеев",
                "Пригородная", 21, 188, "victooshev.net"," 1111");
        Voter voter1 = new Voter("Виктор", "Хорошеев",
                "Пригородная", 21, 188, "victoroshev.net"," 1111");
        Voter voter2 = new Voter("Виктор", "Хорошеев",
                "Пригородная", 21, 188, "victorhev.net"," 1111");
        Database.getInstance().getCommissionerSessions().put(commissioner, new Session("1"));
        Database.getInstance().getCommissionerSessions().put(commissioner1, new Session("2"));
        Database.getInstance().getCommissionerSessions().put(commissioner2, new Session("3"));
        Database.getInstance().getVoterSessions().put(voter, new Session("4"));
        Database.getInstance().getVoterSessions().put(voter1, new Session("5"));
        Database.getInstance().getVoterSessions().put(voter2, new Session("6"));
        assertTrue(dao.isLogin("3"));
        assertTrue(dao.isLogin("6"));
        assertFalse(dao.isLogin("98"));
        Database.getInstance().setVoterSessions(new HashMap<>());
        Database.getInstance().setCommissionerSessions(new HashMap<>());
    }

    @Test
    public void loginVoterTest() {
        Voter voter = new Voter("Виктор", "Хорошеев",
                "Пригородная", 21, 188, "victooshev.net"," 1111");
        assertEquals(new Session("1"), dao.loginVoter(voter, new Session("1")));
        Database.getInstance().setVoterSessions(new HashMap<>());
    }

    @Test
    public void loginCommissionerTest() {
        Commissioner commissioner = new Commissioner("victor.net", "25345Qw&&", true);
        assertEquals(new Session("1"), dao.loginCommissioner(commissioner, new Session("1")));
        Database.getInstance().setCommissionerSessions(new HashMap<>());
    }

    @Test
    public void logoutVoterTest() {
        Voter voter = new Voter("Виктор", "Хорошеев",
                "Пригородная", 21, 188, "victooshev.net"," 1111");
        Database.getInstance().getVoterSessions().put(voter, new Session("1"));
        dao.logoutVoter(voter);
        assertFalse(Database.getInstance().getVoterSessions().containsKey(voter));
    }

    @Test
    public void logoutCommissionerTest() {
        Commissioner commissioner = new Commissioner("victor.net", "25345Qw&&", true);
        Database.getInstance().getCommissionerSessions().put(commissioner, new Session("1"));
        dao.logoutCommissioner(commissioner);
        assertFalse(Database.getInstance().getCommissionerSessions().containsKey(commissioner));
    }

}
