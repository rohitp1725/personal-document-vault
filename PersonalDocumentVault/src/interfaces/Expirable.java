package interfaces;

import models.Document;
import java.util.List;

public interface Expirable {
    List<Document> getExpiringDocuments(int withinDays);
    void showExpiryAlerts();
}
