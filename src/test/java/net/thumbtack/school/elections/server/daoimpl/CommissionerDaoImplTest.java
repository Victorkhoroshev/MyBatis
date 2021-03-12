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
        Set<Commissioner> commissionerSet = new HashSet<>();
        commissionerSet.add(new Commissioner("victor.net", "25345Qw&&", true));
        commissionerSet.add(new Commissioner("egor.net", "3456eR&21", false));
        commissionerSet.add(new Commissioner("igor.net", "77??SDSw23", false));
        Database.getInstance().setCommissionerSet(commissionerSet);
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
        List<String> list = new ArrayList<>();
        list.add("igor.net");
        list.add("victor.net");
        list.add("egor.net");
        assertEquals(list, dao.getLogins());
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