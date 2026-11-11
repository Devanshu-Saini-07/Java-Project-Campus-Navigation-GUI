package com.campusnavigation.model;
/**
 * Represents a walkable path/edge between two campus locations in the graph.
 */
public class CampusEdge {
    private String sourceId;
    private String destinationId;
    private int distance; // Distance in meters
    private boolean isWheelchairAccessible;
    private String pathType; // "paved", "ramp", "stairs"

    public CampusEdge(String sourceId, String destinationId, int distance) {
        this(sourceId, destinationId, distance, true, "paved");
    }

    public CampusEdge(String sourceId, String destinationId, int distance, boolean isWheelchairAccessible, String pathType) {
        this.sourceId = sourceId.trim();
        this.destinationId = destinationId.trim();
        this.distance = distance;
        this.isWheelchairAccessible = isWheelchairAccessible;
        this.pathType = (pathType != null) ? pathType.trim() : "paved";
    }

    public String getSourceId() { return sourceId; }
    public String getDestinationId() { return destinationId; }
    public int getDistance() { return distance; }
    public boolean isWheelchairAccessible() { return isWheelchairAccessible; }
    public String getPathType() { return pathType; }

    public String toFileString() {
        return sourceId + "|" + destinationId + "|" + distance + "|" + (isWheelchairAccessible ? "true" : "false") + "|" + pathType;
    }

    public static CampusEdge fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 3) {
            try {
                int dist = Integer.parseInt(parts[2].trim());
                boolean accessible = true;
                String type = "paved";
                if (parts.length >= 4) {
                    accessible = !"false".equalsIgnoreCase(parts[3].trim()) && !"stairs".equalsIgnoreCase(parts[3].trim());
                }
                if (parts.length >= 5) {
                    type = parts[4].trim();
                } else if (parts.length >= 4 && "stairs".equalsIgnoreCase(parts[3].trim())) {
                    type = "stairs";
                    accessible = false;
                }
                return new CampusEdge(parts[0], parts[1], dist, accessible, type);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}
