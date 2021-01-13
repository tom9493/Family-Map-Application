package Service;

import DAO.*;
import RequestResult.EventIDResult;
import RequestResult.PersonIDResult;
import RequestResult.RegisterRequest;
import RequestResult.RegisterResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    Database db = new Database();
    PersonService ps = new PersonService();
    EventService es = new EventService();
    ClearService cs = new ClearService();
    RegisterService rs;
    RegisterRequest registerRequest;
    String authToken;
    UserDao uDao;
    EventDao eDao;
    PersonDao pDao;

    @BeforeEach
    void setUp() throws DataAccessException, FileNotFoundException {
        Connection conn = db.getConnection();
        uDao = new UserDao(conn);
        pDao = new PersonDao(conn);
        eDao = new EventDao(conn);
        rs = new RegisterService();
        registerRequest = new RegisterRequest("testUsername", "testPassword", "testEmail",
                "testFirstName", "testLastName", "m");
        RegisterResult registerResult = rs.register(registerRequest);
        authToken = registerResult.getAuthToken();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        uDao.removeUser("testUsername");
        pDao.removePeople("testUsername");
        eDao.removeEvents("testUsername");
        db.closeConnection(true);
    }

    @Test
    void clearTest() throws DataAccessException {
        ArrayList<PersonIDResult> people = ps.person(authToken).getPeople();
        ArrayList<EventIDResult> events = es.event(authToken).getEvents();
        assertNotEquals(0, people.size());
        assertNotEquals(0, events.size());
        assertNotNull(people.get(5));
        assertNotNull(events.get(10));
        cs.clear();
        assertThrows(DataAccessException.class, ()-> ps.person(authToken));
        assertThrows(DataAccessException.class, ()-> es.event(authToken));
    }
}