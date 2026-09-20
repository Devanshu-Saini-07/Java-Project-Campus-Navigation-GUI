package com.campusnavigation.model;
/**
 * Represents a walkable path/edge between two campus locations in the graph.
 */
public class CampusEdge {
    private String sourceId;
    private String destinationId;
    private int distance; // Distance in meters

    public CampusEdge(String sourceId, String destinationId, int distance) {
        this.sourceId = sourceId.trim();
        this.destinationId = destinationId.trim();
        this.distance = distance;
    }

    public String getSourceId() { return sourceId; }
    public String getDestinationId() { return destinationId; }
    public int getDistance() { return distance; }

    public String toFileString() {
        return sourceId + "|" + destinationId + "|" + distance;
    }

    public static CampusEdge fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 3) {
            try {
                int dist = Integer.parseInt(parts[2].trim());
                return new CampusEdge(parts[0], parts[1], dist);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}
