package edu.byu.cs240.FamilyMapClient.model;

import java.util.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import RequestResult.EventIDResult;
import RequestResult.PersonIDResult;
import model.Event;
import model.Person;

public class DataCache {

    private static DataCache _instance;

    public static DataCache getInstance() { return _instance; }

    static { initialize(); }

    public static void initialize() { _instance = new DataCache(); }

    public static void clear() { _instance._clear(); }

    public static void setUser(Person p) { _instance._setUser(p); }

    public static Person getUser() { return _instance._getUser(); }

    public static void removePerson(Person p) { _instance._removePerson(p); }

    public static void removeEvent(Event e) { _instance._removeEvent(e); }

    public static void addPerson(Person p) { _instance._addPerson(p); }

    public static Collection<Person> getAllPeople() { return _instance._getAllPeople(); }

    public static void setAllPeople(ArrayList<PersonIDResult> data) { 
        _instance._setAllPeople(data); 
    }

    public static Person getPersonById(String id) { return _instance._getPersonById(id); }

    public static void addEvent(Event e) { _instance._addEvent(e); }

    public static Collection<Event> getAllEvents() { return _instance._getAllEvents(); }
    
    public static void setAllEvents(ArrayList<EventIDResult> data) {
        _instance._setAllEvents(data);
    }

    public static Event getEventById(String id) { return _instance._getEventById(id); }

    public static Map<String, Float> getEventTypeColors() {
        return _instance._getEventTypeColors();
    }

    public static void setSettings(Settings s) { _instance._setSettings(s); }

    public static Settings getSettings() { return _instance._getSettings(); }

    public static ArrayList<Event> getPersonEvents(Person p) { return _instance._getPersonEvents(p); }

    public static Map<String, ArrayList<Event>> getPersonEvents() {
        return _instance._getPersonEvents();
    }

    public static Person getPerson(Event event) { return _instance._getPerson(event); }

    public static ArrayList<Person> fillPeopleArray(Person p) {
        return _instance._fillPeopleArray(p);
    }

    public static String getRelationship(Person relative, Person rootPerson) {
        return _instance._getRelationship(relative, rootPerson);
    }

    public static void setEventActivityEvent(Event event) {
        _instance._setEventActivityEvent(event);
    }

    public static Event getEventActivityEvent() { return _instance._getEventActivityEvent(); }

    public static void makePersonEvents() { _instance._makePersonEvents(); }

    public static void makeFamilyTreeLists() { _instance._makeFamilyTreeLists(); }

    public static void makeFilteredEvents() { _instance._makeFilteredEvents(); }

    public static ArrayList<Event> getFilteredEvents() { return _instance._getFilteredEvents(); }

    public static ArrayList<String> getPaternalAncestors() {
        return _instance._getPaternalAncestors();
    }

    public static ArrayList<String> getMaternalAncestors() {
        return _instance._getMaternalAncestors();
    }

    public static ArrayList<Event> getPaternalEvents() {
        return _instance._getPaternalEvents();
    }

    public static ArrayList<Event> getMaternalEvents() {
        return _instance._getMaternalEvents();
    }

    public static ArrayList<Person> getChildren(Person p) { return _instance._getChildren(p); }

    private Map<String, Person> people;
    private Map<String, Event> events;
    private ArrayList<Event> filteredEvents;
    private Map<String, ArrayList<Event>> personEvents;
    private Settings settings;
    private List<String> eventTypes;
    private Map<String, Float> eventTypeColors;
    private Person user;
    private ArrayList<String> paternalAncestors;
    private ArrayList<String> maternalAncestors;
    private ArrayList<Event> paternalEvents;
    private ArrayList<Event> maternalEvents;
    private Map<String, List<Person>> personChildren;
    private Event eventActivityEvent;

    private DataCache() {
        people = new HashMap<>();
        events = new HashMap<>();
        filteredEvents = new ArrayList<>();
        personEvents = new HashMap<>();
        settings = new Settings();
        eventTypes = new ArrayList<>();
        eventTypeColors = new HashMap<>();
        user = null;
        paternalAncestors = new ArrayList<>();
        maternalAncestors = new ArrayList<>();
        paternalEvents = new ArrayList<>();
        maternalEvents = new ArrayList<>();
        personChildren = new HashMap<>();
    }

    private void _clear() {
        people.clear();
        events.clear();
        filteredEvents.clear();
        personEvents.clear();
        eventTypes.clear();
        eventTypeColors.clear();
        user = null;
        paternalAncestors.clear();
        maternalAncestors.clear();
        paternalEvents.clear();
        maternalEvents.clear();
        personChildren.clear();
    }

    private void _setEventActivityEvent(Event event) { eventActivityEvent = event; }

    private Event _getEventActivityEvent() { return eventActivityEvent;}

    private void _setUser(Person p) { this.user = p; }

    private Person _getUser() { return user; }

    private void _addPerson(Person p) { people.put(p.getPersonID(), p); }

    private void _addEvent(Event e){ events.put(e.getEventID(), e); }

    private void _removePerson(Person p) { people.remove(p.getPersonID()); }

    private void _removeEvent(Event e) { people.remove(e.getEventID()); }

    private Collection<Person> _getAllPeople() { return people.values(); }

    private Collection<Event> _getAllEvents() { return events.values(); }

    private Person _getPersonById(String id){ return people.get(id); }

    private Event _getEventById(String id) { return events.get(id); }

    private Map<String, Float> _getEventTypeColors(){
        return eventTypeColors;
    }

    private void _setSettings(Settings s) { settings = s; }

    private Settings _getSettings() {
        return settings;
    }

    private Map<String, ArrayList<Event>> _getPersonEvents() { return personEvents; }

    private Person _getPerson(Event event) { return _getPersonById(event.getPersonID()); }

    private ArrayList<Event> _getFilteredEvents() { return filteredEvents; }

    private ArrayList<String> _getPaternalAncestors() { return paternalAncestors; }

    private ArrayList<String> _getMaternalAncestors() { return maternalAncestors; }

    private ArrayList<Event> _getPaternalEvents() { return paternalEvents; }

    private ArrayList<Event> _getMaternalEvents() { return maternalEvents; }

    private void _makeFamilyTreeLists() {
        _calcPaternalAncestors();
        _calcMaternalAncestors();
    }

    private void _calcPaternalAncestors() {
        paternalAncestors.clear();
        paternalEvents.clear();
        ArrayList<Event> evts = new ArrayList<>(events.values());
        if (user.getFatherID() != null) {
            _calcPaternalAncestors(getPersonById(user.getFatherID()), evts);
        }
    }

    private void _calcPaternalAncestors(Person p, ArrayList<Event> evts) {
        if (p != null) {
            paternalAncestors.add(p.getPersonID());
            for (int i = 0; i < evts.size(); ++i) {
                if (evts.get(i).getPersonID().equals(p.getPersonID())) {
                    paternalEvents.add(evts.get(i));
                }
            }
            _calcPaternalAncestors(getPersonById(p.getFatherID()), evts);
            _calcPaternalAncestors(getPersonById(p.getMotherID()), evts);
        }
    }

    private void _calcMaternalAncestors() {
        maternalAncestors.clear();
        maternalEvents.clear();
        ArrayList<Event> evts = new ArrayList<>(events.values());
        _calcMaternalAncestors(getPersonById(user.getMotherID()), evts);
    }

    private void _calcMaternalAncestors(Person p, ArrayList<Event> evts) {
        if (p != null) {
            maternalAncestors.add(p.getPersonID());
            for (int i = 0; i < evts.size(); ++i) {
                if (evts.get(i).getPersonID().equals(p.getPersonID())) {
                    maternalEvents.add(evts.get(i));
                }
            }
            _calcMaternalAncestors(getPersonById(p.getMotherID()), evts);
            _calcMaternalAncestors(getPersonById(p.getFatherID()), evts);
        }
    }

    public ArrayList<Person> _getChildren(Person p) {
        ArrayList<Person> people = new ArrayList<>(getAllPeople());
        ArrayList<Person> childrenList = new ArrayList<>();
        String id = p.getPersonID();
        for (int i = 0; i < people.size(); ++i) {
            if (people.get(i).getFatherID() != null) {
                if (people.get(i).getFatherID().equals(id)) { childrenList.add(people.get(i)); }
            }
            if (people.get(i).getMotherID() != null) {
                if (people.get(i).getMotherID().equals(id)) { childrenList.add(people.get(i)); }
            }
        }
        if (childrenList.size() == 0) { return null; }
        else { return childrenList; }
    }

    private void _setAllPeople(ArrayList<PersonIDResult> data) {
        people.clear();
        
        for (int i = 0; i < data.size(); ++i) {
            Person person = new Person();
            PersonIDResult result = data.get(i);

            person.setPersonID(result.getPersonID());
            person.setUsername(result.getUsername());
            person.setFirstName(result.getFirstName());
            person.setLastName(result.getLastName());
            person.setGender(result.getGender());
            person.setFatherID(result.getFatherID());
            person.setMotherID(result.getMotherID());
            person.setSpouseID(result.getSpouseID());
            
            people.put(person.getPersonID(), person);
        }

        Person p = getPersonById(data.get(0).getPersonID());

        while (user == null) {
            if (getChildren(p) == null) { _setUser(p); }
            else { p = getChildren(p).get(0); }
        }
    }
    
    private void _setAllEvents(ArrayList<EventIDResult> data) {
        events.clear();

        for (int i = 0; i < data.size(); ++i) {
            EventIDResult result = data.get(i);
            Event event = new Event();

            event.setEventID(result.getEventID());
            event.setAssociatedUsername(result.getAssociatedUsername());
            event.setPersonID(result.getPersonID());
            event.setLatitude(result.getLatitude());
            event.setLongitude(result.getLongitude());
            event.setCountry(result.getCountry());
            event.setCity(result.getCity());
            event.setEventType(result.getEventType());
            event.setYear(result.getYear());

            events.put(event.getEventID(), event);
        }
        _createEventColorMap(data);
    }

    private ArrayList<Event> _getPersonEvents(Person p) {
        ArrayList<Event> listToSend = new ArrayList<>();
        ArrayList<Event> personEventList = personEvents.get(p.getPersonID());
        for (int i = 0; i < personEventList.size(); ++i) {
            Event event = getEventById(personEventList.get(i).getEventID());
            for (int j = 0; j < filteredEvents.size(); ++j) {
                if (event == filteredEvents.get(j)) {
                    listToSend.add(event);
                }
            }
        }
        return listToSend;
    }

    private String _getRelationship(Person relative, Person rootPerson) {
        String id = relative.getPersonID();
        ArrayList<Person> people = new ArrayList<>(getAllPeople());
        if (rootPerson.getFatherID() != null) {
            if (rootPerson.getFatherID().equals(id)) { return "Father"; }
        }
        if (rootPerson.getMotherID() != null) {
            if (rootPerson.getMotherID().equals(id)) { return "Mother"; }
        }
        if (rootPerson.getSpouseID() != null) {
            if (rootPerson.getSpouseID().equals(id)) { return "Spouse"; }
        }
        for (int i = 0; i < people.size(); ++i) {
            if (people.get(i).getFatherID() != null) {
                if (people.get(i).getFatherID().equals(rootPerson.getPersonID())) { return "Child"; }
            }
            if (people.get(i).getMotherID() != null) {
                if (people.get(i).getMotherID().equals(rootPerson.getPersonID())) { return "Child"; }
            }
        }
        return "Not in nuclear family (Error)";
    }


    private ArrayList<Person> _fillPeopleArray(Person p) {
        ArrayList<Person> peopleList = new ArrayList<>();
        ArrayList<Person> childrenList = _getChildren(p);
        Person father = getPersonById(p.getFatherID());
        Person mother = getPersonById(p.getMotherID());
        Person spouse = getPersonById(p.getSpouseID());

        if (father != null) { peopleList.add(father); }
        if (mother != null) { peopleList.add(mother); }
        if (spouse != null) { peopleList.add(spouse); }

        if (childrenList != null) { peopleList.addAll(childrenList); }

        return peopleList;
    }

    private void _createEventColorMap(ArrayList<EventIDResult> data) {
        ArrayList<String> eventTypes = getAllEventTypes(data);
        ArrayList<Float> colors = getAllMapColors();

        for (int i = 0; i < eventTypes.size(); ++i) {
            eventTypeColors.put(eventTypes.get(i), colors.get(i % colors.size()));
        }
    }

    private ArrayList<String> getAllEventTypes(ArrayList<EventIDResult> data) {
        ArrayList<String> eventTypes = new ArrayList<>();

        for (int i = 0; i < data.size(); ++i) {
            String eventType = data.get(i).getEventType();
            if (!eventTypes.contains(eventType)) { eventTypes.add(eventType); }
        }
        return eventTypes;
    }

    private ArrayList<Float> getAllMapColors() {
        ArrayList<Float> colors = new ArrayList<>();
        colors.add(BitmapDescriptorFactory.HUE_AZURE);
        colors.add(BitmapDescriptorFactory.HUE_BLUE);
        colors.add(BitmapDescriptorFactory.HUE_ORANGE);
        colors.add(BitmapDescriptorFactory.HUE_ROSE);
        colors.add(BitmapDescriptorFactory.HUE_CYAN);
        colors.add(BitmapDescriptorFactory.HUE_GREEN);
        colors.add(BitmapDescriptorFactory.HUE_MAGENTA);
        colors.add(BitmapDescriptorFactory.HUE_RED);
        colors.add(BitmapDescriptorFactory.HUE_VIOLET);
        colors.add(BitmapDescriptorFactory.HUE_YELLOW);
        return colors;
    }

    private void _makePersonEvents() {
        ArrayList<Person> pl = new ArrayList<>(people.values());
        ArrayList<Event> ets = filteredEvents;
        for (int i = 0; i < pl.size(); ++i) {
            personEvents.put(pl.get(i).getPersonID(), new ArrayList<>());
            for (int j = 0; j < ets.size(); ++j) {
                if (ets.get(j).getPersonID().equals(pl.get(i).getPersonID())) {
                    Objects.requireNonNull(personEvents.get(pl.get(i).getPersonID())).add(ets.get(j));
                }
            }
        }
        sortPersonEvents();
    }

    private void sortPersonEvents() {
        ArrayList<Person> pl = new ArrayList<>(people.values());
        Map<String, ArrayList<Event>> newMap = new HashMap<>();
        for (int i = 0; i < pl.size(); ++i) {
            ArrayList<Event> nsEvents = personEvents.get(pl.get(i).getPersonID());
            assert nsEvents != null;
            ArrayList<Event> sortedEvents = sort(nsEvents);
            newMap.put(pl.get(i).getPersonID(), sortedEvents);
        }
        personEvents = newMap;
    }

    private ArrayList<Event> sort(ArrayList<Event> nsEvents) {
        ArrayList<Event> sortedEvents = new ArrayList<>();
        ArrayList<Integer> yearList = new ArrayList<>();
        for (int i = 0; i < nsEvents.size(); ++i) {
            yearList.add(nsEvents.get(i).getYear());
        }

        List<Integer> newList = new ArrayList<>(yearList);
        Collections.sort(newList);

        for (int i = 0; i < newList.size(); ++i) {
            for (int j = 0; j < nsEvents.size(); ++j) {
                Event e = nsEvents.get(j);
                if (e.getYear() == newList.get(i) && !sortedEvents.contains(e)) {
                    sortedEvents.add(e);
                }
            }
        }
        return sortedEvents;
    }

    private void _makeFilteredEvents() {
        filteredEvents.clear();
        _calcMaternalAncestors();
        _calcPaternalAncestors();
        ArrayList<Event> temp = new ArrayList<>();
        ArrayList<Event> allEvents = new ArrayList<>(events.values());
        for (int i = 0; i < allEvents.size(); ++i) {
            if (allEvents.get(i).getPersonID().equals(user.getPersonID())) {
                filteredEvents.add(allEvents.get(i));
            }
            if (allEvents.get(i).getPersonID().equals(user.getSpouseID())) {
                filteredEvents.add(allEvents.get(i));
            }
        }
        if (settings.isMother()) { filteredEvents.addAll(maternalEvents); }
        if (settings.isFather()) { filteredEvents.addAll(paternalEvents); }
        if (!settings.isMale()) {
            for (int i = 0; i < filteredEvents.size(); ++i) {
                if (getPersonById(filteredEvents.get(i).getPersonID()).getGender().equals("m")) {
                    temp.add(filteredEvents.get(i));
                }
            }
            filteredEvents.removeAll(temp);
        }
        if (!settings.isFemale()) {
            for (int i = 0; i < filteredEvents.size(); ++i) {
                if (getPersonById(filteredEvents.get(i).getPersonID()).getGender().equals("f")) {
                    temp.add(filteredEvents.get(i));
                }
            }
            filteredEvents.removeAll(temp);
        }
    }
}
