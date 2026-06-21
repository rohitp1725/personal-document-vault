package models;
import java.time.LocalDate;

public class EducationDocument extends Document {
    public EducationDocument(String name, String documentNumber,
                              LocalDate issueDate, LocalDate expiryDate,
                              String issuingAuthority, String notes) {
        super(name, documentNumber, "Education", issueDate, expiryDate, issuingAuthority, notes);
    }
    public EducationDocument(String docId, String name, String documentNumber,
                              LocalDate issueDate, LocalDate expiryDate, LocalDate addedDate,
                              String issuingAuthority, String notes, int version) {
        super(docId, name, documentNumber, "Education", issueDate, expiryDate, addedDate, issuingAuthority, notes, version);
    }
    @Override public String getDocumentType() { return "Education"; }
    @Override public String getCategoryIcon() { return "[EDU]"; }
}
