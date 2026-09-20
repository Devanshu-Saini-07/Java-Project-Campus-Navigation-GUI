package com.campusnavigation.model;
/**
 * Represents a campus bus route with bus stop, route details, destination, and nearby landmarks.
 */
public class BusRoute {
    private String stopName;
    private String routeNumber;
    private String destination;
    private String timings;
    private String nearbyLocations;

    public BusRoute(String stopName, String routeNumber, String destination, String timings, String nearbyLocations) {
        this.stopName = stopName.trim();
        this.routeNumber = routeNumber.trim();
        this.destination = destination.trim();
        this.timings = timings.trim();
        this.nearbyLocations = nearbyLocations.trim();
    }

    public String getStopName() { return stopName; }
    public String getRouteNumber() { return routeNumber; }
    public String getDestination() { return destination; }
    public String getTimings() { return timings; }
    public String getNearbyLocations() { return nearbyLocations; }

    public String getFormattedDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------------------\n");
        sb.append(" Stop: ").append(stopName).append(" | Route: ").append(routeNumber).append("\n");
        sb.append(" Destination:      ").append(destination).append("\n");
        sb.append(" Frequency/Timing: ").append(timings).append("\n");
        sb.append(" Nearby Points:    ").append(nearbyLocations).append("\n");
        sb.append("-----------------------------------------------------\n");
        return sb.toString();
    }

    public String toFileString() {
        return stopName + "|" + routeNumber + "|" + destination + "|" + timings + "|" + nearbyLocations;
    }

    public static BusRoute fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 5) {
            return new BusRoute(parts[0], parts[1], parts[2], parts[3], parts[4]);
        }
        return null;
    }
}
