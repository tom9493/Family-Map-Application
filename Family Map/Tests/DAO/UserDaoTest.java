package DAO;

import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest {

    private Database db;
    private User user;
    private UserDao uDao;

    @BeforeEach
    void setUp() throws DataAccessException {
        db = new Database();
        Connection conn = db.getConnection();
        user = new User("testUsername", "testPassword", "testEmail", "testFirstName",
                "testLastName", "m");
        db.clearTables();
        uDao = new UserDao(conn);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        db.closeConnection(false);
        db = null;
    }

    @Test
    void testInsertPositive() throws DataAccessException {
        uDao.insert(user);
        User userCompare = uDao.find(user.getUsername(), user.getPassword());
        assertNotNull(userCompare);
        assertEquals(user, userCompare);
    }

    @Test
    void testInsertNegative() throws DataAccessException {
        // Bad gender
        user.setGender("male");
        assertThrows(DataAccessException.class, ()-> uDao.insert(user));
        user.setGender("m");
        uDao.insert(user);
        // inserts one user twice into database
        assertThrows(DataAccessException.class, ()-> uDao.insert(user));
    }

    @Test
    void testFindPositive() throws DataAccessException {
        uDao.insert(user);
        assertNotNull(uDao.find("testUsername", "testPassword"));
        user.setUsername("Howdy"); user.setPassword("Greetings");
        uDao.insert(user);
        assertNotNull(uDao.find("Howdy", "Greetings"));
        assertNotEquals(uDao.find("userID", "Username"),
                uDao.find("Howdy", "Greetings"));

    }

    @Test
    void testFindNegative() throws DataAccessException {
        uDao.insert(user);
        assertNotNull(uDao.find("testUsername", "testPassword"));
        assertNull(uDao.find("badUsername", "password"));
        assertNull(uDao.find("username", "badPassword"));
        assertThrows(DataAccessException.class, ()-> uDao.find("username", null));
    }

    @Test
    void clear() throws DataAccessException {
        assertTrue(uDao.clear());
        uDao.insert(user);
        assertNotNull(uDao.find("testUsername", "testPassword"));
        assertTrue(uDao.clear());
        assertNull(uDao.find(user.getUsername(), user.getPassword()));
    }
}