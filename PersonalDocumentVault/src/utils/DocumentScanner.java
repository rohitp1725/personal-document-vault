package utils;

import java.util.*;

public class DocumentScanner {

    // Pre-filled templates simulating document scan auto-fill
    private static final Map<String, String[]> TEMPLATES = new LinkedHashMap<>();

    static {
        TEMPLATES.put("Aadhaar Card",       new String[]{"Aadhaar Card",   "XXXX-XXXX-1234", "UIDAI",                 "Identity",  "N/A",        "10 years"});
        TEMPLATES.put("PAN Card",           new String[]{"PAN Card",        "ABCDE1234F",     "Income Tax Dept",       "Identity",  "N/A",        "Lifetime"});
        TEMPLATES.put("Passport",           new String[]{"Passport",        "A1234567",       "Ministry of External",  "Identity",  "10-06-2035", "10 years"});
        TEMPLATES.put("Voter ID",           new String[]{"Voter ID",        "MH/12/345/6789", "Election Commission",   "Identity",  "N/A",        "Lifetime"});
        TEMPLATES.put("Driving Licence",    new String[]{"Driving Licence", "MH12-2020-0001", "RTO Maharashtra",       "Identity",  "15-08-2030", "10 years"});
        TEMPLATES.put("Health Insurance",   new String[]{"Health Insurance","INS-2024-98765",  "Star Health Insurance", "Medical",   "31-03-2026", "1 year"});
        TEMPLATES.put("Prescription",       new String[]{"Prescription",    "RX-2024-001",    "City Hospital",         "Medical",   "30-06-2025", "6 months"});
        TEMPLATES.put("PF Account",         new String[]{"PF Account",      "MH/PUN/123456",  "EPFO",                  "Financial", "N/A",        "Lifetime"});
        TEMPLATES.put("Bank Passbook",      new String[]{"Bank Passbook",   "ACC-0012345678", "State Bank of India",   "Financial", "N/A",        "Lifetime"});
        TEMPLATES.put("10th Marksheet",     new String[]{"10th Marksheet",  "SSC-2020-12345", "Maharashtra Board",     "Education", "N/A",        "Lifetime"});
        TEMPLATES.put("12th Marksheet",     new String[]{"12th Marksheet",  "HSC-2022-67890", "Maharashtra Board",     "Education", "N/A",        "Lifetime"});
        TEMPLATES.put("Degree Certificate", new String[]{"Degree Certificate","SPPU-2025-001","Savitribai Phule Univ", "Education", "N/A",        "Lifetime"});
        TEMPLATES.put("Rent Agreement",     new String[]{"Rent Agreement",  "RENT-2024-PUNE", "Registered Office",     "Property",  "31-03-2026", "1 year"});
    }

    public static void showScanMenu(Scanner sc) {
        UIUtil.printSectionTitle("Document Scanner Simulation");
        System.out.println(UIUtil.CYAN + "  Scanning document... Please select document type:" + UIUtil.RESET);
        System.out.println();

        List<String> keys = new ArrayList<>(TEMPLATES.keySet());
        for (int i = 0; i < keys.size(); i++) {
            System.out.println(UIUtil.WHITE + "  [" + (i + 1) + "] " + keys.get(i) + UIUtil.RESET);
        }
        UIUtil.printThinDivider();
        UIUtil.printPrompt("Select template (0 to skip)");
        String input = sc.nextLine().trim();

        try {
            int choice = Integer.parseInt(input);
            if (choice == 0) return;
            if (choice < 1 || choice > keys.size()) {
                UIUtil.printError("Invalid selection.");
                return;
            }
            String key = keys.get(choice - 1);
            String[] data = TEMPLATES.get(key);

            System.out.println();
            System.out.println(UIUtil.GREEN + UIUtil.BOLD + "  [SCAN COMPLETE] Auto-filled details:" + UIUtil.RESET);
            UIUtil.printThinDivider();
            System.out.println(UIUtil.CYAN + "  Document Name     : " + UIUtil.RESET + data[0]);
            System.out.println(UIUtil.CYAN + "  Document Number   : " + UIUtil.RESET + data[1]);
            System.out.println(UIUtil.CYAN + "  Issuing Authority : " + UIUtil.RESET + data[2]);
            System.out.println(UIUtil.CYAN + "  Category          : " + UIUtil.RESET + data[3]);
            System.out.println(UIUtil.CYAN + "  Suggested Expiry  : " + UIUtil.RESET + data[4]);
            System.out.println(UIUtil.CYAN + "  Validity          : " + UIUtil.RESET + data[5]);
            UIUtil.printThinDivider();
            UIUtil.printInfo("You can use these auto-filled values or enter your own.");

        } catch (NumberFormatException e) {
            UIUtil.printError("Invalid input.");
        }
    }
}
