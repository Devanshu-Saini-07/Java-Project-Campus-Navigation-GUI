package com.campusnavigation;

import com.campusnavigation.model.CampusLocation;
import com.campusnavigation.service.CampusGraph;
import com.campusnavigation.service.RouteFinder;
import com.campusnavigation.service.RouteFinder.RouteResult;

import java.util.List;

public class RouteFinderTest {

    public static void run() {
        System.out.println("\n--- Running RouteFinderTest ---");
        testShortestPathAStar();
        testWheelchairAccessibleRoute();
        testUnreachableNodeHandling();
        testTurnByTurnDirectionsGeneration();
    }

    private static CampusGraph createSampleGraph() {
        CampusGraph graph = new CampusGraph();

        // Node A (0, 0), Node B (100, 0), Node C (200, 0), Node D (100, 100)
        CampusLocation a = new CampusLocation("A", "Gate", "Entry", "Gate", "Ground", "24/7", "", 0, 0);
        CampusLocation b = new CampusLocation("B", "Plaza", "Building", "Plaza", "Ground", "24/7", "", 100, 0);
        CampusLocation c = new CampusLocation("C", "Library", "Building", "Library", "Ground", "9-5", "", 200, 0);
        CampusLocation d = new CampusLocation("D", "Hilltop Overlook", "Building", "Hill", "Ground", "9-5", "", 100, 100);
        CampusLocation isolated = new CampusLocation("ISO", "Remote Cabin", "Building", "Outpost", "Ground", "9-5", "", 900, 900);

        graph.addLocation(a);
        graph.addLocation(b);
        graph.addLocation(c);
        graph.addLocation(d);
        graph.addLocation(isolated);

        // Path A -> B (100m, accessible)
        graph.addEdge("A", "B", 100, true, "paved");
        // Path B -> C (100m, accessible)
        graph.addEdge("B", "C", 100, true, "paved");
        // Path A -> D (80m, inaccessible - steep stairs)
        graph.addEdge("A", "D", 80, false, "stairs");
        // Path D -> C (80m, inaccessible - steep stairs)
        graph.addEdge("D", "C", 80, false, "stairs");

        return graph;
    }

    private static void testShortestPathAStar() {
        CampusGraph graph = createSampleGraph();

        // Standard shortest path: A -> D -> C is 160m (vs A -> B -> C which is 200m)
        RouteResult result = RouteFinder.findShortestPathAStar(graph, "A", "C", false);

        TestRunner.assertNotNull("Route result should not be null", result);
        TestRunner.assertEquals("Total distance should be 160m via stairs D", 160, result.getTotalDistance());
        TestRunner.assertEquals("Path length", 3, result.getPath().size());
        TestRunner.assertEquals("Start node", "Gate", result.getPath().get(0).getName());
        TestRunner.assertEquals("Mid node", "Hilltop Overlook", result.getPath().get(1).getName());
        TestRunner.assertEquals("End node", "Library", result.getPath().get(2).getName());
        System.out.println("  [PASS] testShortestPathAStar");
    }

    private static void testWheelchairAccessibleRoute() {
        CampusGraph graph = createSampleGraph();

        // Accessible only: A -> D -> C is disallowed because of stairs. Must take A -> B -> C (200m)
        RouteResult result = RouteFinder.findShortestPathAStar(graph, "A", "C", true);

        TestRunner.assertNotNull("Accessible route result should not be null", result);
        TestRunner.assertEquals("Total accessible distance should be 200m via B", 200, result.getTotalDistance());
        TestRunner.assertEquals("Path length", 3, result.getPath().size());
        TestRunner.assertEquals("Start node", "Gate", result.getPath().get(0).getName());
        TestRunner.assertEquals("Mid node", "Plaza", result.getPath().get(1).getName());
        TestRunner.assertEquals("End node", "Library", result.getPath().get(2).getName());
        TestRunner.assertTrue("Result should be marked accessible", result.isWheelchairAccessible());
        System.out.println("  [PASS] testWheelchairAccessibleRoute");
    }

    private static void testUnreachableNodeHandling() {
        CampusGraph graph = createSampleGraph();

        // ISO is disconnected
        RouteResult result = RouteFinder.findShortestPathAStar(graph, "A", "ISO", false);
        TestRunner.assertNull("Unreachable target should return null", result);
        System.out.println("  [PASS] testUnreachableNodeHandling");
    }

    private static void testTurnByTurnDirectionsGeneration() {
        CampusGraph graph = createSampleGraph();

        RouteResult result = RouteFinder.findShortestPathAStar(graph, "A", "C", true);
        TestRunner.assertNotNull("Route exists", result);

        List<String> directions = result.getTurnByTurnDirections();
        TestRunner.assertNotNull("Turn-by-turn directions list should not be null", directions);
        TestRunner.assertTrue("Turn directions should have at least 2 instructions", directions.size() >= 2);

        String firstStep = directions.get(0);
        TestRunner.assertTrue("First instruction should mention start point", firstStep.contains("Start at Gate"));

        String lastStep = directions.get(directions.size() - 1);
        TestRunner.assertTrue("Final instruction should mention arriving at destination", lastStep.contains("Arrive at destination: Library"));
        System.out.println("  [PASS] testTurnByTurnDirectionsGeneration");
    }
}
