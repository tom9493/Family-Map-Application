package RequestResult;

import java.util.ArrayList;

public class EventResult {
    private ArrayList<EventIDResult> data;
    private boolean success;
    String message;

    public EventResult() { success = false; }

    public EventResult(ArrayList<EventIDResult> events, boolean success) {
        data = events;
        this.success = success;
        this.message = null;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public ArrayList<EventIDResult> getEvents() {
        return data;
    }

    public void setEvents(ArrayList<EventIDResult> events) {
        data = events;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
