import java.util.*;

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

        public RouteResult(List<CampusLocation> path, int totalDistance) {
            this.path = path;
            this.totalDistance = totalDistance;
            // Average human walking speed: ~1.2 m/s -> ~72 meters per minute.
            // Minimum 1 minute if distance > 0.
            this.estimatedMinutes = totalDistance == 0 ? 0 : Math.max(1, (int) Math.ceil(totalDistance / 70.0));
        }

        public List<CampusLocation> getPath() { return path; }
        public int getTotalDistance() { return totalDistance; }
        public int getEstimatedMinutes() { return estimatedMinutes; }

        public String getFormattedRoute() {
            if (path == null || path.isEmpty()) {
                return "No route found between the selected locations.";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("=====================================================\n");
            sb.append(" SHORTEST CAMPUS ROUTE\n");
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
        if (graph == null || startId == null || targetId == null) {
            return null;
        }

        if (startId.equals(targetId)) {
            CampusLocation loc = graph.getLocation(startId);
            if (loc != null) {
                return new RouteResult(Collections.singletonList(loc), 0);
            }
            return null;
        }

        // Distance map: node ID -> shortest known distance from start
        Map<String, Integer> distances = new HashMap<>();
        // Predecessor map: node ID -> preceding node ID along the shortest path
        Map<String, String> predecessors = new HashMap<>();
        // PriorityQueue storing entries of (nodeId, distance)
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));

        // Initialize all distances to Infinity
        for (CampusLocation loc : graph.getAllLocations()) {
            distances.put(loc.getId(), Integer.MAX_VALUE);
        }

        // Start node distance is 0
        distances.put(startId, 0);
        pq.add(new NodeDistance(startId, 0));

        Set<String> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            String currId = current.nodeId;

            if (visited.contains(currId)) continue;
            visited.add(currId);

            // Reached destination
            if (currId.equals(targetId)) break;

            // Explore neighbors
            for (CampusEdge edge : graph.getNeighbors(currId)) {
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

        // Check if target is reachable
        if (distances.get(targetId) == null || distances.get(targetId) == Integer.MAX_VALUE) {
            return null; // No path exists
        }

        // Reconstruct path by backtracking from targetId to startId
        LinkedList<CampusLocation> path = new LinkedList<>();
        String step = targetId;
        while (step != null) {
            CampusLocation loc = graph.getLocation(step);
            if (loc != null) {
                path.addFirst(loc);
            }
            step = predecessors.get(step);
        }

        return new RouteResult(path, distances.get(targetId));
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
