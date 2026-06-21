package exceptions;

public class DocumentExpiredException extends Exception {
    public DocumentExpiredException(String docName) {
        super("Document '" + docName + "' has already expired.");
    }
}
