package com.campusnavigation;

import com.campusnavigation.model.CampusLocation;
import com.campusnavigation.repository.AtomicFileUtil;
import com.campusnavigation.repository.CampusDataManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class CampusDataManagerTest {

    public static void run() {
        System.out.println("\n--- Running CampusDataManagerTest ---");
        testSearchQueries();
        testAtomicFileWrite();
    }

    private static void testSearchQueries() {
        CampusDataManager cdm = new CampusDataManager();

        // Exact match
        List<CampusLocation> results = cdm.searchLocations("Library");
        TestRunner.assertTrue("Search for Library should return matches", !results.isEmpty());
        TestRunner.assertEquals("Match should be Library", "Library", results.get(0).getName());

        // Case insensitivity
        List<CampusLocation> lowerCaseResults = cdm.searchLocations("library");
        TestRunner.assertTrue("Lowercase search should return matches", !lowerCaseResults.isEmpty());

        // Alias resolution: 'lib' -> library
        List<CampusLocation> aliasResults = cdm.searchLocations("lib");
        TestRunner.assertTrue("Alias 'lib' should match Library", !aliasResults.isEmpty());

        // Alias resolution: 'admin' -> Admin Block
        List<CampusLocation> adminResults = cdm.searchLocations("admin");
        TestRunner.assertTrue("Alias 'admin' should match Admin Block", !adminResults.isEmpty());

        // Non-existent search
        List<CampusLocation> noResults = cdm.searchLocations("NonExistentLocationXYZ");
        TestRunner.assertTrue("Non-existent location returns empty list", noResults.isEmpty());

        System.out.println("  [PASS] testSearchQueries");
    }

    private static void testAtomicFileWrite() {
        File testFile = new File("test_atomic_output.txt");
        if (testFile.exists()) {
            testFile.delete();
        }

        List<String> lines = Arrays.asList("Header Line", "Record 1|Data A|Data B", "Record 2|Data C|Data D");
        boolean success = false;
        try {
            AtomicFileUtil.writeAtomic(testFile.getPath(), lines);
            success = true;
        } catch (IOException e) {
            TestRunner.fail("Exception during atomic write: " + e.getMessage());
        }

        TestRunner.assertTrue("Atomic write should report true", success);
        TestRunner.assertTrue("Target file should exist on disk", testFile.exists());

        try {
            List<String> readBack = Files.readAllLines(testFile.toPath());
            TestRunner.assertEquals("Line count should match", 3, readBack.size());
            TestRunner.assertEquals("First line match", "Header Line", readBack.get(0));
            TestRunner.assertEquals("Second line match", "Record 1|Data A|Data B", readBack.get(1));
            TestRunner.assertEquals("Third line match", "Record 2|Data C|Data D", readBack.get(2));
        } catch (IOException e) {
            TestRunner.fail("IOException reading written atomic file: " + e.getMessage());
        } finally {
            if (testFile.exists()) {
                testFile.delete();
            }
            File bakFile = new File("test_atomic_output.txt.bak");
            if (bakFile.exists()) {
                bakFile.delete();
            }
        }

        System.out.println("  [PASS] testAtomicFileWrite");
    }
}
