package Service;

import DAO.*;
import RequestResult.LoginRequest;
import RequestResult.LoginResult;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {

    private Database db;
    private UserDao uDao;
    private PersonDao pDao;
    private EventDao eDao;
    private LoginRequest loginRequest;
    private final LoginService loginService = new LoginService();

    @BeforeEach
    void setUp() throws DataAccessException {
        RegisterService.isTest = true;
        db = new Database();
        Connection conn = db.getConnection();
        uDao = new UserDao(conn);
        pDao = new PersonDao(conn);
        eDao = new EventDao(conn);
        User user = new User("testUsername", "testPassword", "testEmail", "testFirstName",
                "testLastName", "m");
        Person person = new Person("testPersonID", "testUsername", "testFirstName",
                "testLastName", "m");
        uDao.insert(user);
        pDao.insert(person);
        db.closeConnection(true);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        RegisterService.isTest = false;
        Connection conn = db.getConnection();
        uDao = new UserDao(conn);
        pDao = new PersonDao(conn);
        eDao = new EventDao(conn);
        uDao.removeUser("testUsername");
        pDao.removePeople("testUsername");
        eDao.removeEvents("testUsername");
        db.closeConnection(true);
    }

    @Test
    void login() throws DataAccessException {
        loginRequest = new LoginRequest("testUsername", "testPassword");
        LoginResult loginResult = loginService.login(loginRequest);
        LoginResult expectedResult = new LoginResult(loginResult.getAuthToken(), "testUsername",
                loginResult.getPersonID(), true);
        assertEquals(expectedResult.getAuthToken(), loginResult.getAuthToken());
        assertEquals(expectedResult.getUserName(), loginResult.getUserName());
        assertEquals(expectedResult.getPersonID(), loginResult.getPersonID());
        assertEquals(expectedResult.isSuccess(), loginResult.isSuccess());
    }

    @Test
    void loginFail() throws DataAccessException {
        loginRequest = new LoginRequest("badTestUsername", "testPassword");
        assertThrows(DataAccessException.class, ()-> loginService.login(loginRequest));

        loginRequest.setUserName("testUsername");
        LoginResult loginResult = loginService.login(loginRequest);
        LoginResult expectedResult = new LoginResult(loginResult.getAuthToken(), "testUsername",
                loginResult.getPersonID(), true);
        assertEquals(expectedResult.getAuthToken(), loginResult.getAuthToken());
        assertEquals(expectedResult.getUserName(), loginResult.getUserName());
        assertEquals(expectedResult.getPersonID(), loginResult.getPersonID());
        assertEquals(expectedResult.isSuccess(), loginResult.isSuccess());

        loginRequest.setPassword("badTestPassword");
        assertThrows(DataAccessException.class, ()-> loginService.login(loginRequest));

        loginRequest.setPassword("testPassword");
        assertTrue(loginService.login(loginRequest).isSuccess());
    }
}