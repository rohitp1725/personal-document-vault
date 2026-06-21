package models;

public abstract class User {

    protected String username;
    protected String passwordHash;
    protected String fullName;
    protected String bloodGroup;
    protected String emergencyContact;
    protected String role; // ADULT, MINOR, SENIOR
    protected int loginAttempts;
    protected boolean isLocked;
    protected String securityQuestion;
    protected String securityAnswer;

    public User(String username, String passwordHash, String fullName,
                String bloodGroup, String emergencyContact,
                String securityQuestion, String securityAnswer) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.bloodGroup = bloodGroup;
        this.emergencyContact = emergencyContact;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.loginAttempts = 0;
        this.isLocked = false;
    }

    public abstract String getRole();
    public abstract boolean canAccessFamilyVaults();
    public abstract boolean requiresGuardianApproval();

    public void incrementLoginAttempts() {
        loginAttempts++;
        if (loginAttempts >= 3) isLocked = true;
    }
    public void resetLoginAttempts() { loginAttempts = 0; isLocked = false; }

    public String toFileString() {
        return username + "|" + passwordHash + "|" + fullName + "|"
                + bloodGroup + "|" + emergencyContact + "|" + getRole() + "|"
                + (isLocked ? "1" : "0") + "|" + securityQuestion + "|" + securityAnswer;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
    public String getBloodGroup() { return bloodGroup; }
    public String getEmergencyContact() { return emergencyContact; }
    public boolean isLocked() { return isLocked; }
    public String getSecurityQuestion() { return securityQuestion; }
    public String getSecurityAnswer() { return securityAnswer; }
    public void setLocked(boolean locked) { isLocked = locked; }
}
