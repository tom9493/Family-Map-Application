package Service;

import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {
    private Generator generator;

    @BeforeEach
    void setUp() throws FileNotFoundException {
        RegisterService.isTest = true;
        generator = new Generator();
        generator.getJsonFiles();
    }

    @AfterEach
    void tearDown() {
        RegisterService.isTest = false;
    }

    @Test
    void randStringGenerator() {
        String s = generator.randStringGenerator(10);
        assertNotNull(s);
        assertEquals(10, s.length());
        s = generator.randStringGenerator(25);
        assertNotNull(s);
        assertEquals(25, s.length());
    }

    @Test
    void generatePeopleAndEvents() {
        // Generated people and events are random, so cannot check for specific variables except
        int generations = 4;
        int totalPeople = getTotalPeople(generations);
        Person person = new Person(generator.randStringGenerator(10), "testUsername", "testFirstName",
                "testLastName", "m");
        generator.generatePeople(person, generations);

        assertEquals(totalPeople, generator.getPeople().size());
        assertNotNull(generator.getEvents());

        generations = 7;
        totalPeople = getTotalPeople(generations);
        generator.generatePeople(person, generations);

        assertEquals(totalPeople, generator.getPeople().size());
        assertNotNull(generator.getEvents());
    }

    public int getTotalPeople(int generations) {
        int numPeople = 2;
        for (int i = 0; i < generations; ++i) { numPeople = numPeople * 2; }
        return numPeople - 1;
    }
}