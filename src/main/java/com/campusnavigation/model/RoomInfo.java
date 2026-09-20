package com.campusnavigation.model;
/**
 * Represents room details on campus (e.g., Room 101, CSE-201, LAB-01).
 */
public class RoomInfo {
    private String roomNumber;
    private String building;
    private String floor;
    private String departmentOrLab;
    private String description;

    public RoomInfo(String roomNumber, String building, String floor,
                    String departmentOrLab, String description) {
        this.roomNumber = roomNumber.trim();
        this.building = building.trim();
        this.floor = floor.trim();
        this.departmentOrLab = departmentOrLab.trim();
        this.description = description.trim();
    }

    public String getRoomNumber() { return roomNumber; }
    public String getBuilding() { return building; }
    public String getFloor() { return floor; }
    public String getDepartmentOrLab() { return departmentOrLab; }
    public String getDescription() { return description; }

    public boolean matches(String query) {
        if (query == null || query.trim().isEmpty()) return false;
        String q = query.trim().toLowerCase();
        return roomNumber.toLowerCase().contains(q) ||
               building.toLowerCase().contains(q) ||
               departmentOrLab.toLowerCase().contains(q);
    }

    public String getFormattedDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("=====================================================\n");
        sb.append(" ROOM FINDER: ").append(roomNumber.toUpperCase()).append("\n");
        sb.append("=====================================================\n\n");
        sb.append("  • Room Number:     ").append(roomNumber).append("\n");
        sb.append("  • Building:        ").append(building).append("\n");
        sb.append("  • Floor:           ").append(floor).append("\n");
        sb.append("  • Dept / Unit:     ").append(departmentOrLab).append("\n");
        sb.append("  • Details:         ").append(description).append("\n");
        sb.append("\n=====================================================");
        return sb.toString();
    }

    public String toFileString() {
        return roomNumber + "|" + building + "|" + floor + "|" + departmentOrLab + "|" + description;
    }

    public static RoomInfo fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 5) {
            return new RoomInfo(parts[0], parts[1], parts[2], parts[3], parts[4]);
        }
        return null;
    }
}
