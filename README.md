package interfaces;

import models.Document;
import java.util.List;

public interface Searchable {
    List<Document> searchByName(String keyword);
    List<Document> searchByCategory(String category);
    List<Document> sortByExpiryDate();
    List<Document> sortByCategory();
    List<Document> sortByRecentlyAdded();
}
