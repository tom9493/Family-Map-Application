package RequestResult;

public class FillResult {
    private boolean success;
    String message;

    public FillResult() { success = false; }

    public FillResult(boolean success) {
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
