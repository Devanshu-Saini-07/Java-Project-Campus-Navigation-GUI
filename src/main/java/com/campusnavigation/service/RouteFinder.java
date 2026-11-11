package com.campusnavigation.service;

import java.util.*;
import com.campusnavigation.model.CampusLocation;
import com.campusnavigation.model.CampusEdge;

/**
 * RouteFinder implements Dijkstra's Algorithm on the CampusGraph.
 * Calculates the shortest path, total distance in meters, and estimated walking time.
 * Designed clearly for college-level Data Structures & Algorithms learning.
 */
public class RouteFinder {

    /**
     * Helper class to encapsulate the routing result.
     */
    public static class RouteResult {
        private List<CampusLocation> path;
        private int totalDistance;      // in meters
        private int estimatedMinutes;   // estimated walking time
        private boolean isWheelchairAccessible;

        public RouteResult(List<CampusLocation> path, int totalDistance) {
            this(path, totalDistance, true);
        }

        public RouteResult(List<CampusLocation> path, int totalDistance, boolean isWheelchairAccessible) {
            this.path = path;
            this.totalDistance = totalDistance;
            this.isWheelchairAccessible = isWheelchairAccessible;
            this.estimatedMinutes = totalDistance == 0 ? 0 : Math.max(1, (int) Math.ceil(totalDistance / 70.0));
        }

        public List<CampusLocation> getPath() { return path; }
        public int getTotalDistance() { return totalDistance; }
        public int getEstimatedMinutes() { return estimatedMinutes; }
        public boolean isWheelchairAccessible() { return isWheelchairAccessible; }

        public String getFormattedRoute() {
            if (path == null || path.isEmpty()) {
                return "No route found between the selected locations.";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("=====================================================\n");
            sb.append(isWheelchairAccessible ? " ♿ ACCESSIBLE WHEELCHAIR ROUTE (RAMPS & PAVED)\n" : " SHORTEST CAMPUS ROUTE\n");
            sb.append("=====================================================\n\n");
            sb.append(" Route:\n ");
            for (int i = 0; i < path.size(); i++) {
                sb.append(path.get(i).getName());
                if (i < path.size() - 1) {
                    sb.append("  ➔  ");
                }
            }
            sb.append("\n\n");
            sb.append(" Total Distance:         ").append(totalDistance).append(" meters\n");
            sb.append(" Estimated Walking Time: ").append(estimatedMinutes).append(" minute(s)\n");
            sb.append(" Mobility / Accessibility: ").append(isWheelchairAccessible ? "♿ 100% Barrier-free (Ramps/Paved)" : "Standard (may contain stairs)").append("\n");
            sb.append("\n Step-by-Step Directions:\n");
            for (int i = 0; i < path.size() - 1; i++) {
                CampusLocation from = path.get(i);
                CampusLocation to = path.get(i + 1);
                sb.append("  ").append(i + 1).append(". Walk from ")
                  .append(from.getName()).append(" to ").append(to.getName())
                  .append(" (").append(to.getBuilding()).append(")\n");
            }
            sb.append("=====================================================");
            return sb.toString();
        }
    }

    /**
     * Encapsulates a multimodal route combining walking and campus bus shuttle.
     */
    public static class MultimodalOption {
        private boolean isAvailable;
        private String shuttleName;
        private CampusLocation boardStop;
        private CampusLocation disembarkStop;
        private int walkMeters;
        private int savedWalkMeters;
        private int totalMinutes;
        private String summaryText;

        public MultimodalOption(boolean isAvailable, String shuttleName, CampusLocation boardStop,
                                CampusLocation disembarkStop, int walkMeters, int savedWalkMeters,
                                int totalMinutes, String summaryText) {
            this.isAvailable = isAvailable;
            this.shuttleName = shuttleName;
            this.boardStop = boardStop;
            this.disembarkStop = disembarkStop;
            this.walkMeters = walkMeters;
            this.savedWalkMeters = savedWalkMeters;
            this.totalMinutes = totalMinutes;
            this.summaryText = summaryText;
        }

        public boolean isAvailable() { return isAvailable; }
        public String getShuttleName() { return shuttleName; }
        public CampusLocation getBoardStop() { return boardStop; }
        public CampusLocation getDisembarkStop() { return disembarkStop; }
        public int getWalkMeters() { return walkMeters; }
        public int getSavedWalkMeters() { return savedWalkMeters; }
        public int getTotalMinutes() { return totalMinutes; }
        public String getSummaryText() { return summaryText; }
    }

    /**
     * Finds the shortest path between startLocationId and targetLocationId using Dijkstra's Algorithm.
     *
     * Algorithm Steps:
     * 1. Initialize distance table: set distance to infinity for all nodes except startNode (0).
     * 2. Use a PriorityQueue (min-heap) to greedily explore the node with the smallest known distance.
     * 3. For the current node, relax all outgoing edges to adjacent neighbors.
     * 4. If a shorter path to a neighbor is discovered, update its distance and set current node as its predecessor.
     * 5. Backtrack from target to start using the predecessor map to reconstruct the path.
     */
    public static RouteResult findShortestPath(CampusGraph graph, String startId, String targetId) {
        return findShortestPath(graph, startId, targetId, false);
    }

    /**
     * Finds the shortest path using Dijkstra's algorithm with optional wheelchair accessible filtering.
     * When accessibleOnly is true, non-accessible edges (e.g. stairs) are skipped.
     */
    public static RouteResult findShortestPath(CampusGraph graph, String startId, String targetId, boolean accessibleOnly) {
        if (graph == null || startId == null || targetId == null) {
            return null;
        }

        if (startId.equals(targetId)) {
            CampusLocation loc = graph.getLocation(startId);
            if (loc != null) {
                return new RouteResult(Collections.singletonList(loc), 0, true);
            }
            return null;
        }

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));

        for (CampusLocation loc : graph.getAllLocations()) {
            distances.put(loc.getId(), Integer.MAX_VALUE);
        }

        distances.put(startId, 0);
        pq.add(new NodeDistance(startId, 0));

        Set<String> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            String currId = current.nodeId;

            if (visited.contains(currId)) continue;
            visited.add(currId);

            if (currId.equals(targetId)) break;

            for (CampusEdge edge : graph.getNeighbors(currId)) {
                if (accessibleOnly && !edge.isWheelchairAccessible()) {
                    continue; // Skip inaccessible paths (e.g. stairs)
                }

                String neighborId = edge.getDestinationId();
                if (visited.contains(neighborId)) continue;

                int newDist = distances.get(currId) + edge.getDistance();
                if (newDist < distances.get(neighborId)) {
                    distances.put(neighborId, newDist);
                    predecessors.put(neighborId, currId);
                    pq.add(new NodeDistance(neighborId, newDist));
                }
            }
        }

        if (distances.get(targetId) == null || distances.get(targetId) == Integer.MAX_VALUE) {
            return null; // No path found
        }

        LinkedList<CampusLocation> path = new LinkedList<>();
        String step = targetId;
        boolean allAccessible = true;
        while (step != null) {
            CampusLocation loc = graph.getLocation(step);
            if (loc != null) {
                path.addFirst(loc);
            }
            String prev = predecessors.get(step);
            if (prev != null) {
                // Check if the edge was accessible
                for (CampusEdge e : graph.getNeighbors(prev)) {
                    if (e.getDestinationId().equals(step) && !e.isWheelchairAccessible()) {
                        allAccessible = false;
                        break;
                    }
                }
            }
            step = prev;
        }

        return new RouteResult(path, distances.get(targetId), allAccessible);
    }

    /**
     * Determines whether combining a campus shuttle bus with walking is faster than purely walking.
     * Evaluates stops: LOC21 (Main Bus Stop), LOC22 (Library Bus Stop), LOC23 (Hostel Bus Stop).
     */
    public static MultimodalOption findMultimodalRoute(CampusGraph graph, String startId, String targetId) {
        if (graph == null || startId == null || targetId == null || startId.equals(targetId)) {
            return null;
        }

        RouteResult walkDirect = findShortestPath(graph, startId, targetId, false);
        if (walkDirect == null || walkDirect.getTotalDistance() < 250) {
            return null; // For short distances, walking is already fastest
        }

        String[] busStops = {"LOC21", "LOC22", "LOC23"};
        CampusLocation bestBoard = null;
        CampusLocation bestDisembark = null;
        int minTotalWalk = Integer.MAX_VALUE;

        for (String b1 : busStops) {
            RouteResult toStop = findShortestPath(graph, startId, b1, false);
            if (toStop == null || toStop.getTotalDistance() > 180) continue;

            for (String b2 : busStops) {
                if (b1.equals(b2)) continue;
                RouteResult fromStop = findShortestPath(graph, b2, targetId, false);
                if (fromStop == null || fromStop.getTotalDistance() > 180) continue;

                int combinedWalk = toStop.getTotalDistance() + fromStop.getTotalDistance();
                if (combinedWalk < minTotalWalk) {
                    minTotalWalk = combinedWalk;
                    bestBoard = graph.getLocation(b1);
                    bestDisembark = graph.getLocation(b2);
                }
            }
        }

        if (bestBoard != null && bestDisembark != null && minTotalWalk < walkDirect.getTotalDistance() - 100) {
            int savedMeters = walkDirect.getTotalDistance() - minTotalWalk;
            String shuttleName = (bestBoard.getId().equals("LOC21") || bestDisembark.getId().equals("LOC23"))
                                 ? "Campus Shuttle B" : "Campus Shuttle A";
            int busRideMinutes = 3;
            int walkMinutes = (int) Math.ceil(minTotalWalk / 70.0);
            int totalMinutes = busRideMinutes + walkMinutes;

            String summary = "Take " + shuttleName + " from " + bestBoard.getName() + " to " +
                             bestDisembark.getName() + " (saves " + savedMeters + "m of walking!)";

            return new MultimodalOption(true, shuttleName, bestBoard, bestDisembark,
                                        minTotalWalk, savedMeters, totalMinutes, summary);
        }

        return null;
    }

    /**
     * Helper class representing a node and its tentative distance in the PriorityQueue.
     */
    private static class NodeDistance {
        String nodeId;
        int distance;

        NodeDistance(String nodeId, int distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
    }
}
