package RequestResult;

public class ClearResult {
    private boolean success;
    private String message;

    public ClearResult() { success = false; }

    public ClearResult(boolean success) {
        this.success = success;
        this.message = null;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
