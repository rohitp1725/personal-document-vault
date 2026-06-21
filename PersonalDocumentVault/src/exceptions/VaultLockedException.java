package exceptions;

public class VaultLockedException extends Exception {
    public VaultLockedException(String username) {
        super("Vault for user '" + username + "' is LOCKED due to too many failed login attempts.");
    }
}
