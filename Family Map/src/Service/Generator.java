package Service;

import RequestResult.LoadRequest;
import com.google.gson.Gson;
import jsonHelper.*;
import model.Event;
import model.Person;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Random;

public class Generator {
    LocationData locData;
    FNames fNames;
    MNames mNames;
    SNames sNames;
    Random rand = new Random();
    ArrayList<Person> people = new ArrayList<>();
    ArrayList<Event> events = new ArrayList<>();
    int currentGeneration = 0;
    int generations = 4;

    // String for generating random people, events, and strings
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    String numbers = "0123456789";
    String[] eventTypes = {"Baptism", "Rodeo", "Death", "Graduation", "Nobel Prize", "Festival", "Concert", "Party",
    "Book Convention", "Art Convention", "Vacation", "Purchased Company", "Bought House", "Retired", "First Kiss"};

    public ArrayList<Person> getPeople() { return people; }
    public ArrayList<Event> getEvents() { return events; }

    /**
     * Generates random String with length provided. For authTokens, eventIDs, and personIDs
     * @param length
     * @return
     */
    public String randStringGenerator(int length) {
        StringBuilder randString = new StringBuilder(length);

        for (int i = 0; i < length; ++i) {
            if (rand.nextInt(2) == 0) { randString.append(characters.charAt(rand.nextInt(characters.length()))); }
            else { randString.append(numbers.charAt(rand.nextInt(numbers.length()))); }
        }
        return randString.toString();
    }

    /**
     * Generates people for the family tree of user given with number of generations provided
     * @param currentUser
     * @param generations
     * @return
     */
    public void generatePeople(Person currentUser, int generations) {
        people.clear();
        this.generations = generations + 1;

        // For each person, starting with the user, create a boy and a girl person object. Make
        // those two object's personIDs each other's spouseIDs. Make the female the current person's
        // motherID and male the fatherID. Recursively pass in each new person made until the specified
        // generations are filled. Make realistic events based upon currentGeneration variable.

        generatePeople_Helper(currentUser);
        --currentGeneration;
    }

    public void generatePeople_Helper(Person currentPerson) {
        ++currentGeneration;
        generateBirthEvent(currentPerson.getUsername(), currentPerson.getPersonID(), currentGeneration);

        if (currentGeneration > 1) { generateDeathEvent(currentPerson.getUsername(),
                currentPerson.getPersonID(), currentGeneration); }
        if (currentGeneration == generations) { people.add(currentPerson); }
        else {
            String femaleName = fNames.getData()[rand.nextInt(fNames.getData().length)];
            String maleName = mNames.getData()[rand.nextInt(mNames.getData().length)];
            String surName = sNames.getData()[rand.nextInt(sNames.getData().length)];

            String femaleID = randStringGenerator(10);
            String maleID = randStringGenerator(10);
            Person mother = new Person(femaleID, currentPerson.getUsername(), femaleName, surName, "f");
            Person father = new Person(maleID, currentPerson.getUsername(), maleName, surName, "m");
            mother.setSpouseID(maleID);
            father.setSpouseID(femaleID);

            int index = rand.nextInt(locData.getData().length);
            Location location = locData.getData()[index];
            int dif = rand.nextInt(10);
            int year = 2020 - ((currentGeneration - 1) * 20) + dif;

            generateMarriageEvent(currentPerson.getUsername(), mother.getPersonID(), year, location);
            generateMarriageEvent(currentPerson.getUsername(), father.getPersonID(), year, location);

            currentPerson.setMotherID(femaleID);
            currentPerson.setFatherID(maleID);
            people.add(currentPerson);

            generatePeople_Helper(father);
            --currentGeneration;
            generatePeople_Helper(mother);
            --currentGeneration;
        }
    }

    /**
     * Generates associated events for family tree
     * @param username
     * @return
     */
    public void generateBirthEvent(String username, String personID, int currentGeneration) {
        int index = rand.nextInt(locData.getData().length);
        Location location = locData.getData()[index];
        String eventType = "birth";
        String eventID = randStringGenerator(8);

        int dif = rand.nextInt(10);
        int year = 2020 - (currentGeneration * 25) + dif;
        Event event = new Event(eventID, username, personID, location.getLatitude(), location.getLongitude(),
                location.getCountry(), location.getCity(), eventType, year);
        events.add(event);
    }

    /**
     * Generates Death Event for person object
     * @param username
     * @param personID
     * @param currentGeneration
     */
    public void generateDeathEvent(String username, String personID, int currentGeneration) {
        int index = rand.nextInt(locData.getData().length);
        Location location = locData.getData()[index];
        String eventType = "death";
        String eventID = randStringGenerator(8);

        int dif = rand.nextInt(10);
        int year = 2020 - ((currentGeneration - 2) * 10) + dif;
        Event event = new Event(eventID, username, personID, location.getLatitude(), location.getLongitude(),
                location.getCountry(), location.getCity(), eventType, year);
        events.add(event);
    }

    /**
     * Generates Marriage Event for person object
     * @param username
     * @param personID
     * @param year
     * @param location
     */
    public void generateMarriageEvent(String username, String personID, int year, Location location) {
        String eventType = "marriage";
        String eventID = randStringGenerator(8);

        Event event = new Event(eventID, username, personID, location.getLatitude(), location.getLongitude(),
                location.getCountry(), location.getCity(), eventType, year);
        events.add(event);
    }


    /**
     * Gets all the json files and stores them as Java objects to generate family tree
     * @throws FileNotFoundException
     */
    public void getJsonFiles() throws FileNotFoundException {
        Gson gson = new Gson();

        Reader reader = new FileReader("C:\\Users\\Thomas\\IdeaProjects\\Family Map\\json\\locations.json");
        locData = gson.fromJson(reader, LocationData.class);
        reader = new FileReader("C:\\Users\\Thomas\\IdeaProjects\\Family Map\\json\\fnames.json");
        fNames = gson.fromJson(reader, FNames.class);
        reader = new FileReader("C:\\Users\\Thomas\\IdeaProjects\\Family Map\\json\\mnames.json");
        mNames = gson.fromJson(reader, MNames.class);
        reader = new FileReader("C:\\Users\\Thomas\\IdeaProjects\\Family Map\\json\\snames.json");
        sNames = gson.fromJson(reader, SNames.class);
    }

    /**
     * Gets file for example load request and returns associated Java object
     * @return
     * @throws FileNotFoundException
     */
    public LoadRequest getExampleLoadRequest() throws FileNotFoundException {
        Gson gson = new Gson();
        Reader reader = new FileReader("C:\\Users\\Thomas\\IdeaProjects\\Family Map\\json\\example.json");
        return gson.fromJson(reader, LoadRequest.class);
    }

    /**
     * Gets file for bad example oad request for load test
     * @return
     * @throws FileNotFoundException
     */
    public LoadRequest getBadExampleLoadRequest() throws FileNotFoundException {
        Gson gson = new Gson();
        Reader reader = new FileReader("C:\\Users\\Thomas\\IdeaProjects\\Family Map\\json\\badExample.json");
        return gson.fromJson(reader, LoadRequest.class);
    }

    public LoadRequest fillClientPassOff() throws FileNotFoundException {
        Gson gson = new Gson();
        Reader reader = new FileReader("C:\\Users\\Thomas\\Desktop\\Family Map\\json\\ClientPassOff.json");
        return gson.fromJson(reader, LoadRequest.class);
    }
}
