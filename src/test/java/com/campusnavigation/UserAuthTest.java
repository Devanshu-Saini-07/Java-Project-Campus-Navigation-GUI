package com.campusnavigation;

import com.campusnavigation.model.User;
import com.campusnavigation.model.UserRole;
import com.campusnavigation.repository.UserManager;

public class UserAuthTest {

    public static void run() {
        System.out.println("\n--- Running UserAuthTest ---");
        testDefaultAccountsExist();
        testSuccessfulAuthentication();
        testFailedAuthentication();
        testPasswordHashingAndSalt();
        testRolePermissions();
    }

    private static void testDefaultAccountsExist() {
        UserManager userManager = new UserManager();
        TestRunner.assertNotNull("Admin user exists", userManager.getUser("admin"));
        TestRunner.assertNotNull("Student user exists", userManager.getUser("student"));
        TestRunner.assertNotNull("Faculty user exists", userManager.getUser("faculty"));

        TestRunner.assertEquals("Admin role match", UserRole.ROLE_ADMIN, userManager.getUser("admin").getRole());
        TestRunner.assertEquals("Student role match", UserRole.ROLE_STUDENT, userManager.getUser("student").getRole());
        TestRunner.assertEquals("Faculty role match", UserRole.ROLE_FACULTY, userManager.getUser("faculty").getRole());
        System.out.println("  [PASS] testDefaultAccountsExist");
    }

    private static void testSuccessfulAuthentication() {
        UserManager userManager = new UserManager();

        User admin = userManager.authenticate("admin", "123");
        TestRunner.assertNotNull("Admin authentication with valid password", admin);
        TestRunner.assertEquals("Admin username", "admin", admin.getUsername());

        User student = userManager.authenticate("student", "student123");
        TestRunner.assertNotNull("Student authentication with valid password", student);
        TestRunner.assertEquals("Student username", "student", student.getUsername());

        User faculty = userManager.authenticate("faculty", "faculty123");
        TestRunner.assertNotNull("Faculty authentication with valid password", faculty);
        TestRunner.assertEquals("Faculty username", "faculty", faculty.getUsername());

        System.out.println("  [PASS] testSuccessfulAuthentication");
    }

    private static void testFailedAuthentication() {
        UserManager userManager = new UserManager();

        User wrongPass = userManager.authenticate("admin", "wrong_password");
        TestRunner.assertNull("Authentication with incorrect password must fail", wrongPass);

        User unknownUser = userManager.authenticate("ghost_user", "password");
        TestRunner.assertNull("Authentication with non-existent user must fail", unknownUser);

        User emptyUser = userManager.authenticate("", "");
        TestRunner.assertNull("Authentication with empty credentials must fail", emptyUser);

        System.out.println("  [PASS] testFailedAuthentication");
    }

    private static void testPasswordHashingAndSalt() {
        UserManager userManager = new UserManager();

        String salt1 = "11223344556677889900aabbccddeeff";
        String salt2 = "ffeeddccbbaa00998877665544332211";
        String plainPassword = "SecretPassword123";

        String hash1 = userManager.hashPassword(plainPassword, salt1);
        String hash2 = userManager.hashPassword(plainPassword, salt2);

        TestRunner.assertNotNull("Hash 1 should not be null", hash1);
        TestRunner.assertNotNull("Hash 2 should not be null", hash2);
        TestRunner.assertEquals("SHA-256 hash length must be 64 characters", 64, hash1.length());
        TestRunner.assertTrue("Different salts must produce distinct hashes", !hash1.equals(hash2));

        String hash1Repeat = userManager.hashPassword(plainPassword, salt1);
        TestRunner.assertEquals("Hashing with same password and salt must be deterministic", hash1, hash1Repeat);

        System.out.println("  [PASS] testPasswordHashingAndSalt");
    }

    private static void testRolePermissions() {
        TestRunner.assertTrue("Admin can access Admin Panel", UserRole.ROLE_ADMIN.canAccessAdminPanel());
        TestRunner.assertTrue("Admin can manage locations", UserRole.ROLE_ADMIN.canManageLocations());

        TestRunner.assertFalse("Student cannot access Admin Panel", UserRole.ROLE_STUDENT.canAccessAdminPanel());
        TestRunner.assertFalse("Student cannot manage locations", UserRole.ROLE_STUDENT.canManageLocations());

        TestRunner.assertFalse("Faculty cannot access Admin Panel", UserRole.ROLE_FACULTY.canAccessAdminPanel());
        TestRunner.assertFalse("Faculty cannot manage locations", UserRole.ROLE_FACULTY.canManageLocations());

        System.out.println("  [PASS] testRolePermissions");
    }
}
