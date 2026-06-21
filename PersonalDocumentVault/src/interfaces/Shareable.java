package interfaces;

public interface Shareable {
    boolean shareDocumentWith(String docId, String targetUsername, int durationDays);
    void revokeShare(String docId, String targetUsername);
}
