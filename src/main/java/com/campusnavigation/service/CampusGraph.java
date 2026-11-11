package com.campusnavigation.service;

import java.util.*;
import com.campusnavigation.model.CampusLocation;
import com.campusnavigation.model.CampusEdge;

/**
 * Represents the Campus Map as a Graph data structure.
 * Nodes (Vertices) are Campus Locations, and Edges are walkable paths with distance weights.
 * Uses an Adjacency List representation.
 */
public class CampusGraph {
    private Map<String, CampusLocation> locationMap;
    private Map<String, List<CampusEdge>> adjacencyList;

    public CampusGraph() {
        this.locationMap = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }

    /**
     * Adds a location vertex to the graph.
     */
    public void addLocation(CampusLocation location) {
        if (location == null) return;
        locationMap.put(location.getId(), location);
        adjacencyList.putIfAbsent(location.getId(), new ArrayList<>());
    }

    /**
     * Adds an undirected edge between two campus locations.
     */
    public void addEdge(String srcId, String destId, int distance) {
        addEdge(srcId, destId, distance, true, "paved");
    }

    public void addEdge(String srcId, String destId, int distance, boolean isAccessible, String pathType) {
        if (!locationMap.containsKey(srcId) || !locationMap.containsKey(destId)) {
            return;
        }
        adjacencyList.get(srcId).add(new CampusEdge(srcId, destId, distance, isAccessible, pathType));
        adjacencyList.get(destId).add(new CampusEdge(destId, srcId, distance, isAccessible, pathType));
    }

    /**
     * Clears all graph data.
     */
    public void clear() {
        locationMap.clear();
        adjacencyList.clear();
    }

    public CampusLocation getLocation(String id) {
        return locationMap.get(id);
    }

    public CampusLocation getLocationByName(String name) {
        for (CampusLocation loc : locationMap.values()) {
            if (loc.getName().equalsIgnoreCase(name.trim())) {
                return loc;
            }
        }
        return null;
    }

    public List<CampusEdge> getNeighbors(String id) {
        return adjacencyList.getOrDefault(id, Collections.emptyList());
    }

    public int getDistance(String srcId, String destId) {
        List<CampusEdge> edges = adjacencyList.get(srcId);
        if (edges != null) {
            for (CampusEdge edge : edges) {
                if (edge.getDestinationId().equals(destId)) {
                    return edge.getDistance();
                }
            }
        }
        return 0;
    }

    public Collection<CampusLocation> getAllLocations() {
        return locationMap.values();
    }

    public List<CampusEdge> getAllEdges() {
        List<CampusEdge> allEdges = new ArrayList<>();
        Set<String> visitedEdges = new HashSet<>();
        for (List<CampusEdge> edges : adjacencyList.values()) {
            for (CampusEdge edge : edges) {
                String key1 = edge.getSourceId() + "->" + edge.getDestinationId();
                String key2 = edge.getDestinationId() + "->" + edge.getSourceId();
                if (!visitedEdges.contains(key1) && !visitedEdges.contains(key2)) {
                    allEdges.add(edge);
                    visitedEdges.add(key1);
                    visitedEdges.add(key2);
                }
            }
        }
        return allEdges;
    }
}
