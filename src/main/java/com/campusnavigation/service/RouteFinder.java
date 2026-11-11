package com.campusnavigation.service;

import java.util.*;
import com.campusnavigation.model.CampusLocation;
import com.campusnavigation.model.CampusEdge;

/**
 * RouteFinder implements Dijkstra's and A* (A-Star) Pathfinding Algorithms on the CampusGraph.
 * Calculates optimal shortest paths, accessibility constraints (wheelchair/ramps),
 * multimodal bus transitions, and generates turn-by-turn compass directions.
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
        private List<String> turnByTurnDirections;

        public RouteResult(List<CampusLocation> path, int totalDistance) {
            this(path, totalDistance, true, Collections.emptyList());
        }

        public RouteResult(List<CampusLocation> path, int totalDistance, boolean isWheelchairAccessible) {
            this(path, totalDistance, isWheelchairAccessible, Collections.emptyList());
        }

        public RouteResult(List<CampusLocation> path, int totalDistance, boolean isWheelchairAccessible, List<String> directions) {
            this.path = path;
            this.totalDistance = totalDistance;
            this.isWheelchairAccessible = isWheelchairAccessible;
            this.estimatedMinutes = totalDistance == 0 ? 0 : Math.max(1, (int) Math.ceil(totalDistance / 70.0));
            this.turnByTurnDirections = directions != null ? directions : Collections.emptyList();
        }

        public List<CampusLocation> getPath() { return path; }
        public int getTotalDistance() { return totalDistance; }
        public int getEstimatedMinutes() { return estimatedMinutes; }
        public boolean isWheelchairAccessible() { return isWheelchairAccessible; }
        public List<String> getTurnByTurnDirections() { return turnByTurnDirections; }

        public String getFormattedRoute() {
            if (path == null || path.isEmpty()) {
                return "No route found between the selected locations.";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("=====================================================\n");
            sb.append(isWheelchairAccessible ? " ACCESSIBLE BARRIER-FREE ROUTE (RAMPS & PAVED)\n" : " SHORTEST CAMPUS ROUTE (A* / DIJKSTRA)\n");
            sb.append("=====================================================\n\n");
            sb.append(" Route:\n ");
            for (int i = 0; i < path.size(); i++) {
                sb.append(path.get(i).getName());
                if (i < path.size() - 1) {
                    sb.append("  ->  ");
                }
            }
            sb.append("\n\n");
            sb.append(" Total Distance:         ").append(totalDistance).append(" meters\n");
            sb.append(" Estimated Walking Time: ").append(estimatedMinutes).append(" minute(s)\n");
            sb.append(" Mobility / Accessibility: ").append(isWheelchairAccessible ? "Barrier-free (Ramps/Paved)" : "Standard (may contain stairs)").append("\n");

            if (turnByTurnDirections != null && !turnByTurnDirections.isEmpty()) {
                sb.append("\n Turn-by-Turn Directions:\n");
                for (int i = 0; i < turnByTurnDirections.size(); i++) {
                    sb.append("  ").append(i + 1).append(". ").append(turnByTurnDirections.get(i)).append("\n");
                }
            } else {
                sb.append("\n Step-by-Step Waypoints:\n");
                for (int i = 0; i < path.size() - 1; i++) {
                    CampusLocation from = path.get(i);
                    CampusLocation to = path.get(i + 1);
                    sb.append("  ").append(i + 1).append(". Walk from ")
                      .append(from.getName()).append(" to ").append(to.getName())
                      .append(" (").append(to.getBuilding()).append(")\n");
                }
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

    public static RouteResult findShortestPath(CampusGraph graph, String startId, String targetId) {
        return findShortestPath(graph, startId, targetId, false);
    }

    /**
     * Finds the shortest path using Dijkstra's algorithm with optional wheelchair accessible filtering.
     */
    public static RouteResult findShortestPath(CampusGraph graph, String startId, String targetId, boolean accessibleOnly) {
        return findShortestPathAStar(graph, startId, targetId, accessibleOnly);
    }

    /**
     * A* (A-Star) Pathfinding Algorithm.
     * Uses Euclidean spatial distance as an admissible heuristic h(n) = sqrt(dx^2 + dy^2) * scale,
     * guiding search directly towards the target and expanding fewer branches than Dijkstra.
     */
    public static RouteResult findShortestPathAStar(CampusGraph graph, String startId, String targetId, boolean accessibleOnly) {
        if (graph == null || startId == null || targetId == null) {
            return null;
        }

        if (startId.equals(targetId)) {
            CampusLocation loc = graph.getLocation(startId);
            if (loc != null) {
                return new RouteResult(Collections.singletonList(loc), 0, true,
                        Collections.singletonList("You are already at " + loc.getName()));
            }
            return null;
        }

        CampusLocation targetLoc = graph.getLocation(targetId);
        if (targetLoc == null) return null;

        Map<String, Integer> gScore = new HashMap<>();
        Map<String, Double> fScore = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Set<String> closedSet = new HashSet<>();

        for (CampusLocation loc : graph.getAllLocations()) {
            gScore.put(loc.getId(), Integer.MAX_VALUE);
            fScore.put(loc.getId(), Double.MAX_VALUE);
        }

        gScore.put(startId, 0);
        double startH = computeHeuristic(graph.getLocation(startId), targetLoc);
        fScore.put(startId, startH);
        openSet.add(new AStarNode(startId, startH));

        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            String currId = current.nodeId;

            if (currId.equals(targetId)) break;

            if (closedSet.contains(currId)) continue;
            closedSet.add(currId);

            for (CampusEdge edge : graph.getNeighbors(currId)) {
                if (accessibleOnly && !edge.isWheelchairAccessible()) {
                    continue; // Skip inaccessible paths (e.g. stairs)
                }

                String neighborId = edge.getDestinationId();
                if (closedSet.contains(neighborId)) continue;

                int tentativeG = gScore.get(currId) + edge.getDistance();
                if (tentativeG < gScore.get(neighborId)) {
                    predecessors.put(neighborId, currId);
                    gScore.put(neighborId, tentativeG);

                    double h = computeHeuristic(graph.getLocation(neighborId), targetLoc);
                    double f = tentativeG + h;
                    fScore.put(neighborId, f);
                    openSet.add(new AStarNode(neighborId, f));
                }
            }
        }

        if (gScore.get(targetId) == null || gScore.get(targetId) == Integer.MAX_VALUE) {
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
                for (CampusEdge e : graph.getNeighbors(prev)) {
                    if (e.getDestinationId().equals(step) && !e.isWheelchairAccessible()) {
                        allAccessible = false;
                        break;
                    }
                }
            }
            step = prev;
        }

        List<String> turnDirections = generateTurnByTurnDirections(path, graph);
        return new RouteResult(path, gScore.get(targetId), allAccessible, turnDirections);
    }

    /**
     * Admissible Euclidean heuristic h(n) scaled so h(n) <= true distance.
     */
    private static double computeHeuristic(CampusLocation from, CampusLocation to) {
        if (from == null || to == null) return 0.0;
        double dx = from.getX() - to.getX();
        double dy = from.getY() - to.getY();
        // Campus scale: 1 coordinate unit is roughly 0.6 to 1.0 meter
        return Math.sqrt(dx * dx + dy * dy) * 0.55;
    }

    /**
     * Computes intuitive turn-by-turn compass directions based on vector angle changes.
     */
    public static List<String> generateTurnByTurnDirections(List<CampusLocation> path, CampusGraph graph) {
        List<String> directions = new ArrayList<>();
        if (path == null || path.size() < 2) {
            if (path != null && path.size() == 1) {
                directions.add("You are at " + path.get(0).getName() + ".");
            }
            return directions;
        }

        for (int i = 0; i < path.size() - 1; i++) {
            CampusLocation cur = path.get(i);
            CampusLocation next = path.get(i + 1);

            int dist = (graph != null) ? graph.getDistance(cur.getId(), next.getId()) : 0;
            String distText = (dist > 0) ? " for " + dist + "m" : "";

            if (i == 0) {
                directions.add("Start at " + cur.getName() + ". Walk straight towards " + next.getName() + distText + ".");
            } else {
                CampusLocation prev = path.get(i - 1);
                double v1x = cur.getX() - prev.getX();
                double v1y = cur.getY() - prev.getY();
                double v2x = next.getX() - cur.getX();
                double v2y = next.getY() - cur.getY();

                double angle1 = Math.atan2(v1y, v1x);
                double angle2 = Math.atan2(v2y, v2x);
                double diff = Math.toDegrees(angle2 - angle1);

                while (diff > 180.0) diff -= 360.0;
                while (diff < -180.0) diff += 360.0;

                String turnVerb;
                if (Math.abs(diff) <= 25.0) {
                    turnVerb = "Continue straight towards";
                } else if (diff > 25.0 && diff <= 65.0) {
                    turnVerb = "Bear right towards";
                } else if (diff > 65.0 && diff <= 120.0) {
                    turnVerb = "Turn right towards";
                } else if (diff > 120.0) {
                    turnVerb = "Make a sharp right towards";
                } else if (diff < -25.0 && diff >= -65.0) {
                    turnVerb = "Bear left towards";
                } else if (diff < -65.0 && diff >= -120.0) {
                    turnVerb = "Turn left towards";
                } else {
                    turnVerb = "Make a sharp left towards";
                }

                directions.add(turnVerb + " " + next.getName() + distText + " (" + next.getBuilding() + ").");
            }
        }

        CampusLocation dest = path.get(path.size() - 1);
        directions.add("Arrive at destination: " + dest.getName() + " (" + dest.getBuilding() + ", Floor " + dest.getFloor() + ").");
        return directions;
    }

    /**
     * Determines whether combining a campus shuttle bus with walking is faster than purely walking.
     */
    public static MultimodalOption findMultimodalRoute(CampusGraph graph, String startId, String targetId) {
        if (graph == null || startId == null || targetId == null || startId.equals(targetId)) {
            return null;
        }

        RouteResult walkDirect = findShortestPath(graph, startId, targetId, false);
        if (walkDirect == null || walkDirect.getTotalDistance() < 250) {
            return null;
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

    private static class AStarNode {
        String nodeId;
        double fScore;

        AStarNode(String nodeId, double fScore) {
            this.nodeId = nodeId;
            this.fScore = fScore;
        }
    }
}
