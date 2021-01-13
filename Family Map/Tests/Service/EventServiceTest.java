package Service;

import DAO.*;
import RequestResult.EventIDResult;
import RequestResult.RegisterRequest;
import RequestResult.RegisterResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {
    Database db = new Database();
    EventService es = new EventService();
    RegisterService rs;
    RegisterRequest registerRequest;
    String authToken;
    UserDao uDao;
    EventDao eDao;
    PersonDao pDao;

    @BeforeEach
    void setUp() throws FileNotFoundException, DataAccessException {
        Connection conn = db.getConnection();
        uDao = new UserDao(conn);
        pDao = new PersonDao(conn);
        eDao = new EventDao(conn);
        rs = new RegisterService();
        registerRequest = new RegisterRequest("testUsername", "testPassword", "testEmail",
                "testFirstName", "testLastName", "m");
        RegisterResult registerResult = rs.register(registerRequest);
        authToken = registerResult.getAuthToken();
        db.closeConnection(true);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
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
    void eventTest() throws DataAccessException {
        ArrayList<EventIDResult> events = es.event(authToken).getEvents();
        assertNotNull(events);
        assertNotEquals(0, events.size());
        assertEquals(91, events.size());
        EventIDResult event = events.get(0);
        assertNotNull(event);
        assertEquals("testUsername", event.getAssociatedUsername());
        assertNotNull(event.getEventType());
        assertNotNull(event.getEventID());
        assertNotNull(event.getPersonID());
        assertNotNull(event.getCountry());
        assertNotNull(event.getCity());
        assertNotEquals(0, event.getLongitude());
        assertNotEquals(0, event.getLatitude());
        assertNotEquals(0, event.getYear());
        assertTrue(event.isSuccess());
    }

    @Test
    void eventTestFail() throws DataAccessException {
        ArrayList<EventIDResult> events = es.event(authToken).getEvents();
        assertNotNull(events);
        assertNotEquals(0, events.size());
        assertEquals(91, events.size());
        assertThrows(DataAccessException.class, ()-> es.event("badAuthToken"));
        assertTrue(es.event(authToken).isSuccess());
    }

}