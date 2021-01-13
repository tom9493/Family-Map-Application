package model;

public class AuthToken {
    private String associatedUsername;
    private String authString;

    public AuthToken(String username, String authString) {
        this.associatedUsername = username;
        this.authString = authString;
    }

    public String getUsername() {
        return associatedUsername;
    }

    public void setUsername(String username) {
        this.associatedUsername = username;
    }

    public String getAuthString() {
        return authString;
    }

    public void setAuthString(String authString) { this.authString = authString; }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof AuthToken) {
            AuthToken oAuthToken = (AuthToken) o;
            return oAuthToken.getUsername().equals(getUsername()) &&
                    oAuthToken.getAuthString().equals(getAuthString());
        } else {
            return false;
        }
    }
}

