package RequestResult;

public class LoginResult {
    private String authToken;
    private String userName;
    private String personID;
    private boolean success;
    private String message;

    public LoginResult() { success = false; }

    public LoginResult(String authToken, String userName, String personID, boolean success) {
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
        this.success = success;
        this.message = null;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) { return false; }
        if (o instanceof LoginResult) {
            LoginResult oLoginResult = (LoginResult) o;
            return oLoginResult.getAuthToken().equals(getAuthToken()) &&
                    oLoginResult.getUserName().equals(getUserName()) &&
                    oLoginResult.getPersonID().equals(getPersonID()) &&
                    oLoginResult.isSuccess() == isSuccess();
        }
        else { return false; }
    }
}
