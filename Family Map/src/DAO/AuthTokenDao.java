package DAO;

import model.AuthToken;
import java.sql.*;

public class AuthTokenDao {
    private final Connection conn;

    /**
     * Creates Data Access Object to Authentication Tokens in Database
     * @param conn Connection to Database
     */
    public AuthTokenDao(Connection conn) { this.conn = conn; }

    /**
     * Insert authentication token associated with current User logged in
     * @param authToken
     */
    public void insert(AuthToken authToken) throws DataAccessException {
        String sql = "INSERT INTO AuthTokens (associatedUsername, authString)" +
                "VALUES(?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken.getUsername());
            stmt.setString(2, authToken.getAuthString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     * Find the authToken in the database and returns the associatedUsername
     * @param authString
     * @return associated username or null if not found
     */
    public String find(String authString) throws DataAccessException {
        AuthToken authToken;
        ResultSet rs = null;
        String sql = "SELECT * FROM AuthTokens WHERE authString = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authString);
            rs = stmt.executeQuery();

            if (rs.next()) {
                authToken = new AuthToken(rs.getString("associatedUsername"),
                        rs.getString("authString"));
                return authToken.getUsername();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding authToken");
        } finally {
            if (rs != null) {
                try { rs.close(); }
                catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return null;
    }

    /**
     * Removes all authentication tokens from database
     */
    public boolean clear() throws DataAccessException {
        try (Statement stmt = conn.createStatement()) {
            String sql = "DELETE FROM AuthTokens";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing AuthTokens table");
        }
        return true;
    }

}
