import engine.*;
import exceptions.*;
import models.*;
import utils.*;
import vault.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static VaultManager manager;
    static SuggestionEngine suggestionEngine = new SuggestionEngine();

    public static void main(String[] args) {
        manager = new VaultManager(sc);
        UIUtil.clearScreen();
        UIUtil.printBanner();
        mainMenu();
    }

    // ════════════════════════════════════════════════════════
    //  MAIN MENU (pre-login)
    // ════════════════════════════════════════════════════════
    static void mainMenu() {
        while (true) {
            UIUtil.printDivider();
            UIUtil.printMenuOption(1, "Login");
            UIUtil.printMenuOption(2, "Sign Up");
            UIUtil.printMenuOption(3, "Recover Vault (Forgot Password)");
            UIUtil.printMenuOption(0, "Exit");
            UIUtil.printDivider();
            UIUtil.printPrompt("Choice");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    try {
                        if (manager.login()) {
                            UIUtil.printSuccess("Welcome back, " + manager.getCurrentUser().getFullName() + "!");
                            vaultMenu();
                        }
                    } catch (VaultLockedException e) {
                        UIUtil.printError(e.getMessage());
                        UIUtil.printInfo("Use option 3 to recover your vault.");
                    } catch (AuthenticationFailedException e) {
                        UIUtil.printError(e.getMessage());
                    }
                    break;
                case "2": manager.signup(); break;
                case "3": manager.recoverVault(); break;
                case "0":
                    System.out.println(UIUtil.CYAN + "\n  Thank you for using DOC VAULT. Stay safe!\n" + UIUtil.RESET);
                    System.exit(0);
                default:
                    UIUtil.printError("Invalid choice.");
            }
        }
    }

    // ════════════════════════════════════════════════════════
    //  VAULT MENU (post-login)
    // ════════════════════════════════════════════════════════
    static void vaultMenu() {
        Vault vault = manager.getCurrentVault();
        User user   = manager.getCurrentUser();

        // Show dashboard on login
        showDashboard(vault, user);

        // Show expiry alerts on login
        new AlertEngine(vault.getDocuments()).showExpiryAlerts();

        // Show upcoming reminders
        showUpcomingReminders(vault);

        while (true) {
            System.out.println();
            UIUtil.printHeader("VAULT MENU - @" + user.getUsername());
            UIUtil.printMenuOption(1,  "Add Document");
            UIUtil.printMenuOption(2,  "View All Documents");
            UIUtil.printMenuOption(3,  "Search & Sort Documents");
            UIUtil.printMenuOption(4,  "Delete Document");
            UIUtil.printMenuOption(5,  "Update Document");
            UIUtil.printMenuOption(6,  "View Document Version History");
            UIUtil.printMenuOption(7,  "Smart Suggestions");
            UIUtil.printMenuOption(8,  "Reminders");
            UIUtil.printMenuOption(9,  "Family Vault Network");
            UIUtil.printMenuOption(10, "EMERGENCY MODE");
            UIUtil.printMenuOption(11, "Export Summary Report");
            UIUtil.printMenuOption(12, "View Audit Log");
            UIUtil.printMenuOption(0,  "Logout");
            UIUtil.printDivider();
            UIUtil.printPrompt("Choice");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":  addDocumentMenu(vault); break;
                case "2":  viewAllDocuments(vault.getDocuments()); break;
                case "3":  searchSortMenu(vault); break;
                case "4":  deleteDocument(vault); break;
                case "5":  updateDocument(vault); break;
                case "6":  viewVersionHistory(vault); break;
                case "7":  suggestionEngine.showSuggestions(vault.getDocuments()); break;
                case "8":  remindersMenu(vault); break;
                case "9":  familyNetworkMenu(user); break;
                case "10": emergencyMode(user); break;
                case "11": exportReport(vault, user); break;
                case "12": AuditLogger.getInstance().showRecentLogs(20); break;
                case "0":
                    manager.logout();
                    UIUtil.printBanner();
                    return;
                default: UIUtil.printError("Invalid option.");
            }
            UIUtil.pressEnterToContinue(sc);
        }
    }

    // ════════════════════════════════════════════════════════
    //  DASHBOARD
    // ════════════════════════════════════════════════════════
    static void showDashboard(Vault vault, User user) {
        UIUtil.printHeader("DASHBOARD - " + user.getFullName());
        System.out.println(UIUtil.CYAN + "  Role: " + UIUtil.RESET + user.getRole()
                + UIUtil.CYAN + "   |   Blood Group: " + UIUtil.RESET + user.getBloodGroup());
        System.out.println();

        Map<String, Integer> stats = vault.getCategoryStats();
        int total    = vault.getTotalDocuments();
        int expiring = vault.getExpiringCount(30);
        int expired  = vault.getExpiredCount();

        System.out.println(UIUtil.BOLD + UIUtil.GREEN
                + "  Total Documents  : " + total + UIUtil.RESET);

        String expiryColor = expiring > 0 ? UIUtil.YELLOW : UIUtil.GREEN;
        System.out.println(expiryColor + UIUtil.BOLD
                + "  Expiring Soon    : " + expiring + " (within 30 days)" + UIUtil.RESET);

        if (expired > 0)
            System.out.println(UIUtil.RED + UIUtil.BOLD
                    + "  Expired          : " + expired + UIUtil.RESET);

        System.out.println();
        System.out.print(UIUtil.CYAN + "  Category Breakdown: " + UIUtil.RESET);
        for (Map.Entry<String, Integer> e : stats.entrySet()) {
            System.out.print(UIUtil.WHITE + e.getKey() + ": " + e.getValue() + "  " + UIUtil.RESET);
        }
        System.out.println();
        UIUtil.printDivider();
    }

    // ════════════════════════════════════════════════════════
    //  ADD DOCUMENT
    // ════════════════════════════════════════════════════════
    static void addDocumentMenu(Vault vault) {
        UIUtil.printHeader("ADD NEW DOCUMENT");

        // Offer Scanner simulation
        UIUtil.printInfo("Want to use Document Scanner to auto-fill? (y/n)");
        UIUtil.printPrompt("Choice");
        String scan = sc.nextLine().trim();
        if (scan.equalsIgnoreCase("y")) {
            DocumentScanner.showScanMenu(sc);
            System.out.println();
            UIUtil.printInfo("Enter actual document details below (use scanned values or your own):");
        }

        UIUtil.printPrompt("Document Name");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) { UIUtil.printError("Name cannot be empty."); return; }

        System.out.println(UIUtil.CYAN + "\n  Select Category:" + UIUtil.RESET);
        System.out.println("  [1] Identity  [2] Medical  [3] Financial  [4] Education  [5] Property");
        UIUtil.printPrompt("Category");
        String catChoice = sc.nextLine().trim();
        String category = getCategoryFromChoice(catChoice);

        UIUtil.printPrompt("Document Number");
        String docNo = sc.nextLine().trim();

        UIUtil.printPrompt("Issuing Authority");
        String authority = sc.nextLine().trim();

        LocalDate issueDate = promptDate("Issue Date (dd-MM-yyyy, or press ENTER to skip)");
        LocalDate expiryDate = promptDate("Expiry Date (dd-MM-yyyy, or press ENTER if no expiry)");

        UIUtil.printPrompt("Notes (optional)");
        String notes = sc.nextLine().trim();
        if (notes.isEmpty()) notes = "N/A";

        Document doc = createDocument(name, docNo, category, issueDate, expiryDate, authority, notes);
        vault.addDocument(doc);

        UIUtil.printSuccess("Document '" + name + "' added to vault! ID: " + doc.getDocId());
    }

    // ════════════════════════════════════════════════════════
    //  VIEW DOCUMENTS
    // ════════════════════════════════════════════════════════
    static void viewAllDocuments(List<Document> docs) {
        UIUtil.printHeader("ALL DOCUMENTS");
        if (docs.isEmpty()) { UIUtil.printInfo("No documents in vault."); return; }

        String currentCat = "";
        for (Document d : docs) {
            if (!d.getCategory().equals(currentCat)) {
                currentCat = d.getCategory();
                System.out.println(UIUtil.PURPLE + UIUtil.BOLD + "\n  -- " + currentCat + " --" + UIUtil.RESET);
            }
            printDocumentFull(d);
        }
        System.out.println();
    }

    static void printDocumentFull(Document d) {
        String expiry = d.getExpiryDate() != null ? d.getExpiryDate().format(Document.DATE_FORMAT) : "N/A";
        String statusColor = d.isExpired() ? UIUtil.RED
                : d.isExpiringSoon(30) ? UIUtil.YELLOW
                : UIUtil.GREEN;
        String statusText = d.isExpired() ? " [EXPIRED]"
                : d.isExpiringSoon(30) ? " [!" + d.daysUntilExpiry() + "d]"
                : "";

        System.out.println(UIUtil.CYAN + "  [" + d.getDocId() + "] " + UIUtil.RESET
                + d.getCategoryIcon() + " "
                + UIUtil.BOLD + d.getName() + UIUtil.RESET
                + statusColor + statusText + UIUtil.RESET);
        System.out.println("         No: " + d.getDocumentNumber()
                + " | Authority: " + d.getIssuingAuthority()
                + " | Expiry: " + expiry
                + " | v" + d.getVersion()
                + " | Added: " + d.getAddedDate().format(Document.DATE_FORMAT));
        if (!d.getNotes().equals("N/A"))
            System.out.println(UIUtil.CYAN + "         Notes: " + UIUtil.RESET + d.getNotes());
        UIUtil.printThinDivider();
    }

    // ════════════════════════════════════════════════════════
    //  SEARCH & SORT
    // ════════════════════════════════════════════════════════
    static void searchSortMenu(Vault vault) {
        UIUtil.printHeader("SEARCH & SORT");
        UIUtil.printMenuOption(1, "Search by Name / Document Number");
        UIUtil.printMenuOption(2, "Filter by Category");
        UIUtil.printMenuOption(3, "Sort by Expiry Date (soonest first)");
        UIUtil.printMenuOption(4, "Sort by Category (A-Z)");
        UIUtil.printMenuOption(5, "Sort by Recently Added");
        UIUtil.printPrompt("Choice");
        String c = sc.nextLine().trim();

        List<Document> result;
        switch (c) {
            case "1":
                UIUtil.printPrompt("Keyword");
                String kw = sc.nextLine().trim();
                result = vault.searchByName(kw);
                UIUtil.printInfo("Found " + result.size() + " result(s):");
                viewAllDocuments(result);
                break;
            case "2":
                System.out.println("  [1] Identity  [2] Medical  [3] Financial  [4] Education  [5] Property");
                UIUtil.printPrompt("Category");
                result = vault.searchByCategory(getCategoryFromChoice(sc.nextLine().trim()));
                viewAllDocuments(result);
                break;
            case "3": viewAllDocuments(vault.sortByExpiryDate()); break;
            case "4": viewAllDocuments(vault.sortByCategory());    break;
            case "5": viewAllDocuments(vault.sortByRecentlyAdded()); break;
            default: UIUtil.printError("Invalid option.");
        }
    }

    // ════════════════════════════════════════════════════════
    //  DELETE
    // ════════════════════════════════════════════════════════
    static void deleteDocument(Vault vault) {
        UIUtil.printHeader("DELETE DOCUMENT");
        viewAllDocuments(vault.getDocuments());
        UIUtil.printPrompt("Enter Document ID to delete");
        String id = sc.nextLine().trim().toUpperCase();
        UIUtil.printPrompt("Confirm delete '" + id + "'? (yes/no)");
        if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
            if (vault.deleteDocument(id)) UIUtil.printSuccess("Document deleted.");
            else UIUtil.printError("Document ID not found.");
        } else {
            UIUtil.printInfo("Deletion cancelled.");
        }
    }

    // ════════════════════════════════════════════════════════
    //  UPDATE
    // ════════════════════════════════════════════════════════
    static void updateDocument(Vault vault) {
        UIUtil.printHeader("UPDATE DOCUMENT");
        viewAllDocuments(vault.getDocuments());
        UIUtil.printPrompt("Enter Document ID to update");
        String id = sc.nextLine().trim().toUpperCase();
        Document doc = vault.findById(id);
        if (doc == null) { UIUtil.printError("Document not found."); return; }

        UIUtil.printInfo("Leave blank to keep current value.");
        UIUtil.printPrompt("New Name [" + doc.getName() + "]");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) doc.setName(name);

        UIUtil.printPrompt("New Document Number [" + doc.getDocumentNumber() + "]");
        String no = sc.nextLine().trim();
        if (!no.isEmpty()) doc.setDocumentNumber(no);

        UIUtil.printPrompt("New Issuing Authority [" + doc.getIssuingAuthority() + "]");
        String auth = sc.nextLine().trim();
        if (!auth.isEmpty()) doc.setIssuingAuthority(auth);

        LocalDate newExpiry = promptDate("New Expiry Date (dd-MM-yyyy) [ENTER to keep]");
        if (newExpiry != null) doc.setExpiryDate(newExpiry);

        UIUtil.printPrompt("New Notes [" + doc.getNotes() + "]");
        String notes = sc.nextLine().trim();
        if (!notes.isEmpty()) doc.setNotes(notes);

        vault.updateDocument(doc);
        UIUtil.printSuccess("Document updated to v" + doc.getVersion() + "!");
    }

    // ════════════════════════════════════════════════════════
    //  VERSION HISTORY
    // ════════════════════════════════════════════════════════
    static void viewVersionHistory(Vault vault) {
        UIUtil.printHeader("VERSION HISTORY");
        UIUtil.printPrompt("Enter Document ID");
        String id = sc.nextLine().trim().toUpperCase();
        Document doc = vault.findById(id);
        if (doc == null) { UIUtil.printError("Document not found."); return; }

        List<VersionedDocument> history = vault.getHistoryForDoc(id);
        System.out.println(UIUtil.BOLD + "\n  History for: " + doc.getName() + UIUtil.RESET);
        if (history.isEmpty()) {
            UIUtil.printInfo("No version history yet (updated documents appear here).");
            return;
        }
        for (VersionedDocument vd : history) {
            System.out.println(vd.toString());
        }
        System.out.println(UIUtil.GREEN + "  Current: v" + doc.getVersion() + " | " + doc.getName()
                + " | " + doc.getDocumentNumber() + UIUtil.RESET);
    }

    // ════════════════════════════════════════════════════════
    //  REMINDERS
    // ════════════════════════════════════════════════════════
    static void remindersMenu(Vault vault) {
        UIUtil.printHeader("REMINDERS");
        UIUtil.printMenuOption(1, "Add Reminder");
        UIUtil.printMenuOption(2, "View All Reminders");
        UIUtil.printMenuOption(3, "Mark Reminder as Done");
        UIUtil.printPrompt("Choice");
        String c = sc.nextLine().trim();

        switch (c) {
            case "1":
                UIUtil.printPrompt("Reminder Title");
                String title = sc.nextLine().trim();
                LocalDate date = promptDate("Reminder Date (dd-MM-yyyy)");
                if (date == null) { UIUtil.printError("Invalid date."); return; }
                UIUtil.printPrompt("Description");
                String desc = sc.nextLine().trim();
                vault.addReminder(new Reminder(title, date, desc));
                UIUtil.printSuccess("Reminder added!");
                break;
            case "2":
                List<Reminder> all = vault.getReminders();
                if (all.isEmpty()) { UIUtil.printInfo("No reminders set."); break; }
                UIUtil.printSectionTitle("All Reminders");
                for (Reminder r : all) {
                    String color = r.isDone() ? UIUtil.GREEN
                            : r.isUpcoming(3) ? UIUtil.YELLOW : UIUtil.WHITE;
                    System.out.println(color + "  " + r + UIUtil.RESET);
                }
                break;
            case "3":
                UIUtil.printPrompt("Reminder ID");
                vault.markReminderDone(sc.nextLine().trim().toUpperCase());
                UIUtil.printSuccess("Reminder marked done.");
                break;
        }
    }

    static void showUpcomingReminders(Vault vault) {
        List<Reminder> upcoming = new ArrayList<>();
        for (Reminder r : vault.getReminders()) {
            if (r.isUpcoming(7) && !r.isDone()) upcoming.add(r);
        }
        if (upcoming.isEmpty()) return;
        System.out.println();
        UIUtil.printSectionTitle("Upcoming Reminders (Next 7 Days)");
        for (Reminder r : upcoming) {
            System.out.println(UIUtil.YELLOW + "  [REMINDER] " + r.getTitle()
                    + " - " + r.getReminderDate().format(Document.DATE_FORMAT) + UIUtil.RESET);
        }
    }

    // ════════════════════════════════════════════════════════
    //  FAMILY NETWORK
    // ════════════════════════════════════════════════════════
    static void familyNetworkMenu(User user) {
        FamilyNetwork fn = new FamilyNetwork(user.getUsername());
        UIUtil.printHeader("FAMILY VAULT NETWORK");
        UIUtil.printMenuOption(1, "Link a Family Member");
        UIUtil.printMenuOption(2, "View Linked Family Members");
        UIUtil.printMenuOption(3, "View a Family Member's Vault");
        UIUtil.printMenuOption(4, "Share a Document with Family");
        UIUtil.printPrompt("Choice");
        String c = sc.nextLine().trim();

        switch (c) {
            case "1":
                UIUtil.printPrompt("Family Member's Username");
                fn.linkFamilyMember(sc.nextLine().trim());
                break;
            case "2":
                fn.showFamilyMembers();
                break;
            case "3":
                fn.showFamilyMembers();
                UIUtil.printPrompt("Enter username to view");
                fn.viewFamilyMemberVault(sc.nextLine().trim());
                break;
            case "4":
                UIUtil.printPrompt("Document ID to share");
                String docId = sc.nextLine().trim().toUpperCase();
                fn.showFamilyMembers();
                UIUtil.printPrompt("Share with (username)");
                String target = sc.nextLine().trim();
                UIUtil.printPrompt("Duration (days)");
                try {
                    int days = Integer.parseInt(sc.nextLine().trim());
                    if (manager.getCurrentVault().shareDocumentWith(docId, target, days))
                        UIUtil.printSuccess("Document shared with " + target + " for " + days + " days.");
                } catch (NumberFormatException e) {
                    UIUtil.printError("Invalid number.");
                }
                break;
        }
    }

    // ════════════════════════════════════════════════════════
    //  EMERGENCY MODE
    // ════════════════════════════════════════════════════════
    static void emergencyMode(User user) {
        UIUtil.clearScreen();
        System.out.println(UIUtil.RED + UIUtil.BOLD);
        System.out.println("  *** EMERGENCY MODE ACTIVATED ***");
        System.out.println(UIUtil.RESET);

        // Own info
        System.out.println(UIUtil.YELLOW + UIUtil.BOLD + "  YOUR EMERGENCY INFO:" + UIUtil.RESET);
        UIUtil.printThinDivider();
        System.out.println("  Name          : " + user.getFullName());
        System.out.println("  Blood Group   : " + UIUtil.RED + UIUtil.BOLD + user.getBloodGroup() + UIUtil.RESET);
        System.out.println("  Emergency No  : " + user.getEmergencyContact());

        Vault vault = manager.getCurrentVault();
        List<Document> docs = vault.getDocuments();

        System.out.println(UIUtil.YELLOW + "\n  CRITICAL DOCUMENTS:" + UIUtil.RESET);
        UIUtil.printThinDivider();
        for (Document d : docs) {
            if (d.getCategory().equals("Medical") || d.getCategory().equals("Identity")) {
                System.out.println("  " + d.getCategoryIcon() + " " + UIUtil.BOLD + d.getName()
                        + UIUtil.RESET + " | " + d.getDocumentNumber()
                        + (d.getIssuingAuthority() != null ? " | " + d.getIssuingAuthority() : ""));
            }
        }

        // Family emergency access
        FamilyNetwork fn = new FamilyNetwork(user.getUsername());
        List<String> members = fn.getLinkedMembers();
        if (!members.isEmpty()) {
            System.out.println(UIUtil.YELLOW + "\n  FAMILY EMERGENCY ACCESS:" + UIUtil.RESET);
            UIUtil.printThinDivider();
            for (int i = 0; i < members.size(); i++) {
                System.out.println("  [" + (i + 1) + "] " + members.get(i));
            }
            UIUtil.printPrompt("View family member info? (Enter number or 0 to skip)");
            try {
                int choice = Integer.parseInt(sc.nextLine().trim());
                if (choice > 0 && choice <= members.size()) {
                    fn.emergencyAccess(members.get(choice - 1));
                }
            } catch (NumberFormatException ignored) {}
        }

        AuditLogger.getInstance().logAction(user.getUsername(), "EMERGENCY_MODE", "Activated");
    }

    // ════════════════════════════════════════════════════════
    //  EXPORT REPORT
    // ════════════════════════════════════════════════════════
    static void exportReport(Vault vault, User user) {
        String filename = "data/report_" + user.getUsername() + "_"
                + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(filename))) {
            pw.println("=".repeat(60));
            pw.println("  PERSONAL DOCUMENT VAULT - SUMMARY REPORT");
            pw.println("  User     : " + user.getFullName() + " (@" + user.getUsername() + ")");
            pw.println("  Role     : " + user.getRole());
            pw.println("  Blood Gp : " + user.getBloodGroup());
            pw.println("  Date     : " + LocalDate.now().format(Document.DATE_FORMAT));
            pw.println("=".repeat(60));
            pw.println();

            Map<String, Integer> stats = vault.getCategoryStats();
            pw.println("  SUMMARY:");
            pw.println("  Total Documents : " + vault.getTotalDocuments());
            pw.println("  Expiring (30d)  : " + vault.getExpiringCount(30));
            pw.println("  Expired         : " + vault.getExpiredCount());
            pw.println();
            pw.println("  CATEGORY BREAKDOWN:");
            for (Map.Entry<String, Integer> e : stats.entrySet()) {
                pw.println("    " + e.getKey() + " : " + e.getValue());
            }
            pw.println();
            pw.println("-".repeat(60));
            pw.println("  DOCUMENTS:");
            pw.println("-".repeat(60));

            for (Document d : vault.sortByCategory()) {
                pw.println("  [" + d.getDocId() + "] " + d.getName());
                pw.println("    Category  : " + d.getCategory());
                pw.println("    Doc No    : " + d.getDocumentNumber());
                pw.println("    Authority : " + d.getIssuingAuthority());
                pw.println("    Expiry    : " + (d.getExpiryDate() != null ? d.getExpiryDate().format(Document.DATE_FORMAT) : "N/A"));
                pw.println("    Status    : " + (d.isExpired() ? "EXPIRED" : d.isExpiringSoon(90) ? "EXPIRING SOON" : "VALID"));
                pw.println("    Version   : v" + d.getVersion());
                pw.println("    Notes     : " + d.getNotes());
                pw.println();
            }

            UIUtil.printSuccess("Report exported to: " + filename);
            AuditLogger.getInstance().logAction(user.getUsername(), "EXPORT_REPORT", filename);
        } catch (java.io.IOException e) {
            UIUtil.printError("Could not write report: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════
    static Document createDocument(String name, String docNo, String category,
                                    LocalDate issue, LocalDate expiry,
                                    String authority, String notes) {
        switch (category) {
            case "Medical":   return new MedicalDocument(name, docNo, issue, expiry, authority, notes);
            case "Financial": return new FinancialDocument(name, docNo, issue, expiry, authority, notes);
            case "Education": return new EducationDocument(name, docNo, issue, expiry, authority, notes);
            case "Property":  return new PropertyDocument(name, docNo, issue, expiry, authority, notes);
            default:          return new IdentityDocument(name, docNo, issue, expiry, authority, notes);
        }
    }

    static String getCategoryFromChoice(String choice) {
        switch (choice) {
            case "2": return "Medical";
            case "3": return "Financial";
            case "4": return "Education";
            case "5": return "Property";
            default:  return "Identity";
        }
    }

    static LocalDate promptDate(String prompt) {
        UIUtil.printPrompt(prompt);
        String input = sc.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            return LocalDate.parse(input, Document.DATE_FORMAT);
        } catch (DateTimeParseException e) {
            UIUtil.printWarning("Invalid date format. Skipping.");
            return null;
        }
    }
}
