package Service;

import DAO.*;
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

class PersonServiceTest {
    Database db = new Database();
    PersonService ps = new PersonService();
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
    void personTest() throws DataAccessException {
        ArrayList<PersonIDResult> people = ps.person(authToken).getPeople();
        assertNotNull(people);
        assertNotEquals(0, people.size());
        assertEquals(31, people.size());
        PersonIDResult person = people.get(0);
        assertNotNull(person);
        assertEquals("testUsername", person.getUsername());
        assertEquals("testFirstName", person.getFirstName());
        assertEquals("testLastName", person.getLastName());
        assertEquals("m", person.getGender());
        person = people.get(30);
        assertNotNull(person);
        assertEquals("testUsername", person.getUsername());
        assertNotEquals("testFirstName", person.getFirstName());
        assertNotEquals("testLastName", person.getLastName());
    }

    @Test
    void personTestFail() throws DataAccessException {
        ArrayList<PersonIDResult> people = ps.person(authToken).getPeople();
        assertNotNull(people);
        assertNotEquals(0, people.size());
        assertEquals(31, people.size());
        PersonIDResult person = people.get(0);
        assertNotNull(person);
        assertEquals("testUsername", person.getUsername());
        assertEquals("testFirstName", person.getFirstName());
        assertEquals("testLastName", person.getLastName());
        assertEquals("m", person.getGender());
        assertThrows(DataAccessException.class, ()-> ps.person("badAuthToken"));
        assertThrows(DataAccessException.class, ()-> ps.person("badAuthToken2"));
    }

}