package interfaces;

public interface Auditable {
    void logAction(String username, String action, String detail);
    void showRecentLogs(int count);
}
