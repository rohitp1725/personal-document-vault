package vault;

import exceptions.*;
import models.*;
import utils.*;
import engine.AuditLogger;
import java.time.LocalDate;
import java.util.Scanner;

public class VaultManager {

    private Scanner sc;
    private User currentUser;
    private Vault currentVault;

    public VaultManager(Scanner sc) {
        this.sc = sc;
    }

    // ─── Login ──────────────────────────────────────────────
    public boolean login() throws VaultLockedException, AuthenticationFailedException {
        UIUtil.printSectionTitle("Login to Your Vault");
        UIUtil.printPrompt("Username");
        String username = sc.nextLine().trim();

        if (!FileHandler.userExists(username)) {
            UIUtil.printError("No vault found for '" + username + "'. Please sign up.");
            return false;
        }

        User user = FileHandler.loadUser(username);
        if (user.isLocked()) throw new VaultLockedException(username);

        UIUtil.printPrompt("Password");
        String password = sc.nextLine().trim();

        if (!EncryptionUtil.verifyPassword(password, user.getPasswordHash())) {
            user.incrementLoginAttempts();
            FileHandler.saveUser(user);
            if (user.isLocked()) throw new VaultLockedException(username);
            int left = 3 - (user.isLocked() ? 3 : 0);
            throw new AuthenticationFailedException(Math.max(0, 3 - (int) countAttempts(user)));
        }

        user.resetLoginAttempts();
        FileHandler.saveUser(user);
        this.currentUser = user;
        this.currentVault = new Vault(username);
        AuditLogger.getInstance().logAction(username, "LOGIN", "Successful login");
        return true;
    }

    private int countAttempts(User user) {
        // Rough estimate for display
        return 1;
    }

    // ─── Signup ─────────────────────────────────────────────
    public boolean signup() {
        UIUtil.printSectionTitle("Create New Vault");

        UIUtil.printPrompt("Choose a username");
        String username = sc.nextLine().trim();
        if (username.isEmpty() || username.contains("|")) {
            UIUtil.printError("Invalid username."); return false;
        }
        if (FileHandler.userExists(username)) {
            UIUtil.printError("Username already taken."); return false;
        }

        UIUtil.printPrompt("Full Name");
        String fullName = sc.nextLine().trim();

        UIUtil.printPrompt("Password (min 4 chars)");
        String password = sc.nextLine().trim();
        if (password.length() < 4) { UIUtil.printError("Password too short."); return false; }

        System.out.println(UIUtil.CYAN + "\n  Select Role:" + UIUtil.RESET);
        System.out.println("  [1] Adult   [2] Minor (under 18)   [3] Senior");
        UIUtil.printPrompt("Role");
        String roleChoice = sc.nextLine().trim();

        UIUtil.printPrompt("Blood Group (e.g. A+, O-)");
        String blood = sc.nextLine().trim();

        UIUtil.printPrompt("Emergency Contact (name & phone)");
        String emergency = sc.nextLine().trim();

        UIUtil.printPrompt("Security Question (e.g. 'Mother's maiden name?')");
        String secQ = sc.nextLine().trim();

        UIUtil.printPrompt("Answer");
        String secA = sc.nextLine().trim();

        String hash = EncryptionUtil.hashPassword(password);
        User user;
        switch (roleChoice) {
            case "2": user = new MinorUser(username, hash, fullName, blood, emergency, secQ, secA); break;
            case "3": user = new SeniorUser(username, hash, fullName, blood, emergency, secQ, secA); break;
            default:  user = new AdultUser(username, hash, fullName, blood, emergency, secQ, secA);
        }

        FileHandler.saveUser(user);
        AuditLogger.getInstance().logAction(username, "SIGNUP", "New vault created | Role: " + user.getRole());
        UIUtil.printSuccess("Vault created successfully! Please log in.");
        return true;
    }

    // ─── Password Recovery ──────────────────────────────────
    public void recoverVault() {
        UIUtil.printSectionTitle("Vault Recovery");
        UIUtil.printPrompt("Username");
        String username = sc.nextLine().trim();

        User user = FileHandler.loadUser(username);
        if (user == null) { UIUtil.printError("User not found."); return; }

        System.out.println(UIUtil.CYAN + "  Security Question: " + user.getSecurityQuestion() + UIUtil.RESET);
        UIUtil.printPrompt("Your Answer");
        String answer = sc.nextLine().trim();

        if (!answer.equalsIgnoreCase(user.getSecurityAnswer())) {
            UIUtil.printError("Wrong answer. Recovery failed.");
            AuditLogger.getInstance().logAction(username, "RECOVERY_FAIL", "Wrong security answer");
            return;
        }

        UIUtil.printPrompt("New Password");
        String newPass = sc.nextLine().trim();
        if (newPass.length() < 4) { UIUtil.printError("Password too short."); return; }

        // Recreate user with new password — preserve all fields
        User updated;
        switch (user.getRole()) {
            case "MINOR":  updated = new MinorUser(username,  EncryptionUtil.hashPassword(newPass), user.getFullName(), user.getBloodGroup(), user.getEmergencyContact(), user.getSecurityQuestion(), user.getSecurityAnswer()); break;
            case "SENIOR": updated = new SeniorUser(username, EncryptionUtil.hashPassword(newPass), user.getFullName(), user.getBloodGroup(), user.getEmergencyContact(), user.getSecurityQuestion(), user.getSecurityAnswer()); break;
            default:       updated = new AdultUser(username,  EncryptionUtil.hashPassword(newPass), user.getFullName(), user.getBloodGroup(), user.getEmergencyContact(), user.getSecurityQuestion(), user.getSecurityAnswer());
        }
        FileHandler.saveUser(updated);
        UIUtil.printSuccess("Password reset! Vault unlocked. Please log in.");
        AuditLogger.getInstance().logAction(username, "RECOVERY_SUCCESS", "Vault unlocked via security question");
    }

    public User getCurrentUser() { return currentUser; }
    public Vault getCurrentVault() { return currentVault; }

    public void logout() {
        if (currentVault != null) {
            currentVault.backup();
            UIUtil.printSuccess("Backup created. Goodbye, " + currentUser.getFullName() + "!");
            AuditLogger.getInstance().logAction(currentUser.getUsername(), "LOGOUT", "Session ended");
        }
        currentUser = null;
        currentVault = null;
    }
}
