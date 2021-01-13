package DAO;

import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

class PersonDaoTest {

    private Database db;
    private Person person;
    private PersonDao pDao;

    @BeforeEach
    void setUp() throws DataAccessException {
        db = new Database();
        Connection conn = db.getConnection();
        person = new Person("personID", "Username", "Tom", "Hart", "m");
        db.clearTables();
        pDao = new PersonDao(conn);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        db.closeConnection(false);
        db = null;
    }

    @Test
    void testInsertPositive() throws DataAccessException {
        pDao.insert(person);
        Person personCompare = pDao.find("personID", "Username");
        assertNotNull(personCompare);
        assertEquals(person, personCompare);
    }

    @Test
    void testInsertNegative() throws DataAccessException {
        // Bad gender
        person.setGender("male");
        assertThrows(DataAccessException.class, ()-> pDao.insert(person));
        person.setGender("m");
        pDao.insert(person);
        // Inserts one person twice into database
        assertThrows(DataAccessException.class, ()-> pDao.insert(person));
    }

    @Test
    void testFindPositive() throws DataAccessException { // Positive
        pDao.insert(person);
        assertNotNull(pDao.find("personID", "Username"));
        person.setPersonID("Howdy");
        pDao.insert(person);
        assertNotNull(pDao.find("Howdy", "Username"));
        assertNotEquals(pDao.find("personID", "Username"), pDao.find("Howdy", "Username"));

    }

    @Test
    void testFindNegative() throws DataAccessException {
        pDao.insert(person);
        assertNotNull(pDao.find("personID", "Username"));
        assertNull(pDao.find("personBadID", "Username"));
        assertNull(pDao.find("personBadID2", "Username"));
        assertNotNull(pDao.find("personID", "Username"));
    }

    @Test
    void testClear() throws DataAccessException {
        assertTrue(pDao.clear());
        pDao.insert(person);
        assertNotNull(pDao.find("personID", "Username"));
        assertTrue(pDao.clear());
        assertNull(pDao.find(person.getPersonID(), "Username"));
    }

    @Test
    void testGetAllPeople() throws DataAccessException {
        pDao.insert(person);
        assertEquals(1, pDao.getAllPeople(person.getUsername()).size());
        person = new Person("personID2", "Username", "Tom", "Hart", "m");
        pDao.insert(person);
        assertNotEquals(1, pDao.getAllPeople(person.getUsername()).size());
        assertEquals(2, pDao.getAllPeople(person.getUsername()).size());
        person = new Person("personID3", "Username", "Tom", "Hart", "m");
        pDao.insert(person);
        person = new Person("personID4", "Username", "Tom", "Hart", "m");
        pDao.insert(person);
        person = new Person("personID5", "Username", "Tom", "Hart", "m");
        pDao.insert(person);
        person = new Person("personID6", "Username", "Tom", "Hart", "m");
        pDao.insert(person);
        person = new Person("personID7", "Username", "Tom", "Hart", "m");
        pDao.insert(person);
        assertEquals(7, pDao.getAllPeople(person.getUsername()).size());
        pDao.clear();
        assertEquals(0, pDao.getAllPeople(person.getUsername()).size());
    }

    @Test
    void testRemovePeople() throws DataAccessException {
        pDao.insert(person);
        person = new Person("personID2", "Username", "Tom", "Hart", "m");
        pDao.insert(person);
        person = new Person("personID3", "Username2", "Tom", "Hart", "m");
        pDao.insert(person);
        person = new Person("personID4", "Username2", "Tom", "Hart", "m");
        pDao.insert(person);
        pDao.removePeople("Username");
        assertEquals(0, pDao.getAllPeople("Username").size());
        assertEquals(2, pDao.getAllPeople("Username2").size());
        pDao.removePeople("Username2");
        assertEquals(0, pDao.getAllPeople("Username2").size());
    }

    @Test
    void testGetPersonID() throws DataAccessException {
        pDao.insert(person);
        person = new Person("personID2", "Username", "Larry", "King", "m");
        pDao.insert(person);
        person = new Person("personID3", "Username", "Madi", "Douglas", "f");
        pDao.insert(person);
        person = new Person("personID4", "Username", "Keegan", "Rembacz", "m");
        pDao.insert(person);
        assertEquals("personID4", pDao.getPersonID("Username", "Keegan", "Rembacz",
                "m"));
        assertEquals("personID2", pDao.getPersonID("Username", "Larry", "King",
                "m"));
        assertEquals("personID3", pDao.getPersonID("Username", "Madi", "Douglas",
                "f"));
        assertNotEquals("personID3", pDao.getPersonID("Username", "Madi", "Douglas",
                "m"));
        assertNull(pDao.getPersonID("Username", "LarryBad", "King",
                "m"));
    }

}