package DAO;

import RequestResult.PersonIDResult;
import model.Person;
import java.sql.*;
import java.util.ArrayList;

public class PersonDao {
    private final Connection conn;

    public PersonDao(Connection conn) { this.conn = conn; }

    /**
     * Inserts a Person into the database
     * @param person Person object
     */
    public void insert(Person person) throws DataAccessException {
        if (!person.getGender().equals("m") & !person.getGender().equals("f")) {
            throw new DataAccessException("Gender not m or f");
        }

        String sql = "INSERT INTO Persons (PersonID, AssociatedUsername, firstName, lastName, gender, " +
                "fatherID, motherID, spouseID) VALUES(?,?,?,?,?,?,?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            if (person.getFatherID() != null) { stmt.setString(6, person.getFatherID()); }
            if (person.getMotherID() != null) { stmt.setString(7, person.getMotherID()); }
            if (person.getSpouseID() != null) { stmt.setString(8, person.getSpouseID()); }
            stmt.executeUpdate();
        } catch (SQLException e) {throw new DataAccessException("Error encountered while inserting into the database");}
    }

    /**
     * Returns Person object if it is found in the database. Otherwise, returns null
     * @param personID Person ID String to find in database
     * @return Person object
     * @throws DataAccessException If something goes wrong interacting with database
     */
    public Person find(String personID, String username) throws DataAccessException {
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE personID = ? AND associatedUsername = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            stmt.setString(2, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString("personID"), rs.getString("associatedUsername"),
                        rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("gender"), rs.getString("fatherID"),
                        rs.getString("motherID"), rs.getString("spouseID"));

                return person;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
        } finally { finallyStatement(rs); }
        return null;
    }

    /**
     * Clears all Person objects from database
     */
    public boolean clear() throws DataAccessException {
        try (Statement stmt = conn.createStatement()) {
            String sql = "DELETE FROM Persons";
            stmt.executeUpdate(sql);
        } catch (SQLException e) { throw new DataAccessException("SQL Error encountered while clearing Persons table");}
        return true;
    }

    /**
     * Gets all Person objects linked to associatedUsername and returns them in an array
     * @param associatedUsername
     * @return Array of person objects
     */
    public ArrayList<PersonIDResult> getAllPeople(String associatedUsername) throws DataAccessException {
        if (associatedUsername == null) { throw new DataAccessException("Associated Username is null"); }

        ArrayList<PersonIDResult> allPeople = new ArrayList<>();
        PersonIDResult person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE associatedUsername = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, associatedUsername);
            rs = stmt.executeQuery();
            while (rs.next()) {
                person = new PersonIDResult(rs.getString("personID"), rs.getString("associatedUsername"),
                        rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("gender"), rs.getString("fatherID"),
                        rs.getString("motherID"), rs.getString("spouseID"), true);

                allPeople.add(person);
            }
            return allPeople;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting all people");
        } finally { finallyStatement(rs); }
    }

    /**
     * Removes all people in database with associated userName
     * @param associatedUsername
     */
    public void removePeople(String associatedUsername) throws DataAccessException {
        String sql = "DELETE FROM Persons WHERE associatedUsername = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, associatedUsername);
            stmt.executeUpdate();
        } catch (SQLException e) { throw new DataAccessException("SQL Error encountered while removing People"); }
    }

    /**
     * Gets personID associated with username when a user logs in
     * @param userName
     * @param firstName
     * @param lastName
     * @param gender
     * @return personID
     * @throws DataAccessException
     */
    public String getPersonID(String userName, String firstName, String lastName, String gender) throws DataAccessException {
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE associatedUsername = ? AND firstName = ? " +
                "AND lastName = ? AND gender = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, gender);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString("personID"), rs.getString("associatedUsername"),
                        rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("gender"), rs.getString("fatherID"),
                        rs.getString("motherID"), rs.getString("spouseID"));

                return person.getPersonID();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding personID");
        } finally { finallyStatement(rs); }
        return null;
    }

    /**
     * Finally statement. Prevents duplicate code.
     * @param rs Result set
     */
    public void finallyStatement(ResultSet rs) {
        if (rs != null) {
            try { rs.close(); }
            catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
