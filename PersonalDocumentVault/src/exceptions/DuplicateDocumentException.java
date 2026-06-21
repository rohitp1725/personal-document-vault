package exceptions;

public class DuplicateDocumentException extends Exception {
    public DuplicateDocumentException(String docName) {
        super("Document '" + docName + "' already exists in this vault.");
    }
}
