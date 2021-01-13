package edu.byu.cs240.FamilyMapClient.net;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import RequestResult.LoginRequest;
import RequestResult.LoginResult;
import RequestResult.RegisterRequest;
import RequestResult.RegisterResult;
import edu.byu.cs240.FamilyMapClient.model.DataCache;
import static org.junit.jupiter.api.Assertions.*;

class ServerProxyTest {

    ServerProxy sp = new ServerProxy();

    @BeforeEach
    void setUp() {
        sp.setServerHostName("localhost");
        sp.setServerPortNumber(8080);
    }

    @Test
    void login() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName("sheila");
        loginRequest.setPassword("parker");
        LoginResult loginResult = sp.login(loginRequest);
        assertNotNull(loginResult.getAuthToken());
        assertEquals("sheila", loginResult.getUserName());
        assertNotNull(loginResult.getPersonID());
        assertTrue(loginResult.isSuccess());
        assertNull(loginResult.getMessage());

        loginRequest.setUserName("patrick");
        loginRequest.setPassword("spencer");
        loginResult = sp.login(loginRequest);
        assertNotNull(loginResult.getAuthToken());
        assertEquals("patrick", loginResult.getUserName());
        assertNotNull(loginResult.getPersonID());
        assertTrue(loginResult.isSuccess());
        assertNull(loginResult.getMessage());
    }

    @Test
    void loginFail() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName("sheila");
        loginRequest.setPassword("badPassword");
        LoginResult loginResult = sp.login(loginRequest);
        assertNull(loginResult.getAuthToken());
        assertNull(loginResult.getPersonID());
        assertFalse(loginResult.isSuccess());

        loginRequest.setUserName("patrick");
        loginResult = sp.login(loginRequest);
        assertNull(loginResult.getAuthToken());
        assertNull(loginResult.getPersonID());
        assertFalse(loginResult.isSuccess());
    }

    @Test
    void register() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName("tomTest");
        registerRequest.setPassword("hart");
        registerRequest.setFirstName("Tom");
        registerRequest.setLastName("Hart");
        registerRequest.setGender("m");
        registerRequest.setEmail("Tom9493@gmail.com");
        RegisterResult registerResult = sp.register(registerRequest);
        assertNotNull(registerResult.getAuthToken());
        assertNotNull(registerResult.getPersonID());
        assertNull(registerResult.getMessage());
        assertTrue(registerResult.isSuccess());
        assertEquals("tomTest", registerResult.getUserName());

        registerRequest.setUserName("saraTest");
        registerRequest.setPassword("hart");
        registerRequest.setFirstName("Sara");
        registerRequest.setLastName("Hart");
        registerRequest.setGender("f");
        registerRequest.setEmail("Sara9493@gmail.com");
        registerResult = sp.register(registerRequest);
        assertNotNull(registerResult.getAuthToken());
        assertNotNull(registerResult.getPersonID());
        assertNull(registerResult.getMessage());
        assertTrue(registerResult.isSuccess());
        assertEquals("saraTest", registerResult.getUserName());
    }

    @Test
    void registerFail() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName("sheila");
        registerRequest.setPassword("parker");
        registerRequest.setFirstName("Sheila");
        registerRequest.setLastName("Parker");
        registerRequest.setGender("f");
        registerRequest.setEmail("Tom9493@gmail.com");
        RegisterResult registerResult = sp.register(registerRequest);
        assertNull(registerResult.getAuthToken());
        assertNull(registerResult.getPersonID());
        assertNull(registerResult.getUserName());
        assertFalse(registerResult.isSuccess());

        registerRequest.setUserName("patrick");
        registerRequest.setPassword("spencer");
        registerRequest.setFirstName("Patrick");
        registerRequest.setLastName("Spencer");
        registerRequest.setGender("m");
        registerRequest.setEmail("Tom9493@gmail.com");
        registerResult = sp.register(registerRequest);
        assertNull(registerResult.getAuthToken());
        assertNull(registerResult.getPersonID());
        assertNull(registerResult.getUserName());
        assertFalse(registerResult.isSuccess());
    }

    @Test
    void getAllPeople() {
        DataCache.clear();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName("sheila");
        loginRequest.setPassword("parker");
        LoginResult loginResult = sp.login(loginRequest);
        sp.getAllPeople(loginResult.getAuthToken());
        assertNotNull(DataCache.getAllPeople());
        assertEquals(8, DataCache.getAllPeople().size());
        assertEquals("sheila", DataCache.getUser().getUsername());

        DataCache.clear();

        loginRequest = new LoginRequest();
        loginRequest.setUserName("patrick");
        loginRequest.setPassword("spencer");
        loginResult = sp.login(loginRequest);
        sp.getAllPeople(loginResult.getAuthToken());
        assertNotNull(DataCache.getAllPeople());
        assertEquals(3, DataCache.getAllPeople().size());
        assertEquals("patrick", DataCache.getUser().getUsername());
    }

    @Test
    void getAllPeopleFail() {
        DataCache.clear();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName("sheila");
        loginRequest.setPassword("parker");
        LoginResult loginResult = sp.login(loginRequest);
        sp.getAllPeople("badAuthToken");
        assertNotNull(DataCache.getAllPeople());
        assertEquals(0, DataCache.getAllPeople().size());
        assertNull(DataCache.getUser());

        loginRequest = new LoginRequest();
        loginRequest.setUserName("patrick");
        loginRequest.setPassword("spencer");
        loginResult = sp.login(loginRequest);
        sp.getAllPeople("badAuthToken");
        assertNotNull(DataCache.getAllPeople());
        assertEquals(0, DataCache.getAllPeople().size());
        assertNull(DataCache.getUser());
    }

    @Test
    void getAllEvents() {
        DataCache.clear();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName("sheila");
        loginRequest.setPassword("parker");
        LoginResult loginResult = sp.login(loginRequest);
        sp.getAllEvents(loginResult.getAuthToken());
        assertNotNull(DataCache.getAllPeople());
        assertEquals(16, DataCache.getAllEvents().size());
        assertNotEquals(0, DataCache.getEventTypeColors().size());

        DataCache.clear();

        loginRequest = new LoginRequest();
        loginRequest.setUserName("patrick");
        loginRequest.setPassword("spencer");
        loginResult = sp.login(loginRequest);
        sp.getAllEvents(loginResult.getAuthToken());
        assertNotNull(DataCache.getAllEvents());
        assertEquals(3, DataCache.getAllEvents().size());
        assertNotEquals(0, DataCache.getEventTypeColors().size());
    }

    @Test
    void getAllEventsFail() {
        DataCache.clear();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName("sheila");
        loginRequest.setPassword("parker");
        LoginResult loginResult = sp.login(loginRequest);
        sp.getAllEvents("badAuthToken");
        assertNotNull(DataCache.getAllPeople());
        assertEquals(0, DataCache.getAllEvents().size());
        assertEquals(0, DataCache.getEventTypeColors().size());

        DataCache.clear();

        loginRequest = new LoginRequest();
        loginRequest.setUserName("patrick");
        loginRequest.setPassword("spencer");
        loginResult = sp.login(loginRequest);
        sp.getAllEvents("badAuthToken");
        assertNotNull(DataCache.getAllEvents());
        assertEquals(0, DataCache.getAllEvents().size());
        assertEquals(0, DataCache.getEventTypeColors().size());
    }
}