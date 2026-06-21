package exceptions;

public class AuthenticationFailedException extends Exception {
    public AuthenticationFailedException(int attemptsLeft) {
        super("Incorrect password. Attempts remaining: " + attemptsLeft);
    }
}
