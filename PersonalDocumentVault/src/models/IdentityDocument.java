package models;

import java.time.LocalDate;

public class IdentityDocument extends Document {
    public IdentityDocument(String name, String documentNumber,
                             LocalDate issueDate, LocalDate expiryDate,
                             String issuingAuthority, String notes) {
        super(name, documentNumber, "Identity", issueDate, expiryDate, issuingAuthority, notes);
    }
    public IdentityDocument(String docId, String name, String documentNumber,
                             LocalDate issueDate, LocalDate expiryDate, LocalDate addedDate,
                             String issuingAuthority, String notes, int version) {
        super(docId, name, documentNumber, "Identity", issueDate, expiryDate, addedDate, issuingAuthority, notes, version);
    }
    @Override public String getDocumentType() { return "Identity"; }
    @Override public String getCategoryIcon() { return "[ID]"; }
}
