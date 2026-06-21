package models;

public class AdultUser extends User {
    public AdultUser(String username, String passwordHash, String fullName,
                     String bloodGroup, String emergencyContact,
                     String securityQuestion, String securityAnswer) {
        super(username, passwordHash, fullName, bloodGroup, emergencyContact, securityQuestion, securityAnswer);
    }
    @Override public String getRole() { return "ADULT"; }
    @Override public boolean canAccessFamilyVaults() { return true; }
    @Override public boolean requiresGuardianApproval() { return false; }
}
