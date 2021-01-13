package Service;

import DAO.*;
import RequestResult.EventIDResult;
import RequestResult.LoadResult;
import RequestResult.PersonIDResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class LoadServiceTest {
    private final Generator g = new Generator();
    private final LoadService ls = new LoadService();
    private final Database db = new Database();
    private UserDao uDao;
    private PersonDao pDao;
    private EventDao eDao;

    @BeforeEach
    void setUp() throws DataAccessException {
        Connection conn = db.getConnection();
        uDao = new UserDao(conn);
        pDao = new PersonDao(conn);
        eDao = new EventDao(conn);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        uDao.removeUser("testUsername");
        pDao.removePeople("testUsername");
        eDao.removeEvents("testUsername");
        db.closeConnection(true);
    }

    @Test
    void load() throws FileNotFoundException, DataAccessException {
        LoadResult loadResult = ls.load(g.getExampleLoadRequest());
        assertTrue(loadResult.isSuccess());
        assertEquals("Successfully added 1 users, 3 persons, and 2 events to the database.",
                loadResult.getMessage());
        ArrayList<PersonIDResult> people = pDao.getAllPeople("sheila");
        ArrayList<EventIDResult> events = eDao.getAllEvents("sheila");
        assertEquals(3, people.size());
        assertEquals(2, events.size());
        PersonIDResult person = people.get(0);
        EventIDResult event = events.get(0);

        assertEquals("sheila", person.getUsername());
        assertEquals("Sheila", person.getFirstName());
        assertEquals("Parker", person.getLastName());
        assertEquals("f", person.getGender());
        assertEquals("Sheila_Parker", person.getPersonID());
        assertEquals("Patrick_Spencer", person.getFatherID());
        assertEquals("Im_really_good_at_names", person.getMotherID());

        assertEquals("started family map", event.getEventType());
        assertEquals("Sheila_Parker", event.getPersonID());
        assertEquals("Salt Lake City", event.getCity());
        assertEquals("United States", event.getCountry());
        assertEquals(40.7500f, event.getLatitude());
        assertEquals(-110.1167f, event.getLongitude());
        assertEquals(2016, event.getYear());
        assertEquals("sheila", event.getAssociatedUsername());
    }

   @Test
    void loadFail() throws FileNotFoundException, DataAccessException {
        assertTrue(ls.load(g.getExampleLoadRequest()).isSuccess());
        assertThrows(DataAccessException.class, ()-> ls.load(g.getBadExampleLoadRequest()));
        assertTrue(ls.load(g.getExampleLoadRequest()).isSuccess());
    }
}