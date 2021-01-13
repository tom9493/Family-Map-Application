package edu.byu.cs240.FamilyMapClient.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import RequestResult.LoginRequest;
import RequestResult.LoginResult;
import edu.byu.cs240.FamilyMapClient.net.ServerProxy;
import model.Event;
import model.Person;
import static org.junit.jupiter.api.Assertions.*;

class DataCacheTest {

    ServerProxy sp = new ServerProxy();

    @BeforeEach
    void setUp() {
        sp.setServerHostName("localhost");
        sp.setServerPortNumber(8080);
        DataCache.clear();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName("sheila");
        loginRequest.setPassword("parker");
        LoginResult loginResult = sp.login(loginRequest);
        sp.getAllEvents(loginResult.getAuthToken());
        sp.getAllPeople(loginResult.getAuthToken());
    }

    // Calculates family relationships (i.e., spouses, parents, children)
    @Test
    void calcFamilyRelationshipsTest() {
        DataCache.makeFamilyTreeLists();
        assertEquals(3, DataCache.getPaternalAncestors().size());
        assertEquals(3, DataCache.getMaternalAncestors().size());
        assertEquals(5, DataCache.getPaternalEvents().size());
        assertEquals(5, DataCache.getMaternalEvents().size());

        Person p = DataCache.getUser();
        assertEquals("Sheila_Parker", p.getPersonID());
        ArrayList<Person> children = DataCache.getChildren(p);
        assertNull(children);
        p = DataCache.getPersonById(p.getFatherID());
        assertEquals("Blaine_McGary", p.getPersonID());
        children = DataCache.getChildren(p);
        assertEquals(1, children.size());
        p = DataCache.getPersonById(p.getFatherID());
        assertEquals("Ken_Rodham", p.getPersonID());
        children = DataCache.getChildren(p);
        assertEquals(1, children.size());
        assertNull(p.getFatherID());
        assertNull(p.getMotherID());
        p = DataCache.getUser();
        assertEquals("Sheila_Parker", p.getPersonID());
        children = DataCache.getChildren(p);
        assertNull(children);
        p = DataCache.getPersonById(p.getMotherID());
        assertEquals("Betty_White", p.getPersonID());
        children = DataCache.getChildren(p);
        assertEquals(1, children.size());
        p = DataCache.getPersonById(p.getMotherID());
        assertEquals("Mrs_Jones", p.getPersonID());
        children = DataCache.getChildren(p);
        assertEquals(1, children.size());
        assertNull(p.getMotherID());
        assertNull(p.getMotherID());

        DataCache.clear();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName("patrick");
        loginRequest.setPassword("spencer");
        LoginResult loginResult = sp.login(loginRequest);
        sp.getAllEvents(loginResult.getAuthToken());
        sp.getAllPeople(loginResult.getAuthToken());

        DataCache.makeFamilyTreeLists();
        assertEquals(1, DataCache.getPaternalAncestors().size());
        assertEquals(1, DataCache.getMaternalAncestors().size());
        assertEquals(1, DataCache.getPaternalEvents().size());
        assertEquals(1, DataCache.getMaternalEvents().size());

        p = DataCache.getUser();
        assertEquals("Patrick_Spencer", p.getPersonID());
        children = DataCache.getChildren(p);
        assertNull(children);
        p = DataCache.getPersonById(p.getFatherID());
        assertEquals("Happy_Birthday", p.getPersonID());
        children = DataCache.getChildren(p);
        assertEquals(1, children.size());
        p = DataCache.getPersonById(p.getFatherID());
        assertNull(p);
    }

    @Test
    void calcFamilyRelationshipsTestFail() {
        // Try previous test case without using family tree function
        assertEquals(0, DataCache.getPaternalAncestors().size());
        assertEquals(0, DataCache.getMaternalAncestors().size());
        assertEquals(0, DataCache.getPaternalEvents().size());
        assertEquals(0, DataCache.getMaternalEvents().size());

        Person p = DataCache.getUser();
        ArrayList<Person> children = DataCache.getChildren(p);
        assertNull(children);
        p = DataCache.getPersonById(p.getFatherID());
        children = DataCache.getChildren(p);
        assertEquals(1, children.size());
        p = DataCache.getPersonById(p.getFatherID());
        children = DataCache.getChildren(p);
        assertEquals(1, children.size());
        assertNull(p.getFatherID());
        assertNull(p.getMotherID());

        // Now with the function
        DataCache.makeFamilyTreeLists();
        assertEquals(3, DataCache.getPaternalAncestors().size());
        assertEquals(3, DataCache.getMaternalAncestors().size());
        assertEquals(5, DataCache.getPaternalEvents().size());
        assertEquals(5, DataCache.getMaternalEvents().size());

        p = DataCache.getUser();
        children = DataCache.getChildren(p);
        assertNull(children);
        p = DataCache.getPersonById(p.getFatherID());
        children = DataCache.getChildren(p);
        assertEquals(1, children.size());
        p = DataCache.getPersonById(p.getFatherID());
        assertNull(p.getFatherID());
        assertNull(p.getMotherID());
    }

    // Filters events according to the current filter settings
    @Test
    void filterEventsTest() {
        DataCache.makeFilteredEvents();
        DataCache.makePersonEvents();
        ArrayList<Event> filteredEvents = DataCache.getFilteredEvents();
        Person p = DataCache.getPersonById(filteredEvents.get(0).getPersonID());
        assertEquals("f", p.getGender());
        
        Settings s = new Settings();
        s.setFemale(false);
        DataCache.setSettings(s);
        DataCache.makeFilteredEvents();
        
        filteredEvents = DataCache.getFilteredEvents();
        assertEquals(6, filteredEvents.size());
        // After settings switched female filter on, all events only have males
        for (int i = 0; i < filteredEvents.size(); ++i) {
            assertEquals("m", DataCache.getPersonById(filteredEvents.get(i).getPersonID())
                    .getGender());
        }

        s = new Settings();
        s.setFemale(true);
        s.setMale(false);
        DataCache.setSettings(s);
        DataCache.makeFilteredEvents();

        filteredEvents = DataCache.getFilteredEvents();
        assertEquals(10, filteredEvents.size());
        // After settings switched male filter on, all events only have females
        for (int i = 0; i < filteredEvents.size(); ++i) {
            assertEquals("f", DataCache.getPersonById(filteredEvents.get(i).getPersonID())
                    .getGender());
        }

        s.setMale(true);
        DataCache.setSettings(s);
        DataCache.makeFilteredEvents();

        ArrayList<Event> paternalEvents = DataCache.getPaternalEvents();
        ArrayList<Event> maternalEvents = DataCache.getMaternalEvents();
        for (int i = 0; i < paternalEvents.size(); ++i) {
            assertTrue(filteredEvents.contains(paternalEvents.get(i)));
        }
        for (int i = 0; i < maternalEvents.size(); ++i) {
            assertTrue(filteredEvents.contains(maternalEvents.get(i)));
        }

        s.setFather(false);
        s.setMother(false);
        DataCache.setSettings(s);
        DataCache.makeFilteredEvents();

        for (int i = 0; i < paternalEvents.size(); ++i) {
            assertFalse(filteredEvents.contains(paternalEvents.get(i)));
        }
        for (int i = 0; i < maternalEvents.size(); ++i) {
            assertFalse(filteredEvents.contains(maternalEvents.get(i)));
        }
        DataCache.setSettings(new Settings());
    }

    @Test
    void filterEventsTestFail() {
        DataCache.makeFilteredEvents();
        DataCache.makePersonEvents();
        Event e = DataCache.getFilteredEvents().get(5);
        assertNotNull(e);
        assertEquals("f", DataCache.getPersonById(e.getPersonID()).getGender());
        String id = e.getEventID();

        Settings s = new Settings();
        s.setFemale(false);
        DataCache.setSettings(s);
        DataCache.makeFilteredEvents();
        assertFalse(DataCache.getFilteredEvents().contains(DataCache.getEventById(id)));

        s.setFemale(true);
        DataCache.setSettings(s);
    }

    // Chronologically sorts a person’s individual events (birth first, death last, etc.)
    @Test
    void sortPersonEventsChronologicallyTest() {
        DataCache.makeFilteredEvents();
        DataCache.makePersonEvents();
        Person p = DataCache.getUser();
        ArrayList<Event> events = DataCache.getPersonEvents(p);
        assertEquals(1970, events.get(0).getYear());
        assertEquals("birth", events.get(0).getEventType());
        assertEquals(2012, events.get(1).getYear());
        assertEquals(2014, events.get(2).getYear());
        assertEquals(2014, events.get(3).getYear());
        assertEquals(2015, events.get(4).getYear());

        p = DataCache.getPersonById(p.getFatherID());
        events = DataCache.getPersonEvents(p);
        assertEquals(1, events.size());
        assertEquals(1948, events.get(0).getYear());

        p = DataCache.getPersonById(p.getFatherID());
        events = DataCache.getPersonEvents(p);
        assertEquals(2, events.size());
        assertEquals(1879, events.get(0).getYear());
        assertEquals(1895, events.get(1).getYear());

        p = DataCache.getPersonById(p.getSpouseID());
        events = DataCache.getPersonEvents(p);
        assertEquals(2, events.size());
        assertEquals(1890, events.get(0).getYear());
        assertEquals("Did a backflip", events.get(0).getEventType());
        assertEquals(1890, events.get(1).getYear());
        assertEquals("Did a backflip", events.get(0).getEventType());
        assertNotEquals(events.get(0), events.get(1));
    }

    @Test
    void sortPersonEventsChronologicallyTestFail() {
        DataCache.makeFilteredEvents();
        DataCache.makePersonEvents();
        Person p = DataCache.getUser();
        ArrayList<Event> events = DataCache.getPersonEvents(p);
        assertEquals(1970, events.get(0).getYear());
        assertEquals("birth", events.get(0).getEventType());
        assertEquals(2012, events.get(1).getYear());
        assertEquals(2014, events.get(2).getYear());
        assertEquals(2014, events.get(3).getYear());
        assertEquals(2015, events.get(4).getYear());

        p = DataCache.getPersonById(p.getMotherID());
        events = DataCache.getPersonEvents(p);
        assertEquals(1, events.size());
        assertEquals(2017, events.get(0).getYear());

        p = DataCache.getPersonById(p.getFatherID());
        events = DataCache.getPersonEvents(p);
        assertEquals(2, events.size());
        assertEquals(1993, events.get(0).getYear());
        assertEquals(1997, events.get(1).getYear());

        p = DataCache.getPersonById(p.getSpouseID());
        events = DataCache.getPersonEvents(p);
        assertEquals(2, events.size());
        assertEquals(2000, events.get(0).getYear());
        assertEquals("Learned to Surf", events.get(0).getEventType());
        assertEquals(2012, events.get(1).getYear());
        assertEquals("Learned to Surf", events.get(0).getEventType());
        assertNotEquals(events.get(0), events.get(1));
    }

    // Correctly searches for people and events
    @Test
    void searchForPeopleAndEventsTest() {
        Person p = new Person();
        p.setPersonID("Tom_Hart");
        p.setFirstName("Tom");
        p.setLastName("Hart");
        p.setGender("m");
        p.setUsername("Tom9493");

        DataCache.addPerson(p);
        Person searchedPerson = DataCache.getPersonById("Tom_Hart");
        assertNotNull(searchedPerson);
        assertEquals(p, searchedPerson);
        DataCache.removePerson(p);

        Event e = new Event();
        e.setEventID("New_Event");
        e.setAssociatedUsername("Tom9493");
        e.setCity("Utah");
        e.setCountry("USA");
        e.setLatitude(55.0340f);
        e.setLongitude(-12.3214f);
        e.setEventType("Blew a bubble");
        e.setYear(2020);
        e.setPersonID("Tom_Hart");
        DataCache.addEvent(e);
        Event searchedEvent = DataCache.getEventById("New_Event");
        assertNotNull(searchedEvent);
        assertEquals(e, searchedEvent);
        DataCache.removeEvent(e);
    }

    @Test
    void searchForPeopleAndEventsTestFail() {
        // Person and Event added in other test don't appear if not added + others...
        Person searchedPerson = DataCache.getPersonById("Tom_Hart");
        assertNull(searchedPerson);
        searchedPerson = DataCache.getPersonById("noPerson1");
        assertNull(searchedPerson);
        searchedPerson = DataCache.getPersonById("noPerson2");
        assertNull(searchedPerson);
        searchedPerson = DataCache.getPersonById("noPerson3");
        assertNull(searchedPerson);
        searchedPerson = DataCache.getPersonById("noPerson4");
        assertNull(searchedPerson);

        Event searchedEvent = DataCache.getEventById("New_Event");
        assertNull(searchedEvent);
        searchedEvent = DataCache.getEventById("noEvent1");
        assertNull(searchedEvent);
        searchedEvent = DataCache.getEventById("noEvent2");
        assertNull(searchedEvent);
        searchedEvent = DataCache.getEventById("noEvent3");
        assertNull(searchedEvent);
        searchedEvent = DataCache.getEventById("noEvent4");
        assertNull(searchedEvent);
    }
}