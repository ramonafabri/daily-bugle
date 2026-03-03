package hu.progmasters.dailybugle.exception;

public class InvalidAdminOperationException extends RuntimeException {
    public InvalidAdminOperationException(String message) {
        super(message);
    }
}
