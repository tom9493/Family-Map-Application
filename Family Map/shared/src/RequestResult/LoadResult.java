package RequestResult;

public class LoadResult {
    private boolean success;
    private String message;

    public LoadResult() { success = false; }

    public LoadResult(boolean success) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null) { return false; }
        if (o instanceof LoadResult) {
            LoadResult oLoadResult = (LoadResult) o;
            return oLoadResult.isSuccess() == isSuccess();
        }
        else { return false; }
    }
}
