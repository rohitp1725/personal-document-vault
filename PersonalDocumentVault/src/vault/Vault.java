package vault;

import interfaces.Searchable;
import interfaces.Shareable;
import models.*;
import utils.FileHandler;
import engine.AuditLogger;
import java.util.*;
import java.util.stream.Collectors;

public class Vault implements Searchable, Shareable {

    private String owner;
    private List<Document> documents;
    private List<VersionedDocument> versionHistory;
    private List<Reminder> reminders;

    public Vault(String owner) {
        this.owner = owner;
        this.documents = FileHandler.loadDocuments(owner);
        this.versionHistory = FileHandler.loadVersionHistory(owner);
        this.reminders = FileHandler.loadReminders(owner);
    }

    // ─── Document CRUD ──────────────────────────────────────
    public void addDocument(Document doc) {
        documents.add(doc);
        save();
        AuditLogger.getInstance().logAction(owner, "ADD_DOC", doc.getName() + " [" + doc.getCategory() + "]");
    }

    public boolean deleteDocument(String docId) {
        boolean removed = documents.removeIf(d -> d.getDocId().equals(docId));
        if (removed) {
            save();
            AuditLogger.getInstance().logAction(owner, "DELETE_DOC", "DocID: " + docId);
        }
        return removed;
    }

    public Document findById(String docId) {
        return documents.stream().filter(d -> d.getDocId().equals(docId)).findFirst().orElse(null);
    }

    public void updateDocument(Document doc) {
        // Save version snapshot before update
        VersionedDocument snapshot = new VersionedDocument(doc);
        versionHistory.add(snapshot);
        FileHandler.saveVersionSnapshot(owner, snapshot);
        doc.incrementVersion();
        save();
        AuditLogger.getInstance().logAction(owner, "UPDATE_DOC", doc.getName() + " -> v" + doc.getVersion());
    }

    // ─── Searchable ─────────────────────────────────────────
    @Override
    public List<Document> searchByName(String keyword) {
        String kw = keyword.toLowerCase();
        return documents.stream()
                .filter(d -> d.getName().toLowerCase().contains(kw)
                        || d.getDocumentNumber().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    @Override
    public List<Document> searchByCategory(String category) {
        return documents.stream()
                .filter(d -> d.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<Document> sortByExpiryDate() {
        return documents.stream()
                .sorted(Comparator.comparing(
                        d -> d.getExpiryDate() != null ? d.getExpiryDate() : java.time.LocalDate.MAX))
                .collect(Collectors.toList());
    }

    @Override
    public List<Document> sortByCategory() {
        return documents.stream()
                .sorted(Comparator.comparing(Document::getCategory))
                .collect(Collectors.toList());
    }

    @Override
    public List<Document> sortByRecentlyAdded() {
        return documents.stream()
                .sorted(Comparator.comparing(Document::getAddedDate).reversed())
                .collect(Collectors.toList());
    }

    // ─── Shareable ──────────────────────────────────────────
    @Override
    public boolean shareDocumentWith(String docId, String targetUsername, int durationDays) {
        Document doc = findById(docId);
        if (doc == null) return false;
        AuditLogger.getInstance().logAction(owner, "SHARE_DOC",
                doc.getName() + " shared with " + targetUsername + " for " + durationDays + " days");
        return true;
    }

    @Override
    public void revokeShare(String docId, String targetUsername) {
        AuditLogger.getInstance().logAction(owner, "REVOKE_SHARE",
                "DocID: " + docId + " revoked from " + targetUsername);
    }

    // ─── Reminders ──────────────────────────────────────────
    public void addReminder(Reminder r) {
        reminders.add(r);
        FileHandler.saveReminders(owner, reminders);
    }

    public void markReminderDone(String reminderId) {
        for (Reminder r : reminders) {
            if (r.getReminderId().equals(reminderId)) {
                r.setDone(true);
                break;
            }
        }
        FileHandler.saveReminders(owner, reminders);
    }

    public List<Reminder> getReminders() { return reminders; }

    // ─── Backup & Save ──────────────────────────────────────
    public void save() {
        FileHandler.saveDocuments(owner, documents);
    }

    public void backup() {
        FileHandler.createBackup(owner, documents);
        AuditLogger.getInstance().logAction(owner, "BACKUP", "Vault backup created on logout");
    }

    // ─── Summary Stats ──────────────────────────────────────
    public Map<String, Integer> getCategoryStats() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String[] cats = {"Identity", "Medical", "Financial", "Education", "Property"};
        for (String c : cats) stats.put(c, 0);
        for (Document d : documents) stats.merge(d.getCategory(), 1, Integer::sum);
        return stats;
    }

    public int getTotalDocuments() { return documents.size(); }

    public int getExpiringCount(int days) {
        return (int) documents.stream().filter(d -> d.isExpiringSoon(days)).count();
    }

    public int getExpiredCount() {
        return (int) documents.stream().filter(Document::isExpired).count();
    }

    // ─── Version History ────────────────────────────────────
    public List<VersionedDocument> getHistoryForDoc(String docId) {
        return versionHistory.stream()
                .filter(v -> v.getDocId().equals(docId))
                .collect(Collectors.toList());
    }

    // Getters
    public List<Document> getDocuments() { return documents; }
    public String getOwner() { return owner; }
}
