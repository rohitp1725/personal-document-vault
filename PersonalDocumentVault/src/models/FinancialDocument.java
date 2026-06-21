package models;
import java.time.LocalDate;

public class FinancialDocument extends Document {
    public FinancialDocument(String name, String documentNumber,
                              LocalDate issueDate, LocalDate expiryDate,
                              String issuingAuthority, String notes) {
        super(name, documentNumber, "Financial", issueDate, expiryDate, issuingAuthority, notes);
    }
    public FinancialDocument(String docId, String name, String documentNumber,
                              LocalDate issueDate, LocalDate expiryDate, LocalDate addedDate,
                              String issuingAuthority, String notes, int version) {
        super(docId, name, documentNumber, "Financial", issueDate, expiryDate, addedDate, issuingAuthority, notes, version);
    }
    @Override public String getDocumentType() { return "Financial"; }
    @Override public String getCategoryIcon() { return "[FIN]"; }
}
