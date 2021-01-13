package DAO;

import model.AuthToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

class AuthTokenDaoTest {
    private Database db;
    private AuthToken authToken;
    private AuthTokenDao aDao;

    @BeforeEach
    void setUp() throws DataAccessException {
        db = new Database();
        authToken = new AuthToken("username", "authString");
        Connection conn = db.getConnection();
        db.clearTables();
        aDao = new AuthTokenDao(conn);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        db.closeConnection(false);
        db = null;
    }

    @Test
    void testInsertPositive() throws DataAccessException {
        aDao.insert(authToken);
        authToken.setAuthString("Hey_there");
        aDao.insert(authToken);
        authToken.setAuthString("Hi");
        aDao.insert(authToken);
    }

    @Test
    void testInsertNegative() throws DataAccessException {
        aDao.insert(authToken);
        String authTokenString = aDao.find("authTokenID");
        AuthToken authTokenCompare = new AuthToken("username", authTokenString);
        assertThrows(DataAccessException.class, ()-> aDao.insert(authTokenCompare));
    }

    @Test
    void testFindPositive() throws DataAccessException {
        aDao.insert(authToken);
        assertEquals("username", aDao.find("authString"));
        authToken.setAuthString("authString2");
        authToken.setUsername("username2");
        aDao.insert(authToken);
        assertEquals("username", aDao.find("authString"));
        assertEquals("username2", aDao.find("authString2"));
    }

    @Test
    void testFindNegative() throws DataAccessException {
        aDao.insert(authToken);
        assertEquals("username", aDao.find("authString"));
        authToken.setAuthString("authString2");
        authToken.setUsername("username2");
        aDao.insert(authToken);
        assertNotEquals("username2", aDao.find("authString"));
        assertNotEquals("username", aDao.find("authString2"));
        assertNull(aDao.find("badAuthString"));
    }

    @Test
    void testClear() throws DataAccessException {
        assertTrue(aDao.clear());
        aDao.insert(authToken);
        assertNotNull(aDao.find("authString"));
        assertTrue(aDao.clear());
        assertNull(aDao.find("authString"));
    }
}