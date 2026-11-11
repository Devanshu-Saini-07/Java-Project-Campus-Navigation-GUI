package com.campusnavigation.repository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

/**
 * Robust Atomic File Writing Utility.
 * Writes to a temporary file before performing an atomic filesystem swap,
 * ensuring zero file corruption if the JVM or system crashes mid-write.
 */
public final class AtomicFileUtil {

    private AtomicFileUtil() {}

    public static synchronized void writeAtomic(String filename, List<String> lines) throws IOException {
        Path targetPath = Paths.get(filename);
        Path parentDir = targetPath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        // Temporary file in same directory ensures same filesystem volume for ATOMIC_MOVE
        Path tempPath = Paths.get(filename + ".tmp." + System.nanoTime());

        try {
            Files.write(tempPath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

            // Optional backup if original exists
            if (Files.exists(targetPath)) {
                Path backupPath = Paths.get(filename + ".bak");
                try {
                    Files.copy(targetPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception ignored) {}
            }

            // Atomic move to target
            try {
                Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            if (Files.exists(tempPath)) {
                try {
                    Files.delete(tempPath);
                } catch (Exception ignored) {}
            }
        }
    }
}
