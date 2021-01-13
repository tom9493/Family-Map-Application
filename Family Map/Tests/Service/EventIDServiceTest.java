package Service;

import DAO.*;
import RequestResult.EventIDRequest;
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

class EventIDServiceTest {
    Database db = new Database();
    EventService es = new EventService();
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
    void eventIDTest() throws DataAccessException {
        ArrayList<EventIDResult> events = es.event(authToken).getEvents();
        EventIDResult event = events.get(5);
        assertTrue(event.isSuccess());
        String eventID = event.getEventID();
        EventIDRequest request = new EventIDRequest(eventID);
        EventIDService eIDs = new EventIDService();
        EventIDResult result = eIDs.eventID(request, authToken);
        assertTrue(result.isSuccess());
        assertEquals(event.getAssociatedUsername(), result.getAssociatedUsername());
        assertEquals(event.getPersonID(), result.getPersonID());
        assertEquals(event.getEventID(), result.getEventID());
        assertEquals(event.getEventType(), result.getEventType());
        assertEquals(event.getCity(), result.getCity());
        assertEquals(event.getCountry(), result.getCountry());
        assertEquals(event.getLatitude(), result.getLatitude());
        assertEquals(event.getLongitude(), result.getLongitude());
        assertEquals(event.getYear(), result.getYear());
    }

    @Test
    void eventIDTestFail() throws DataAccessException {
        ArrayList<EventIDResult> events = es.event(authToken).getEvents();
        EventIDResult event = events.get(10);
        String eventID = event.getEventID();
        EventIDRequest request = new EventIDRequest(eventID);
        EventIDService eIDs = new EventIDService();
        EventIDResult result = eIDs.eventID(request, authToken);
        assertTrue(result.isSuccess());
        assertThrows(DataAccessException.class, ()-> eIDs.eventID(request, "badAuthToken"));
    }
}