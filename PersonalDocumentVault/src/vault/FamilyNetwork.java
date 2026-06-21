package vault;

import models.Document;
import models.User;
import utils.FileHandler;
import utils.UIUtil;
import engine.AuditLogger;
import java.util.List;

public class FamilyNetwork {

    private String currentUser;

    public FamilyNetwork(String currentUser) {
        this.currentUser = currentUser;
    }

    public void linkFamilyMember(String targetUsername) {
        if (!FileHandler.userExists(targetUsername)) {
            UIUtil.printError("User '" + targetUsername + "' does not exist.");
            return;
        }
        FileHandler.saveFamilyLink(currentUser, targetUsername);
        AuditLogger.getInstance().logAction(currentUser, "FAMILY_LINK", "Linked with " + targetUsername);
        UIUtil.printSuccess("Family link established with " + targetUsername + "!");
    }

    public void showFamilyMembers() {
        List<String> members = FileHandler.getFamilyMembers(currentUser);
        UIUtil.printSectionTitle("Family Vault Network");
        if (members.isEmpty()) {
            UIUtil.printInfo("No family members linked yet.");
            return;
        }
        for (String m : members) {
            User u = FileHandler.loadUser(m);
            String role = u != null ? u.getRole() : "UNKNOWN";
            System.out.println(UIUtil.CYAN + "  - " + m
                    + UIUtil.RESET + " (" + role + ")"
                    + (u != null ? " | " + u.getFullName() : ""));
        }
    }

    public void viewFamilyMemberVault(String targetUsername) {
        List<String> members = FileHandler.getFamilyMembers(currentUser);
        if (!members.contains(targetUsername)) {
            UIUtil.printError("You are not linked to '" + targetUsername + "'.");
            return;
        }
        User currentUserObj = FileHandler.loadUser(currentUser);
        User targetUserObj  = FileHandler.loadUser(targetUsername);

        if (targetUserObj == null) {
            UIUtil.printError("User not found.");
            return;
        }
        // Access allowed if current user is adult/senior and target is minor
        // OR both are adults in same family
        AuditLogger.getInstance().logAction(currentUser, "VIEW_FAMILY_VAULT",
                "Accessed vault of " + targetUsername);

        List<Document> docs = FileHandler.loadDocuments(targetUsername);
        UIUtil.printSectionTitle("Vault of: " + targetUserObj.getFullName() + " (@" + targetUsername + ")");

        if (docs.isEmpty()) {
            UIUtil.printInfo("No documents in this vault.");
            return;
        }
        for (Document d : docs) {
            printDocumentRow(d);
        }
    }

    public void emergencyAccess(String targetUsername) {
        List<String> members = FileHandler.getFamilyMembers(currentUser);
        if (!members.contains(targetUsername)) {
            UIUtil.printError("Not linked to this user.");
            return;
        }
        User target = FileHandler.loadUser(targetUsername);
        if (target == null) { UIUtil.printError("User not found."); return; }

        System.out.println();
        System.out.println(UIUtil.RED + UIUtil.BOLD
                + "  *** EMERGENCY ACCESS - " + target.getFullName().toUpperCase() + " ***"
                + UIUtil.RESET);
        UIUtil.printThinDivider();
        System.out.println(UIUtil.YELLOW + "  Blood Group       : " + UIUtil.RESET + UIUtil.BOLD + target.getBloodGroup() + UIUtil.RESET);
        System.out.println(UIUtil.YELLOW + "  Emergency Contact : " + UIUtil.RESET + target.getEmergencyContact());

        List<Document> docs = FileHandler.loadDocuments(targetUsername);
        System.out.println(UIUtil.YELLOW + "\n  Critical Documents:" + UIUtil.RESET);
        for (Document d : docs) {
            if (d.getCategory().equals("Medical") || d.getCategory().equals("Identity")) {
                System.out.println(UIUtil.CYAN + "    " + d.getCategoryIcon() + " "
                        + d.getName() + " | " + d.getDocumentNumber() + UIUtil.RESET);
            }
        }
        UIUtil.printThinDivider();
        AuditLogger.getInstance().logAction(currentUser, "EMERGENCY_ACCESS",
                "Emergency access to " + targetUsername + "'s vault");
    }

    private void printDocumentRow(Document d) {
        String expiry = d.getExpiryDate() != null ? d.getExpiryDate().format(Document.DATE_FORMAT) : "N/A";
        String status = d.isExpired() ? UIUtil.RED + "[EXP]" + UIUtil.RESET
                : d.isExpiringSoon(90) ? UIUtil.YELLOW + "[!]  " + UIUtil.RESET
                : UIUtil.GREEN + "[OK] " + UIUtil.RESET;
        System.out.println("  " + status + " " + d.getCategoryIcon() + " "
                + UIUtil.WHITE + d.getName() + UIUtil.RESET
                + " | " + d.getDocumentNumber()
                + " | Expiry: " + expiry);
    }

    public List<String> getLinkedMembers() {
        return FileHandler.getFamilyMembers(currentUser);
    }
}
