package com.campusnavigation.repository;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import com.campusnavigation.model.User;
import com.campusnavigation.model.UserRole;

/**
 * Manages user accounts, secure authentication (SHA-256 + salt),
 * and role-based permissions.
 */
public class UserManager {

    private static final String USERS_FILE = "users.txt";
    private final Map<String, User> userMap = new HashMap<>();
    private final SecureRandom random = new SecureRandom();

    public UserManager() {
        initializeUsers();
    }

    public synchronized void initializeUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            createDefaultUsers();
        } else {
            loadUsers();
            if (userMap.isEmpty()) {
                createDefaultUsers();
            }
        }
    }

    private void createDefaultUsers() {
        userMap.clear();
        addUserWithPlainPassword("admin", "123", UserRole.ROLE_ADMIN, "System Administrator");
        addUserWithPlainPassword("student", "student123", UserRole.ROLE_STUDENT, "Student / Campus Guest");
        addUserWithPlainPassword("faculty", "faculty123", UserRole.ROLE_FACULTY, "Prof. Academic Faculty");
        saveUsers();
    }

    private void loadUsers() {
        userMap.clear();
        File file = new File(USERS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    User u = User.fromFileString(line);
                    if (u != null) {
                        userMap.put(u.getUsername(), u);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading " + USERS_FILE + ": " + e.getMessage());
        }
    }

    public synchronized void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User u : userMap.values()) {
                writer.write(u.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving " + USERS_FILE + ": " + e.getMessage());
        }
    }

    public synchronized User authenticate(String username, String plainPassword) {
        if (username == null || plainPassword == null) return null;
        String key = username.trim().toLowerCase();
        User user = userMap.get(key);
        if (user == null) return null;

        String hashAttempt = hashPassword(plainPassword, user.getSalt());
        if (user.getPasswordHash().equals(hashAttempt)) {
            return user;
        }
        return null;
    }

    public synchronized boolean addUserWithPlainPassword(String username, String plainPassword, UserRole role, String displayName) {
        if (username == null || plainPassword == null || username.trim().isEmpty()) return false;
        String key = username.trim().toLowerCase();
        String salt = generateSalt();
        String hash = hashPassword(plainPassword, salt);
        User newUser = new User(key, hash, salt, role, displayName);
        userMap.put(key, newUser);
        return true;
    }

    public synchronized User getUser(String username) {
        if (username == null) return null;
        return userMap.get(username.trim().toLowerCase());
    }

    public synchronized Collection<User> getAllUsers() {
        return Collections.unmodifiableCollection(userMap.values());
    }

    public String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes("UTF-8"));
            byte[] bytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException("SHA-256 algorithm not supported", e);
        }
    }

    private String generateSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        StringBuilder sb = new StringBuilder();
        for (byte b : salt) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
