package models;

public class SeniorUser extends User {
    public SeniorUser(String username, String passwordHash, String fullName,
                      String bloodGroup, String emergencyContact,
                      String securityQuestion, String securityAnswer) {
        super(username, passwordHash, fullName, bloodGroup, emergencyContact, securityQuestion, securityAnswer);
    }
    @Override public String getRole() { return "SENIOR"; }
    @Override public boolean canAccessFamilyVaults() { return true; }
    @Override public boolean requiresGuardianApproval() { return false; }
}
