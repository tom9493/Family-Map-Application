package Service;

import DAO.*;
import RequestResult.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class PersonIDServiceTest {
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
    void personIDTest() throws DataAccessException {
        ArrayList<PersonIDResult> persons = ps.person(authToken).getPeople();
        PersonIDResult person = persons.get(5);
        assertTrue(person.isSuccess());
        String personID = person.getPersonID();
        PersonIDRequest request = new PersonIDRequest(personID);
        PersonIDService pIDs = new PersonIDService();
        PersonIDResult result = pIDs.personID(request, authToken);
        assertTrue(result.isSuccess());
        assertEquals(person.getUsername(), result.getUsername());
        assertEquals(person.getPersonID(), result.getPersonID());
        assertEquals(person.getFirstName(), result.getFirstName());
        assertEquals(person.getLastName(), result.getLastName());
        assertEquals(person.getGender(), result.getGender());
        assertEquals(person.getFatherID(), result.getFatherID());
        assertEquals(person.getMotherID(), result.getMotherID());
        assertEquals(person.getSpouseID(), result.getSpouseID());
    }

    @Test
    void personIDTestFail() throws DataAccessException {
        ArrayList<PersonIDResult> persons = ps.person(authToken).getPeople();
        PersonIDResult person = persons.get(10);
        String personID = person.getPersonID();
        PersonIDRequest request = new PersonIDRequest(personID);
        PersonIDService pIDs = new PersonIDService();
        PersonIDResult result = pIDs.personID(request, authToken);
        assertTrue(result.isSuccess());
        assertThrows(DataAccessException.class, ()-> pIDs.personID(request, "badAuthToken"));
    }
}