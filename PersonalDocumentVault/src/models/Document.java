package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class Document {

    protected String docId;
    protected String name;
    protected String documentNumber;
    protected String category;
    protected LocalDate issueDate;
    protected LocalDate expiryDate;
    protected LocalDate addedDate;
    protected String issuingAuthority;
    protected String notes;
    protected int version;
    protected boolean isEncrypted;

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public Document(String name, String documentNumber, String category,
                    LocalDate issueDate, LocalDate expiryDate, String issuingAuthority, String notes) {
        this.docId = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.name = name;
        this.documentNumber = documentNumber;
        this.category = category;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.issuingAuthority = issuingAuthority;
        this.notes = notes;
        this.addedDate = LocalDate.now();
        this.version = 1;
        this.isEncrypted = false;
    }

    // Reconstruct from file
    public Document(String docId, String name, String documentNumber, String category,
                    LocalDate issueDate, LocalDate expiryDate, LocalDate addedDate,
                    String issuingAuthority, String notes, int version) {
        this.docId = docId;
        this.name = name;
        this.documentNumber = documentNumber;
        this.category = category;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.addedDate = addedDate;
        this.issuingAuthority = issuingAuthority;
        this.notes = notes;
        this.version = version;
        this.isEncrypted = false;
    }

    public abstract String getDocumentType();
    public abstract String getCategoryIcon();

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    public boolean isExpiringSoon(int days) {
        if (expiryDate == null) return false;
        LocalDate threshold = LocalDate.now().plusDays(days);
        return !isExpired() && expiryDate.isBefore(threshold);
    }

    public long daysUntilExpiry() {
        if (expiryDate == null) return Long.MAX_VALUE;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    public String toFileString() {
        return docId + "|" + name + "|" + documentNumber + "|" + category + "|"
                + (issueDate != null ? issueDate.format(DATE_FORMAT) : "N/A") + "|"
                + (expiryDate != null ? expiryDate.format(DATE_FORMAT) : "N/A") + "|"
                + addedDate.format(DATE_FORMAT) + "|"
                + issuingAuthority + "|" + notes + "|" + version + "|" + getDocumentType();
    }

    // Getters
    public String getDocId() { return docId; }
    public String getName() { return name; }
    public String getDocumentNumber() { return documentNumber; }
    public String getCategory() { return category; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public LocalDate getAddedDate() { return addedDate; }
    public String getIssuingAuthority() { return issuingAuthority; }
    public String getNotes() { return notes; }
    public int getVersion() { return version; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public void setIssuingAuthority(String issuingAuthority) { this.issuingAuthority = issuingAuthority; }
    public void setNotes(String notes) { this.notes = notes; }
    public void incrementVersion() { this.version++; }
}
