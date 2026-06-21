package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VersionedDocument {

    private String docId;
    private String name;
    private String documentNumber;
    private String category;
    private LocalDate expiryDate;
    private int version;
    private LocalDate snapshotDate;
    private String notes;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public VersionedDocument(Document doc) {
        this.docId = doc.getDocId();
        this.name = doc.getName();
        this.documentNumber = doc.getDocumentNumber();
        this.category = doc.getCategory();
        this.expiryDate = doc.getExpiryDate();
        this.version = doc.getVersion();
        this.snapshotDate = LocalDate.now();
        this.notes = doc.getNotes();
    }

    public VersionedDocument(String docId, String name, String documentNumber,
                              String category, LocalDate expiryDate,
                              int version, LocalDate snapshotDate, String notes) {
        this.docId = docId;
        this.name = name;
        this.documentNumber = documentNumber;
        this.category = category;
        this.expiryDate = expiryDate;
        this.version = version;
        this.snapshotDate = snapshotDate;
        this.notes = notes;
    }

    public String toFileString() {
        return docId + "~" + name + "~" + documentNumber + "~" + category + "~"
                + (expiryDate != null ? expiryDate.format(FMT) : "N/A") + "~"
                + version + "~" + snapshotDate.format(FMT) + "~" + notes;
    }

    public static VersionedDocument fromFileString(String line) {
        String[] p = line.split("~", 8);
        if (p.length < 8) return null;
        LocalDate expiry = p[4].equals("N/A") ? null : LocalDate.parse(p[4], FMT);
        LocalDate snapshot = LocalDate.parse(p[6], FMT);
        return new VersionedDocument(p[0], p[1], p[2], p[3], expiry,
                Integer.parseInt(p[5]), snapshot, p[7]);
    }

    public String getDocId() { return docId; }
    public int getVersion() { return version; }
    public LocalDate getSnapshotDate() { return snapshotDate; }
    public String getName() { return name; }
    public String getDocumentNumber() { return documentNumber; }

    @Override
    public String toString() {
        return "  v" + version + " | " + name + " | DocNo: " + documentNumber
                + " | Expiry: " + (expiryDate != null ? expiryDate.format(FMT) : "N/A")
                + " | Saved on: " + snapshotDate.format(FMT);
    }
}
