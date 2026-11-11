package com.campusnavigation.model;

public class User {
    private String username;
    private String passwordHash;
    private String salt;
    private UserRole role;
    private String displayName;

    public User(String username, String passwordHash, String salt, UserRole role, String displayName) {
        this.username = username.trim().toLowerCase();
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role != null ? role : UserRole.ROLE_STUDENT;
        this.displayName = displayName != null ? displayName.trim() : username;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }
    public UserRole getRole() { return role; }
    public String getDisplayName() { return displayName; }

    public String toFileString() {
        return username + "|" + passwordHash + "|" + salt + "|" + role.name() + "|" + displayName;
    }

    public static User fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 5) {
            try {
                UserRole r = UserRole.valueOf(parts[3].trim());
                return new User(parts[0], parts[1], parts[2], r, parts[4]);
            } catch (Exception e) {
                return new User(parts[0], parts[1], parts[2], UserRole.ROLE_STUDENT, parts[4]);
            }
        }
        return null;
    }
}
