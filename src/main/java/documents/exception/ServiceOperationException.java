package documents.exception;

public class ServiceOperationException extends Exception {
    public ServiceOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceOperationException(String message) {
        super(message);
    }
}
