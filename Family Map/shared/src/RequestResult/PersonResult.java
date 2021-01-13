package RequestResult;

import java.util.ArrayList;

public class PersonResult {
    private ArrayList<PersonIDResult> data;
    private boolean success;
    private String message;

    public PersonResult() {}

    public PersonResult(ArrayList<PersonIDResult> people, boolean success) {
        this.data = people;
        this.success = success;
        this.message = null;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public ArrayList<PersonIDResult> getPeople() {
        return data;
    }

    public void setPeople(ArrayList<PersonIDResult> people) {
        this.data = people;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
