package engine;

import interfaces.Expirable;
import models.Document;
import utils.UIUtil;
import java.util.*;
import java.util.stream.Collectors;

public class AlertEngine implements Expirable {

    private List<Document> documents;

    public AlertEngine(List<Document> documents) {
        this.documents = documents;
    }

    @Override
    public List<Document> getExpiringDocuments(int withinDays) {
        return documents.stream()
                .filter(d -> d.isExpiringSoon(withinDays))
                .sorted(Comparator.comparing(Document::getExpiryDate))
                .collect(Collectors.toList());
    }

    @Override
    public void showExpiryAlerts() {
        List<Document> expired   = documents.stream().filter(Document::isExpired).collect(Collectors.toList());
        List<Document> in30days  = getExpiringDocuments(30);
        List<Document> in90days  = getExpiringDocuments(90).stream()
                .filter(d -> !d.isExpiringSoon(30)).collect(Collectors.toList());

        if (expired.isEmpty() && in30days.isEmpty() && in90days.isEmpty()) return;

        System.out.println();
        UIUtil.printSectionTitle("Expiry Alerts");

        for (Document d : expired) {
            System.out.println(UIUtil.RED + UIUtil.BOLD
                    + "  [EXPIRED]  " + d.getCategoryIcon() + " " + d.getName()
                    + " - Expired on " + d.getExpiryDate().format(Document.DATE_FORMAT)
                    + UIUtil.RESET);
        }
        for (Document d : in30days) {
            System.out.println(UIUtil.YELLOW + UIUtil.BOLD
                    + "  [URGENT!]  " + d.getCategoryIcon() + " " + d.getName()
                    + " - Expires in " + d.daysUntilExpiry() + " days ("
                    + d.getExpiryDate().format(Document.DATE_FORMAT) + ")"
                    + UIUtil.RESET);
        }
        for (Document d : in90days) {
            System.out.println(UIUtil.CYAN
                    + "  [REMINDER] " + d.getCategoryIcon() + " " + d.getName()
                    + " - Expires in " + d.daysUntilExpiry() + " days ("
                    + d.getExpiryDate().format(Document.DATE_FORMAT) + ")"
                    + UIUtil.RESET);
        }
    }
}
