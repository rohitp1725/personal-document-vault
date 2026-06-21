package models;

public class MinorUser extends User {
    public MinorUser(String username, String passwordHash, String fullName,
                     String bloodGroup, String emergencyContact,
                     String securityQuestion, String securityAnswer) {
        super(username, passwordHash, fullName, bloodGroup, emergencyContact, securityQuestion, securityAnswer);
    }
    @Override public String getRole() { return "MINOR"; }
    @Override public boolean canAccessFamilyVaults() { return false; }
    @Override public boolean requiresGuardianApproval() { return true; }
}
