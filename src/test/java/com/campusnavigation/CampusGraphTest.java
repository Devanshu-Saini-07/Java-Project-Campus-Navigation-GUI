package com.campusnavigation;

import com.campusnavigation.model.CampusEdge;
import com.campusnavigation.model.CampusLocation;
import com.campusnavigation.service.CampusGraph;

import java.util.List;

public class CampusGraphTest {

    public static void run() {
        System.out.println("\n--- Running CampusGraphTest ---");
        testAddLocationAndRetrieve();
        testAddEdgeBidirectional();
        testEdgeAccessibilityAndPathType();
        testClearGraph();
    }

    private static void testAddLocationAndRetrieve() {
        CampusGraph graph = new CampusGraph();
        CampusLocation loc = new CampusLocation("T01", "Test Building", "Building", "Building A", "Ground", "9-5", "Test notes", 100, 200);
        graph.addLocation(loc);

        TestRunner.assertNotNull("Location should be retrieved by ID", graph.getLocation("T01"));
        TestRunner.assertEquals("Location name match", "Test Building", graph.getLocation("T01").getName());
        TestRunner.assertNotNull("Location should be retrieved by Name", graph.getLocationByName("Test Building"));
        TestRunner.assertNotNull("Case-insensitive lookup by Name", graph.getLocationByName("test building"));
        System.out.println("  [PASS] testAddLocationAndRetrieve");
    }

    private static void testAddEdgeBidirectional() {
        CampusGraph graph = new CampusGraph();
        CampusLocation loc1 = new CampusLocation("T01", "Node 1", "Building", "B1", "Ground", "9-5", "", 10, 10);
        CampusLocation loc2 = new CampusLocation("T02", "Node 2", "Building", "B2", "Ground", "9-5", "", 50, 50);
        graph.addLocation(loc1);
        graph.addLocation(loc2);

        graph.addEdge("T01", "T02", 75);

        List<CampusEdge> n1 = graph.getNeighbors("T01");
        List<CampusEdge> n2 = graph.getNeighbors("T02");

        TestRunner.assertEquals("Node 1 neighbors count", 1, n1.size());
        TestRunner.assertEquals("Node 2 neighbors count", 1, n2.size());
        TestRunner.assertEquals("Node 1 -> Node 2 distance", 75, n1.get(0).getDistance());
        TestRunner.assertEquals("Node 2 -> Node 1 distance", 75, n2.get(0).getDistance());
        System.out.println("  [PASS] testAddEdgeBidirectional");
    }

    private static void testEdgeAccessibilityAndPathType() {
        CampusGraph graph = new CampusGraph();
        CampusLocation loc1 = new CampusLocation("T01", "Node 1", "Building", "B1", "Ground", "9-5", "", 10, 10);
        CampusLocation loc2 = new CampusLocation("T02", "Node 2", "Building", "B2", "Ground", "9-5", "", 50, 50);
        graph.addLocation(loc1);
        graph.addLocation(loc2);

        graph.addEdge("T01", "T02", 40, false, "stairs");

        List<CampusEdge> edges = graph.getNeighbors("T01");
        TestRunner.assertEquals("Edge count", 1, edges.size());
        TestRunner.assertFalse("Wheelchair accessibility should be false", edges.get(0).isWheelchairAccessible());
        TestRunner.assertEquals("Path type should be stairs", "stairs", edges.get(0).getPathType());
        System.out.println("  [PASS] testEdgeAccessibilityAndPathType");
    }

    private static void testClearGraph() {
        CampusGraph graph = new CampusGraph();
        CampusLocation loc1 = new CampusLocation("T01", "Node 1", "Building", "B1", "Ground", "9-5", "", 10, 10);
        graph.addLocation(loc1);
        TestRunner.assertEquals("Graph location count before clear", 1, graph.getAllLocations().size());

        graph.clear();
        TestRunner.assertEquals("Graph location count after clear", 0, graph.getAllLocations().size());
        TestRunner.assertNull("Lookup after clear returns null", graph.getLocation("T01"));
        System.out.println("  [PASS] testClearGraph");
    }
}
