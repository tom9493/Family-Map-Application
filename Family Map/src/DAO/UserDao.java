package DAO;

import model.User;
import java.sql.*;

public class UserDao {
    private final Connection conn;

    public UserDao(Connection conn) { this.conn = conn; }

    /**
     * Creates user and stores it in Database
     * @param user
     */
    public void insert(User user) throws DataAccessException {
        if (!user.getGender().equals("m") & !user.getGender().equals("f")) {
            throw new DataAccessException("Gender not m or f");
        }

        String sql = "INSERT INTO Users (userName, password, email, firstName, lastName, gender)" +
                "VALUES(?,?,?,?,?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getGender());
            stmt.executeUpdate();
        } catch (SQLException e) { throw new DataAccessException("Error encountered while inserting user into db"); }
    }

    /**
     * Finds person with Username and password and returns the personID
     * @param username
     * @param password
     * @return User object found
     * @throws DataAccessException
     */
    public User find(String username, String password) throws DataAccessException {
        if (password == null) { throw new DataAccessException("No password provided"); }

        User user;
        ResultSet rs = null;
        String sql = "SELECT * FROM Users WHERE userName = ? AND password = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("userName"), rs.getString("password"),
                        rs.getString("email"), rs.getString("firstName"),
                         rs.getString("lastName"), rs.getString("gender"));

                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding user");
        } finally { finallyStatement(rs); }
        return null;
    }

    /**
     * Clears all Users from the database
     * @return boolean indicating if the method succeeded
     */
    public boolean clear() throws DataAccessException {
        try (Statement stmt = conn.createStatement()) {
            String sql = "DELETE FROM Users";
            stmt.executeUpdate(sql);
        } catch (SQLException e) { throw new DataAccessException("SQL Error encountered while clearing Users"); }
        return true;
    }

    /**
     * Get User object with just a username. Needed for fill service
     * @param username
     * @return
     * @throws DataAccessException
     */
    public User findUser(String username) throws DataAccessException {
        User user;
        ResultSet rs = null;
        String sql = "SELECT * FROM Users WHERE userName = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("userName"), rs.getString("password"),
                        rs.getString("email"), rs.getString("firstName"),
                        rs.getString("lastName"), rs.getString("gender"));

                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding user");
        } finally { finallyStatement(rs); }
        return null;
    }

    /**
     * Removes user in database with associated userName
     * @param associatedUsername
     */
    public void removeUser(String associatedUsername) throws DataAccessException {
        String sql = "DELETE FROM Users WHERE userName = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, associatedUsername);
            stmt.executeUpdate();
        } catch (SQLException e) { throw new DataAccessException("SQL Error encountered while removing user"); }
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
