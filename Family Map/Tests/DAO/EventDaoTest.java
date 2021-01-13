package DAO;

import model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

public class EventDaoTest {
    private Database db;
    private Event event;
    private EventDao eDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();
        event = new Event("eventID", "Username", "Gale123A", 35.9f, 140.1f,
                "Japan", "Ushiku", "Biking_Around", 2016);
        Connection conn = db.getConnection();
        db.clearTables();
        eDao = new EventDao(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
        db = null;
    }

    @Test
    public void testInsertPositive() throws DataAccessException {
        eDao.insert(event);
        Event compareTest = eDao.find(event.getEventID(), "Username");
        assertNotNull(compareTest);
        assertEquals(event, compareTest);
        assertEquals("eventID", compareTest.getEventID(), "Different eventID");
        assertEquals("Username", compareTest.getAssociatedUsername(), "Different username");
        assertEquals("Gale123A", compareTest.getPersonID(), "Different personID");
        assertEquals(35.9f, compareTest.getLatitude(), "Different latitude");
        assertEquals(140.1f, compareTest.getLongitude(), "Different longitude");
        assertEquals("Japan", compareTest.getCountry(), "Different country");
        assertEquals("Ushiku", compareTest.getCity(), "Different city");
        assertEquals("Biking_Around", compareTest.getEventType(), "Different event type");
        assertEquals(2016, compareTest.getYear(), "Different year");
    }

    @Test
    public void testInsertNegative() throws DataAccessException {
        eDao.insert(event);
        Event compareTest = eDao.find(event.getEventID(), "Username");
        assertNotNull(compareTest);
        assertEquals(event, compareTest);
        assertEquals("eventID", compareTest.getEventID(), "Different eventID");
        assertEquals("Username", compareTest.getAssociatedUsername(), "Different username");
        assertEquals("Gale123A", compareTest.getPersonID(), "Different personID");
        assertEquals(35.9f, compareTest.getLatitude(), "Different latitude");
        assertEquals(140.1f, compareTest.getLongitude(), "Different longitude");
        assertEquals("Japan", compareTest.getCountry(), "Different country");
        assertEquals("Ushiku", compareTest.getCity(), "Different city");
        assertEquals("Biking_Around", compareTest.getEventType(), "Different event type");
        assertEquals(2016, compareTest.getYear(), "Different year");
        assertThrows(DataAccessException.class, ()-> eDao.insert(event), "No DataAccessException thrown");
    }

    @Test
    public void testFindPositive() throws DataAccessException {
        eDao.insert(event);
        event = new Event("eventID2", "Username", "Gale123A", 35.9f, 140.1f,
                "Japan", "Ushiku", "Biking_Around", 2016);
        eDao.insert(event);
        Event event1 = eDao.find("eventID", "Username");
        Event event2 = eDao.find("eventID2", "Username");
        assertNotNull(event1);
        assertNotNull(event2);
        assertNotEquals(event1, event2);
    }

    @Test 
    void testFindNegative() throws DataAccessException {
        eDao.insert(event);
        assertNotNull(eDao.find("eventID", "Username"));
        assertNull(eDao.find("badEventID", "Username"));
        assertNull(eDao.find("badEventID2", "Username"));
        assertNull(eDao.find("eventID", "badUsername"));
        assertNull(eDao.find("eventID", "badUsername2"));
        assertNotNull(eDao.find("eventID", "Username"));
    }

    @Test
    void testClear() throws DataAccessException {
        assertTrue(eDao.clear());
        eDao.insert(event);
        assertNotNull(eDao.find("eventID", "Username"));
        assertTrue(eDao.clear());
        assertNull(eDao.find(event.getPersonID(), "Username"));
    }
    
    @Test
    void testGetAllEvents() throws DataAccessException {
        eDao.insert(event);
        assertEquals(1, eDao.getAllEvents(event.getAssociatedUsername()).size());
        event.setEventID("eventID2");
        eDao.insert(event);
        assertNotEquals(1, eDao.getAllEvents(event.getAssociatedUsername()).size());
        assertEquals(2, eDao.getAllEvents(event.getAssociatedUsername()).size());
        event.setEventID("eventID3");
        eDao.insert(event);
        event.setEventID("eventID4");
        eDao.insert(event);
        event.setEventID("eventID5");
        eDao.insert(event);
        event.setEventID("eventID6");
        eDao.insert(event);
        event.setEventID("eventID7");
        eDao.insert(event);
        assertEquals(7, eDao.getAllEvents(event.getAssociatedUsername()).size());
        eDao.clear();
        assertEquals(0, eDao.getAllEvents(event.getAssociatedUsername()).size());
    }

    @Test
    void testRemoveEvent() throws DataAccessException {
        eDao.insert(event);
        event.setEventID("eventID2");
        eDao.insert(event);
        event.setEventID("eventID3"); event.setAssociatedUsername("Username2");
        eDao.insert(event);
        event.setEventID("eventID4");
        eDao.insert(event);
        eDao.removeEvents("Username");
        assertEquals(0, eDao.getAllEvents("Username").size());
        assertEquals(2, eDao.getAllEvents("Username2").size());
        eDao.removeEvents("Username2");
        assertEquals(0, eDao.getAllEvents("Username2").size());
    }
 
}
