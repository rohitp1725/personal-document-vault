package models;
import java.time.LocalDate;

public class PropertyDocument extends Document {
    public PropertyDocument(String name, String documentNumber,
                             LocalDate issueDate, LocalDate expiryDate,
                             String issuingAuthority, String notes) {
        super(name, documentNumber, "Property", issueDate, expiryDate, issuingAuthority, notes);
    }
    public PropertyDocument(String docId, String name, String documentNumber,
                             LocalDate issueDate, LocalDate expiryDate, LocalDate addedDate,
                             String issuingAuthority, String notes, int version) {
        super(docId, name, documentNumber, "Property", issueDate, expiryDate, addedDate, issuingAuthority, notes, version);
    }
    @Override public String getDocumentType() { return "Property"; }
    @Override public String getCategoryIcon() { return "[PROP]"; }
}
