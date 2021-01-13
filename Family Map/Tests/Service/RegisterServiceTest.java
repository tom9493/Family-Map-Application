package Service;

import DAO.*;
import RequestResult.RegisterRequest;
import RequestResult.RegisterResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

class RegisterServiceTest {
    Database db = new Database();
    UserDao uDao;
    PersonDao pDao;
    EventDao eDao;
    RegisterService rs;
    RegisterRequest registerRequest;
    RegisterResult registerResult;

    @BeforeEach
    void setUp() throws DataAccessException {
        RegisterService.isTest = true;
        Connection conn = db.getConnection();
        uDao = new UserDao(conn);
        pDao = new PersonDao(conn);
        eDao = new EventDao(conn);
        rs = new RegisterService();
        registerRequest = new RegisterRequest("testUsername", "testPassword", "testEmail",
                "testFirstName", "testLastName", "m");
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        RegisterService.isTest = false;
        uDao.removeUser("testUsername");
        pDao.removePeople("testUsername");
        eDao.removeEvents("testUsername");
        db.closeConnection(true);
    }

    @Test
    void registerNewUser() throws FileNotFoundException, DataAccessException {
        registerResult = rs.register(registerRequest);
        assertNotNull(registerResult.getAuthToken());
        assertEquals(12, registerResult.getAuthToken().length());
        assertEquals("testUsername", registerResult.getUserName());
        assertNotNull(registerResult.getPersonID());
        assertEquals(10, registerResult.getPersonID().length());
        assertTrue(registerResult.isSuccess());
    }

    @Test
    void registerUserExists() throws FileNotFoundException, DataAccessException {
        RegisterService.isTest = false;
        registerResult = rs.register(registerRequest);
        assertNotNull(registerResult.getAuthToken());
        assertEquals(12, registerResult.getAuthToken().length());
        assertEquals("testUsername", registerResult.getUserName());
        assertNotNull(registerResult.getPersonID());
        assertEquals(10, registerResult.getPersonID().length());
        assertTrue(registerResult.isSuccess());
        assertThrows(DataAccessException.class, ()-> rs.register(registerRequest));
    }
}