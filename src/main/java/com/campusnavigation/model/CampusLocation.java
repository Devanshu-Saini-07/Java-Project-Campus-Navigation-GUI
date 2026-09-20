package com.campusnavigation.model;
/**
 * Represents a specific location on the campus.
 * Contains metadata such as coordinates for map rendering, timings, and category.
 */
public class CampusLocation {
    private String id;
    private String name;
    private String category;      // e.g., Building, Department, Hostel, Lab, Bus Stop, Facility
    private String building;      // Associated building
    private String floor;         // Floor number/details (if applicable)
    private String description;   // Detailed description
    private String nearby;        // Nearby landmark locations
    private String timings;       // Operating hours
    private int x;                // Map X coordinate on the Canvas
    private int y;                // Map Y coordinate on the Canvas
    private String aliases;       // Comma-separated search aliases (e.g., "lib,library")

    public CampusLocation(String id, String name, String category, String building,
                          String floor, String description, String nearby,
                          String timings, int x, int y, String aliases) {
        this.id = id.trim();
        this.name = name.trim();
        this.category = category.trim();
        this.building = building.trim();
        this.floor = floor.trim();
        this.description = description.trim();
        this.nearby = nearby.trim();
        this.timings = timings.trim();
        this.x = x;
        this.y = y;
        this.aliases = aliases.trim();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNearby() { return nearby; }
    public void setNearby(String nearby) { this.nearby = nearby; }

    public String getTimings() { return timings; }
    public void setTimings(String timings) { this.timings = timings; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public String getAliases() { return aliases; }
    public void setAliases(String aliases) { this.aliases = aliases; }

    /**
     * Checks if a search keyword matches this location's name, category, or aliases.
     */
    public boolean matches(String query) {
        if (query == null || query.trim().isEmpty()) return false;
        String q = query.trim().toLowerCase();

        if (name.toLowerCase().contains(q)) return true;
        if (category.toLowerCase().contains(q)) return true;
        if (building.toLowerCase().contains(q)) return true;

        String[] aliasList = aliases.split(",");
        for (String alias : aliasList) {
            if (alias.trim().equalsIgnoreCase(q) || alias.trim().toLowerCase().contains(q)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Formats location details for display in the main information panel.
     */
    public String getDetailsFormatted() {
        StringBuilder sb = new StringBuilder();
        sb.append("=====================================================\n");
        sb.append(" LOCATION DETAILS: ").append(name.toUpperCase()).append("\n");
        sb.append("=====================================================\n\n");
        sb.append("  • Name:           ").append(name).append("\n");
        sb.append("  • Category:       ").append(category).append("\n");
        sb.append("  • Building:       ").append(building).append("\n");
        if (floor != null && !floor.isEmpty() && !floor.equalsIgnoreCase("N/A")) {
            sb.append("  • Floor:          ").append(floor).append("\n");
        }
        sb.append("  • Timings:        ").append(timings).append("\n");
        sb.append("  • Description:    ").append(description).append("\n");
        sb.append("  • Nearby Places:  ").append(nearby).append("\n");
        sb.append("  • Coordinates:    (").append(x).append(", ").append(y).append(")\n");
        sb.append("\n=====================================================");
        return sb.toString();
    }

    /**
     * Converts the location object into a pipe-delimited string for file storage.
     */
    public String toFileString() {
        return id + "|" + name + "|" + category + "|" + building + "|" +
               floor + "|" + description + "|" + nearby + "|" + timings + "|" +
               x + "|" + y + "|" + aliases;
    }

    /**
     * Creates a CampusLocation from a pipe-delimited file string.
     */
    public static CampusLocation fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 11) {
            int x = 0, y = 0;
            try {
                x = Integer.parseInt(parts[8].trim());
                y = Integer.parseInt(parts[9].trim());
            } catch (NumberFormatException ignored) {}
            return new CampusLocation(parts[0], parts[1], parts[2], parts[3],
                                      parts[4], parts[5], parts[6], parts[7],
                                      x, y, parts[10]);
        }
        return null;
    }

    @Override
    public String toString() {
        return name + " (" + category + ")";
    }
}
