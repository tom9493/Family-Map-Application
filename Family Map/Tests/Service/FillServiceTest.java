package Service;

import DAO.*;
import RequestResult.EventIDResult;
import RequestResult.FillRequest;
import RequestResult.FillResult;
import RequestResult.PersonIDResult;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class FillServiceTest {
    Database db;
    Connection conn;
    PersonDao pDao;
    EventDao eDao;
    UserDao uDao;
    FillService fs;
    FillRequest fillReq;

    @BeforeEach
    void setUp() throws DataAccessException {
        db = new Database();
        conn = db.getConnection();
        uDao = new UserDao(conn);
        pDao = new PersonDao(conn);
        eDao = new EventDao(conn);

        fs = new FillService();

        User user = new User("testUsername", "testPassword", "testEmail", "testFirstName",
                "testLastName", "m");
        Person person = new Person("testPersonID", "testUsername", "testFirstName",
                "testLastName", "m");

        uDao.insert(user);
        pDao.insert(person);

        db.closeConnection(true);

        fillReq = new FillRequest(user.getUsername(), 4);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        conn = db.getConnection();
        uDao = new UserDao(conn);
        pDao = new PersonDao(conn);
        eDao = new EventDao(conn);
        uDao.removeUser("testUsername");
        pDao.removePeople("testUsername");
        eDao.removeEvents("testUsername");
        db.closeConnection(true);
    }

    @Test
    void fill() throws DataAccessException, FileNotFoundException {
        conn = db.getConnection();
        uDao = new UserDao(conn);
        pDao = new PersonDao(conn);
        eDao = new EventDao(conn);
        FillResult fillResult = fs.fill(fillReq);
        assertEquals("Successfully added 31 persons and 91 events to the database.", fillResult.getMessage());
        assertTrue(fillResult.isSuccess());
        ArrayList<PersonIDResult> people = pDao.getAllPeople("testUsername");
        ArrayList<EventIDResult> events = eDao.getAllEvents("testUsername");
        assertEquals(31, people.size());
        assertEquals(91, events.size());
        PersonIDResult person = people.get(15);
        EventIDResult event = events.get(45);

        assertNotNull(person);
        assertTrue(person.isSuccess());
        assertEquals("testUsername", person.getUsername());
        assertNotNull(person.getPersonID());
        assertNotNull(person.getFirstName());
        assertNotNull(person.getLastName());
        assertNotNull(person.getFirstName());
        assertNotNull(person.getGender());
        assertTrue(person.isSuccess());

        assertNotNull(event);
        assertTrue(event.isSuccess());
        assertEquals("testUsername", event.getAssociatedUsername());
        assertNotNull(event.getEventType());
        assertNotNull(event.getEventID());
        assertNotNull(event.getPersonID());
        assertNotNull(event.getCountry());
        assertNotNull(event.getCity());
        assertNotEquals(0, event.getLongitude());
        assertNotEquals(0, event.getLatitude());
        assertNotEquals(0, event.getYear());


    }

    @Test
    void fillFail() throws DataAccessException, FileNotFoundException {
        fillReq.setUserName("badTestUsername");
        assertThrows(DataAccessException.class, ()-> fs.fill(fillReq));
        fillReq.setUserName("testUsername");
        FillResult fillResult = fs.fill(fillReq);
        assertEquals("Successfully added 31 persons and 91 events to the database.", fillResult.getMessage());
        assertTrue(fillResult.isSuccess());
    }
}