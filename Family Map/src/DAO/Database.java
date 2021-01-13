package DAO;

import Service.Generator;
import model.Event;
import model.Person;
import model.User;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;

import static Server.Server.Logger;

public class Database {
    private Connection conn;

    /**
     * Opens connection to Database
     * @return Connection object
     * @throws DataAccessException If something goes wrong interacting with database
     */
    public Connection openConnection() throws DataAccessException {
        Logger.entering("Database", "openConnection");

        try {
            final String CONNECTION_URL = "jdbc:sqlite:FamilyMap.sqlite";
            conn = DriverManager.getConnection(CONNECTION_URL);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Unable to open connection to database");
        }
        return conn;
    }

    /**
     * Opens connection if Connection object "conn" is not null
     * @return Connection object
     * @throws DataAccessException If something goes wrong interacting with database
     */
    public Connection getConnection() throws DataAccessException {
        if (conn == null) { return openConnection(); }
        else { return conn; }
    }

    /**
     * Closes connection to Database
     * @param commit if the database operations should commit to execution or to not commit and rollback
     * @throws DataAccessException If something goes wrong interacting with database
     */
    public void closeConnection(boolean commit) throws DataAccessException {
        Logger.entering("Database", "closeConnection");

        try {
            if (commit) { conn.commit(); }
            else { conn.rollback(); }
            conn.close();
            conn = null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Unable to close database connection");
        }
    }

    /**
     * Deletes all tables in events
     * @throws DataAccessException If something goes wrong interacting with database
     */
    public void clearTables() throws DataAccessException {
        try (Statement stmt = conn.createStatement()){
            String sql = "DELETE FROM Events;" +
                    "DELETE FROM Users;" +
                    "DELETE FROM AuthTokens;" +
                    "DELETE FROM Persons;";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing tables");
        }
    }

    /**
     * Removes items in database used in tests. Some items must be inserted into the database like during a register
     * test to check if two register calls with the same username will work.
     * @param username
     * @throws DataAccessException
     */
    public void testClear(String username) throws DataAccessException {
        conn = getConnection();
        UserDao uDao = new UserDao(conn);
        PersonDao pDao = new PersonDao(conn);
        EventDao eDao = new EventDao(conn);

        uDao.removeUser(username);
        pDao.removePeople(username);
        eDao.removeEvents(username);
    }

    /**
     * Removes existing info attached to User and repopulates generations and events
     * @param specifiedUser User specified
     * @param generations Number of generations to produce
     * @return Boolean if the operation succeeded or not
     */
    public boolean fill(Person specifiedUser, int generations) throws DataAccessException, FileNotFoundException {
        Generator generator = new Generator();
        generator.getJsonFiles();
        PersonDao pDao = new PersonDao(conn);
        EventDao eDao = new EventDao(conn);

        generator.generatePeople(specifiedUser, generations);

        for (Person person : generator.getPeople()) { pDao.insert(person); }
        for (Event event : generator.getEvents()) { eDao.insert(event); }

        return true;
    }

    /**
     * Clears database tables and repopulates with given data
     * @param users Array of user objects
     * @param persons Array of person objects
     * @param events Array of event objects
     * @throws DataAccessException If something goes wrong interacting with database
     */
    public boolean load(ArrayList<User> users, ArrayList<Person> persons,
                        ArrayList<Event> events) throws DataAccessException {
        UserDao uDao = new UserDao(conn);
        PersonDao pDao = new PersonDao(conn);
        EventDao eDao = new EventDao(conn);

        clearTables();

        for (User user : users) { uDao.insert(user); }
        for (Person person : persons) { pDao.insert(person); }
        for (Event event : events) { eDao.insert(event); }

        return true;
    }
}
