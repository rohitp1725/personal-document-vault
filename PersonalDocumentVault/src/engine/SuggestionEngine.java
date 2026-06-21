package engine;

import models.Document;
import utils.UIUtil;
import java.util.*;

public class SuggestionEngine {

    private static final Map<String, String[]> RECOMMENDED_DOCS = new LinkedHashMap<>();

    static {
        RECOMMENDED_DOCS.put("Identity",  new String[]{"Aadhaar Card", "PAN Card", "Passport", "Voter ID", "Driving Licence"});
        RECOMMENDED_DOCS.put("Medical",   new String[]{"Health Insurance", "Prescription", "Blood Report"});
        RECOMMENDED_DOCS.put("Financial", new String[]{"PF Account", "Bank Passbook", "Income Tax Return"});
        RECOMMENDED_DOCS.put("Education", new String[]{"10th Marksheet", "12th Marksheet", "Degree Certificate"});
        RECOMMENDED_DOCS.put("Property",  new String[]{"Rent Agreement"});
    }

    public void showSuggestions(List<Document> documents) {
        UIUtil.printSectionTitle("Smart Suggestions");

        List<String> suggestions = new ArrayList<>();

        // Check for missing important documents
        Set<String> existingNames = new HashSet<>();
        for (Document d : documents) existingNames.add(d.getName().toLowerCase());

        for (Map.Entry<String, String[]> entry : RECOMMENDED_DOCS.entrySet()) {
            for (String rec : entry.getValue()) {
                if (!existingNames.contains(rec.toLowerCase())) {
                    suggestions.add("Consider adding: " + rec + " (" + entry.getKey() + ")");
                }
            }
        }

        // Check for expiring documents
        for (Document d : documents) {
            if (d.isExpired()) {
                suggestions.add("EXPIRED: '" + d.getName() + "' expired! Please renew it.");
            } else if (d.isExpiringSoon(30)) {
                suggestions.add("EXPIRING SOON: '" + d.getName() + "' expires in " + d.daysUntilExpiry() + " days. Renew now!");
            } else if (d.isExpiringSoon(90)) {
                suggestions.add("REMINDER: '" + d.getName() + "' expires in " + d.daysUntilExpiry() + " days.");
            }
        }

        // Category balance suggestions
        Map<String, Integer> catCount = new HashMap<>();
        for (Document d : documents) {
            catCount.merge(d.getCategory(), 1, Integer::sum);
        }
        if (!catCount.containsKey("Medical")) {
            suggestions.add("You have no Medical documents. Add health insurance or reports.");
        }
        if (!catCount.containsKey("Financial")) {
            suggestions.add("You have no Financial documents. Add bank or PF details.");
        }

        if (suggestions.isEmpty()) {
            UIUtil.printSuccess("Your vault looks complete! Great job.");
            return;
        }

        int count = 0;
        for (String s : suggestions) {
            if (count >= 6) break; // Show max 6 suggestions
            if (s.startsWith("EXPIRED")) {
                System.out.println(UIUtil.RED + "  [!] " + s + UIUtil.RESET);
            } else if (s.startsWith("EXPIRING")) {
                System.out.println(UIUtil.YELLOW + "  [!] " + s + UIUtil.RESET);
            } else if (s.startsWith("REMINDER")) {
                System.out.println(UIUtil.YELLOW + "  [-] " + s + UIUtil.RESET);
            } else {
                System.out.println(UIUtil.CYAN + "  [+] " + s + UIUtil.RESET);
            }
            count++;
        }
    }
}
