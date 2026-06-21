package models;

import java.time.LocalDate;

public class MedicalDocument extends Document {
    public MedicalDocument(String name, String documentNumber,
                            LocalDate issueDate, LocalDate expiryDate,
                            String issuingAuthority, String notes) {
        super(name, documentNumber, "Medical", issueDate, expiryDate, issuingAuthority, notes);
    }
    public MedicalDocument(String docId, String name, String documentNumber,
                            LocalDate issueDate, LocalDate expiryDate, LocalDate addedDate,
                            String issuingAuthority, String notes, int version) {
        super(docId, name, documentNumber, "Medical", issueDate, expiryDate, addedDate, issuingAuthority, notes, version);
    }
    @Override public String getDocumentType() { return "Medical"; }
    @Override public String getCategoryIcon() { return "[MED]"; }
}
