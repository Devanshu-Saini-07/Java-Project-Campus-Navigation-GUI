package com.campusnavigation.repository;

import java.io.*;
import java.util.*;

/**
 * Manages user favorite campus locations.
 * Persists data to 'favorites.txt' with in-memory fallback.
 */
public class FavoriteManager {
    private static final String FAVORITES_FILE = "favorites.txt";
    private Set<String> favoriteIds;

    public FavoriteManager() {
        this.favoriteIds = new LinkedHashSet<>();
        loadFavorites();
    }

    public synchronized void addFavorite(String locationId) {
        if (locationId != null && !locationId.trim().isEmpty()) {
            favoriteIds.add(locationId.trim());
            saveFavorites();
        }
    }

    public synchronized void removeFavorite(String locationId) {
        if (locationId != null) {
            favoriteIds.remove(locationId.trim());
            saveFavorites();
        }
    }

    public synchronized boolean isFavorite(String locationId) {
        return locationId != null && favoriteIds.contains(locationId.trim());
    }

    public synchronized Set<String> getFavoriteIds() {
        return new LinkedHashSet<>(favoriteIds);
    }

    private void loadFavorites() {
        File file = new File(FAVORITES_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    favoriteIds.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not read " + FAVORITES_FILE + ": " + e.getMessage());
        }
    }

    private void saveFavorites() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FAVORITES_FILE))) {
            for (String id : favoriteIds) {
                writer.write(id);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not save to " + FAVORITES_FILE + ": " + e.getMessage());
        }
    }
}
