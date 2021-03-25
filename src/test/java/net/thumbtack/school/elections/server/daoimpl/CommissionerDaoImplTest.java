package net.thumbtack.school.elections.server.daoimpl;

import net.thumbtack.school.elections.server.dao.CommissionerDao;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.model.Commissioner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CommissionerDaoImplTest {
    private CommissionerDao dao;

    @BeforeEach
    void setUp() {
        dao = new CommissionerDaoImpl();
        Map<String, Commissioner> commissionerMap = new HashMap<>();
        commissionerMap.put("victor.net", new Commissioner("victor.net", "25345Qw&&", true));
        commissionerMap.put("egor.net", new Commissioner("egor.net", "3456eR&21", false));
        commissionerMap.put("igor.net", new Commissioner("igor.net", "77??SDSw23", false));
        Database.getInstance().setCommissionerMap(commissionerMap);
    }

    @Test
    public void getTest() {
        Commissioner commissioner1 = new Commissioner("victor.net", "25345Qw&&", true);
        Commissioner commissioner2 = new Commissioner("egor.net", "3456eR&21", false);
        assertAll(
                () -> assertEquals(commissioner1, dao.get(commissioner1.getLogin())),
                () -> assertEquals(commissioner2, dao.get(commissioner2.getLogin())),
                () -> assertNull(dao.get("1"))
        );
    }

    @Test
    public void getLoginsTest() {
        assertAll(
                () -> assertTrue(dao.getLogins().contains("victor.net")),
                () -> assertTrue(dao.getLogins().contains("egor.net")),
                () -> assertTrue(dao.getLogins().contains("igor.net")),
                () -> assertEquals(3, dao.getLogins().size())
        );
    }

    @Test
    public void containTest_Success() {
        assertTrue(dao.contain("igor.net"));
    }

    @Test
    public void containTest_Not_Contain() {
        assertFalse(dao.contain("andrey.net"));
    }

}