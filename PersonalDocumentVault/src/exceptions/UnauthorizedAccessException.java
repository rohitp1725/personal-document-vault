package exceptions;

public class UnauthorizedAccessException extends Exception {
    public UnauthorizedAccessException(String detail) {
        super("Unauthorized access: " + detail);
    }
}
