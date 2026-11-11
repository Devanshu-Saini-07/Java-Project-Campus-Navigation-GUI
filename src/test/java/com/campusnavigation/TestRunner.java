package com.campusnavigation;

import java.util.Objects;

/**
 * Lightweight, zero-dependency standalone test runner.
 * Can be executed via: java -cp bin com.campusnavigation.TestRunner
 */
public class TestRunner {

    private static int totalAssertions = 0;
    private static int passedAssertions = 0;
    private static int failedAssertions = 0;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("===============================================================");
        System.out.println("   CAMPUS NAVIGATION SYSTEM - AUTOMATED UNIT TEST SUITE");
        System.out.println("===============================================================");

        try {
            CampusGraphTest.run();
            RouteFinderTest.run();
            CampusDataManagerTest.run();
            UserAuthTest.run();
        } catch (Throwable t) {
            System.err.println("\n[FATAL EXCEPTION DURING TEST EXECUTION]: " + t.getMessage());
            t.printStackTrace();
            failedAssertions++;
        }

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("\n===============================================================");
        System.out.println("                     TEST EXECUTION SUMMARY");
        System.out.println("===============================================================");
        System.out.println("  Total Assertions Checked : " + totalAssertions);
        System.out.println("  Assertions Passed        : " + passedAssertions);
        System.out.println("  Assertions Failed        : " + failedAssertions);
        System.out.println("  Execution Time           : " + duration + " ms");
        System.out.println("===============================================================");

        if (failedAssertions > 0) {
            System.err.println(">>> RESULT: SOME TESTS FAILED! <<<");
            System.exit(1);
        } else {
            System.out.println(">>> RESULT: ALL TESTS PASSED SUCCESSFULLY (100%) <<<");
        }
    }

    public static void assertEquals(String message, Object expected, Object actual) {
        totalAssertions++;
        if (!Objects.equals(expected, actual)) {
            failedAssertions++;
            System.err.println("  [FAIL] " + message + " - Expected: <" + expected + ">, but got: <" + actual + ">");
        } else {
            passedAssertions++;
        }
    }

    public static void assertEquals(String message, int expected, int actual) {
        assertEquals(message, Integer.valueOf(expected), Integer.valueOf(actual));
    }

    public static void assertTrue(String message, boolean condition) {
        totalAssertions++;
        if (!condition) {
            failedAssertions++;
            System.err.println("  [FAIL] " + message + " - Expected: TRUE, but got: FALSE");
        } else {
            passedAssertions++;
        }
    }

    public static void assertFalse(String message, boolean condition) {
        assertTrue(message, !condition);
    }

    public static void assertNotNull(String message, Object object) {
        totalAssertions++;
        if (object == null) {
            failedAssertions++;
            System.err.println("  [FAIL] " + message + " - Expected: NOT NULL, but got: NULL");
        } else {
            passedAssertions++;
        }
    }

    public static void assertNull(String message, Object object) {
        totalAssertions++;
        if (object != null) {
            failedAssertions++;
            System.err.println("  [FAIL] " + message + " - Expected: NULL, but got: <" + object + ">");
        } else {
            passedAssertions++;
        }
    }

    public static void fail(String message) {
        totalAssertions++;
        failedAssertions++;
        System.err.println("  [FAIL] " + message);
    }
}
