package com.campusnavigation.repository;

import java.io.*;
import java.util.*;

/**
 * Manages the search history.
 * Stores the last 5-10 recent search queries in 'history.txt'.
 */
public class SearchHistory {
    private static final String HISTORY_FILE = "history.txt";
    private static final int MAX_HISTORY = 10;
    private LinkedList<String> historyList;

    public SearchHistory() {
        this.historyList = new LinkedList<>();
        loadHistory();
    }

    public synchronized void addSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;
        String cleanQuery = query.trim();

        historyList.remove(cleanQuery);
        historyList.addFirst(cleanQuery);

        while (historyList.size() > MAX_HISTORY) {
            historyList.removeLast();
        }

        saveHistory();
    }

    public synchronized List<String> getHistory() {
        return new ArrayList<>(historyList);
    }

    public synchronized void clearHistory() {
        historyList.clear();
        saveHistory();
    }

    private void loadHistory() {
        File file = new File(HISTORY_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    historyList.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not read " + HISTORY_FILE + ": " + e.getMessage());
        }
    }

    private void saveHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            for (String query : historyList) {
                writer.write(query);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not save to " + HISTORY_FILE + ": " + e.getMessage());
        }
    }
}
