package DAO;

import RequestResult.EventIDResult;
import model.Event;
import java.sql.*;
import java.util.ArrayList;

public class EventDao {
    private final Connection conn;

    public EventDao(Connection conn)
    {
        this.conn = conn;
    }

    /**
     * Inserts an Event into the database
     * @param event Event object
     * @throws DataAccessException If something goes wrong interacting with database
     */
    public void insert(Event event) throws DataAccessException {
        String sql = "INSERT INTO Events (EventID, AssociatedUsername, PersonID, Latitude, Longitude, " +
                "Country, City, EventType, Year) VALUES(?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getAssociatedUsername());
            stmt.setString(3, event.getPersonID());
            stmt.setFloat(4, event.getLatitude());
            stmt.setFloat(5, event.getLongitude());
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());
            stmt.setInt(9, event.getYear());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     * Returns event object if it is found in Database. Otherwise, returns null
     * @param eventID Event ID String for finding event in database
     * @return Event object
     * @throws DataAccessException If something goes wrong interacting with database
     */
    public Event find(String eventID, String username) throws DataAccessException {
        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE EventID = ? AND associatedUsername = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventID);
            stmt.setString(2, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"),
                        rs.getFloat("Longitude"), rs.getString("Country"),
                        rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));

                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        } finally { finallyStatement(rs); }
        return null;
    }

    /**
     * Removes all Events from database
     * @throws DataAccessException If something goes wrong interacting with database
     * @return
     */
    public boolean clear() throws DataAccessException {
        try (Statement stmt = conn.createStatement()) {
            String sql = "DELETE FROM Events";
            stmt.executeUpdate(sql);
        } catch (SQLException e) { throw new DataAccessException("SQL Error encountered while clearing Persons table");}
        return true;
    }

    /**
     * Gathers all events associated with the username into one array and returns the array
     * @return ArrayList of events
     */
    public ArrayList<EventIDResult> getAllEvents(String associatedUsername) throws DataAccessException {
        if (associatedUsername == null) { throw new DataAccessException("Associated Username is null"); }

        ArrayList<EventIDResult> allEvents = new ArrayList<>();
        EventIDResult event;
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE associatedUsername = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, associatedUsername);
            rs = stmt.executeQuery();
            while (rs.next()) {
                event = new EventIDResult(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"),
                        rs.getFloat("Longitude"), rs.getString("Country"),
                        rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"), true);

                allEvents.add(event);
            }
            return allEvents;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting all events");
        } finally { finallyStatement(rs); }
    }

    /**
     * Removes all events in database with associated userName
     * @param associatedUsername
     */
    public void removeEvents(String associatedUsername) throws DataAccessException {
        String sql = "DELETE FROM Events WHERE associatedUsername = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, associatedUsername);
            stmt.executeUpdate();
        } catch (SQLException e) { throw new DataAccessException("SQL Error encountered while removing Events"); }
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

