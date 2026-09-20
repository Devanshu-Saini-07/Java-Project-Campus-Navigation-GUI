package com.campusnavigation.repository;

import java.io.*;
import java.util.*;
import com.campusnavigation.model.CampusLocation;
import com.campusnavigation.model.CampusEdge;
import com.campusnavigation.model.RoomInfo;
import com.campusnavigation.model.BusRoute;
import com.campusnavigation.service.CampusGraph;

/**
 * Manages all campus data loading, saving, search indexing, and graph construction.
 * Reads and writes from local text files with automatic creation of default data.
 */
public class CampusDataManager {
    private static final String DATA_FILE = "campus_data.txt";
    private static final String EDGES_FILE = "campus_edges.txt";
    private static final String ROOMS_FILE = "campus_rooms.txt";
    private static final String BUS_FILE = "bus_routes.txt";

    private Map<String, CampusLocation> locations;
    private List<CampusEdge> edges;
    private List<RoomInfo> rooms;
    private List<BusRoute> busRoutes;
    private CampusGraph graph;

    public CampusDataManager() {
        this.locations = new LinkedHashMap<>();
        this.edges = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.busRoutes = new ArrayList<>();
        this.graph = new CampusGraph();

        initializeData();
    }

    /**
     * Initializes all data collections from files, creating defaults if files are missing.
     */
    public void initializeData() {
        ensureDataFilesExist();
        loadLocations();
        loadEdges();
        loadRooms();
        loadBusRoutes();
        buildGraph();
    }

    private void ensureDataFilesExist() {
        File dataFile = new File(DATA_FILE);
        if (!dataFile.exists()) {
            createDefaultLocationsFile();
        }
        File edgesFile = new File(EDGES_FILE);
        if (!edgesFile.exists()) {
            createDefaultEdgesFile();
        }
        File roomsFile = new File(ROOMS_FILE);
        if (!roomsFile.exists()) {
            createDefaultRoomsFile();
        }
        File busFile = new File(BUS_FILE);
        if (!busFile.exists()) {
            createDefaultBusFile();
        }
    }

    private void createDefaultLocationsFile() {
        List<CampusLocation> defaults = new ArrayList<>();
        defaults.add(new CampusLocation("LOC01", "Main Gate", "Entry/Exit", "Security Post", "Ground",
                "Primary entry and exit portal of the university campus with 24/7 security.", "Admin Block, Main Bus Stop", "24/7", 80, 310, "gate,main gate,entry,exit"));
        defaults.add(new CampusLocation("LOC02", "Admin Block", "Building", "Admin Block", "Ground - 3rd",
                "Administrative headquarters housing Vice Chancellor Office, Admissions, Registrar, and Accounts.", "Main Gate, Academic Block A", "9:00 AM - 5:00 PM", 120, 200, "admin,admin block,administration,office"));
        defaults.add(new CampusLocation("LOC03", "Academic Block A", "Building", "Academic Block A", "Ground - 4th",
                "Houses Computer Science, IT, and Electronics Departments, Faculty cabins, and seminar halls.", "Admin Block, Library, Academic Block B", "8:30 AM - 6:00 PM", 260, 200, "block a,academic a,acad a,cse building"));
        defaults.add(new CampusLocation("LOC04", "Academic Block B", "Building", "Academic Block B", "Ground - 4th",
                "Houses Mechanical and Civil Engineering Departments, drawing halls, and robotics labs.", "Academic Block A, Sports Complex", "8:30 AM - 6:00 PM", 390, 200, "block b,academic b,acad b,mech building"));
        defaults.add(new CampusLocation("LOC05", "Library", "Building", "Central Library", "Ground - 2nd",
                "Three-floor central library with over 50,000 books, digital archives, and quiet study rooms.", "Academic Block A, AI Lab", "8:00 AM - 8:00 PM", 260, 100, "lib,library,central library,books,study room"));
        defaults.add(new CampusLocation("LOC06", "Auditorium", "Facility", "Auditorium Hall", "Ground",
                "Grand university auditorium with 1,200 seating capacity for fests, seminars, and convocations.", "Admin Block, Library", "9:00 AM - 7:00 PM", 130, 100, "audi,auditorium,hall,events"));
        defaults.add(new CampusLocation("LOC07", "Computer Science", "Department", "Academic Block A", "2nd Floor",
                "Department of Computer Science and Engineering with advanced research labs and faculty offices.", "Programming Lab, AI Lab", "8:30 AM - 5:30 PM", 260, 170, "cse,cs,computer science,computer science department"));
        defaults.add(new CampusLocation("LOC08", "Information Technology", "Department", "Academic Block A", "3rd Floor",
                "Department of Information Technology specializing in Cloud Computing, Cybersecurity, and Software Engineering.", "Networking Lab, CSE Dept", "8:30 AM - 5:30 PM", 260, 230, "it,info tech,information technology"));
        defaults.add(new CampusLocation("LOC09", "Mechanical Engineering", "Department", "Academic Block B", "1st Floor",
                "Department of Mechanical Engineering with workshops, thermodynamics, and CAD/CAM labs.", "Civil Dept, Workshop", "8:30 AM - 5:30 PM", 390, 170, "mech,mechanical,mechanical engineering"));
        defaults.add(new CampusLocation("LOC10", "Civil Engineering", "Department", "Academic Block B", "2nd Floor",
                "Department of Civil Engineering with structural analysis and surveying labs.", "Mechanical Dept", "8:30 AM - 5:30 PM", 390, 230, "civil,civil engineering,structures"));
        defaults.add(new CampusLocation("LOC11", "Electronics", "Department", "Academic Block A", "1st Floor",
                "Department of Electronics and Communication Engineering with VLSI and Embedded Systems labs.", "Electronics Lab", "8:30 AM - 5:30 PM", 220, 200, "ece,electronics,ece department"));
        defaults.add(new CampusLocation("LOC12", "Programming Lab", "Laboratory", "Academic Block A", "Room 201 (2nd Floor)",
                "Equipped with 100+ high-end workstations for Java, Python, C++, and Data Structures practicals.", "CSE Dept, AI Lab", "9:00 AM - 5:00 PM", 290, 170, "prog lab,programming lab,coding lab,lab 1"));
        defaults.add(new CampusLocation("LOC13", "Networking Lab", "Laboratory", "Academic Block A", "Room 301 (3rd Floor)",
                "Cisco certified networking lab with routers, switches, and network simulation testbeds.", "IT Dept", "9:00 AM - 5:00 PM", 290, 230, "net lab,networking lab,network lab,lab 2"));
        defaults.add(new CampusLocation("LOC14", "AI Lab", "Laboratory", "Academic Block A", "Room 205 (2nd Floor)",
                "State-of-the-art lab with GPU clusters for Machine Learning, Deep Learning, and Robotics research.", "CSE Dept, Library", "9:00 AM - 5:00 PM", 330, 100, "ai,ai lab,machine learning lab,ml lab"));
        defaults.add(new CampusLocation("LOC15", "Electronics Lab", "Laboratory", "Academic Block A", "Room 105 (1st Floor)",
                "Digital circuits, microprocessors, and communication hardware testing facility.", "ECE Dept", "9:00 AM - 5:00 PM", 190, 200, "ec lab,electronics lab,hardware lab"));
        defaults.add(new CampusLocation("LOC16", "Boys Hostel A", "Hostel", "Hostel Complex", "Ground - 4th",
                "Residential facility for 1st and 2nd year male undergraduate students with mess and recreation hall.", "Boys Hostel B, Hostel Bus Stop", "24/7 (Curfew 9:30 PM)", 500, 90, "hostel,boys hostel,boys hostel a,bha"));
        defaults.add(new CampusLocation("LOC17", "Boys Hostel B", "Hostel", "Hostel Complex", "Ground - 4th",
                "Residential facility for senior male students and postgraduate scholars.", "Boys Hostel A, Sports Complex", "24/7 (Curfew 9:30 PM)", 500, 140, "boys hostel b,bhb"));
        defaults.add(new CampusLocation("LOC18", "Girls Hostel", "Hostel", "Hostel Complex", "Ground - 4th",
                "Dedicated residential hostel for female students with 24-hour warden and biometric access.", "Guest House, Sports Complex", "24/7 (Curfew 8:30 PM)", 500, 190, "girls hostel,gh,ladies hostel"));
        defaults.add(new CampusLocation("LOC19", "Guest House", "Hostel", "Guest House", "Ground - 1st",
                "Accommodation for visiting professors, VIP guests, and parents with dining facility.", "Girls Hostel", "24/7", 500, 240, "guest house,visiting faculty"));
        defaults.add(new CampusLocation("LOC20", "Sports Complex", "Facility", "Sports Complex", "Ground",
                "Includes Football Ground, Cricket Pitch, Basketball Court, Gymnasium, and Indoor Badminton Courts.", "Academic Block B, Hostels", "6:00 AM - 8:00 PM", 420, 310, "sports,sports complex,gym,ground,stadium"));
        defaults.add(new CampusLocation("LOC21", "Main Bus Stop", "Bus Stop", "Main Gate", "Ground",
                "Bus pickup and drop point connecting to Railway Station, Downtown, and Airport routes.", "Main Gate", "6:30 AM - 9:00 PM", 80, 260, "bus,bus stop,main bus stop,city bus"));
        defaults.add(new CampusLocation("LOC22", "Library Bus Stop", "Bus Stop", "Near Library", "Ground",
                "Transit point serving Central Library, Academic Block A, and Seminar Halls.", "Library, Academic Block A", "7:00 AM - 8:00 PM", 210, 100, "library stop,library bus stop"));
        defaults.add(new CampusLocation("LOC23", "Hostel Bus Stop", "Bus Stop", "Near Hostels", "Ground",
                "Transit shelter serving Boys & Girls Hostel Complex and Sports Complex.", "Boys Hostel A, Sports Complex", "7:00 AM - 8:30 PM", 500, 280, "hostel stop,hostel bus stop"));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (CampusLocation loc : defaults) {
                writer.write(loc.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error creating " + DATA_FILE + ": " + e.getMessage());
        }
    }

    private void createDefaultEdgesFile() {
        List<CampusEdge> defaultEdges = new ArrayList<>();
        defaultEdges.add(new CampusEdge("LOC01", "LOC21", 50));
        defaultEdges.add(new CampusEdge("LOC01", "LOC02", 120));
        defaultEdges.add(new CampusEdge("LOC21", "LOC02", 90));
        defaultEdges.add(new CampusEdge("LOC02", "LOC06", 110));
        defaultEdges.add(new CampusEdge("LOC02", "LOC03", 150));
        defaultEdges.add(new CampusEdge("LOC06", "LOC22", 100));
        defaultEdges.add(new CampusEdge("LOC22", "LOC05", 60));
        defaultEdges.add(new CampusEdge("LOC05", "LOC03", 110));
        defaultEdges.add(new CampusEdge("LOC05", "LOC14", 80));
        defaultEdges.add(new CampusEdge("LOC03", "LOC07", 30));
        defaultEdges.add(new CampusEdge("LOC03", "LOC08", 40));
        defaultEdges.add(new CampusEdge("LOC03", "LOC11", 30));
        defaultEdges.add(new CampusEdge("LOC07", "LOC12", 20));
        defaultEdges.add(new CampusEdge("LOC07", "LOC14", 50));
        defaultEdges.add(new CampusEdge("LOC08", "LOC13", 25));
        defaultEdges.add(new CampusEdge("LOC11", "LOC15", 25));
        defaultEdges.add(new CampusEdge("LOC03", "LOC04", 140));
        defaultEdges.add(new CampusEdge("LOC04", "LOC09", 30));
        defaultEdges.add(new CampusEdge("LOC04", "LOC10", 30));
        defaultEdges.add(new CampusEdge("LOC04", "LOC20", 130));
        defaultEdges.add(new CampusEdge("LOC01", "LOC20", 350));
        defaultEdges.add(new CampusEdge("LOC20", "LOC23", 100));
        defaultEdges.add(new CampusEdge("LOC23", "LOC19", 60));
        defaultEdges.add(new CampusEdge("LOC19", "LOC18", 60));
        defaultEdges.add(new CampusEdge("LOC18", "LOC17", 70));
        defaultEdges.add(new CampusEdge("LOC17", "LOC16", 60));
        defaultEdges.add(new CampusEdge("LOC05", "LOC16", 240));
        defaultEdges.add(new CampusEdge("LOC04", "LOC18", 180));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EDGES_FILE))) {
            for (CampusEdge edge : defaultEdges) {
                writer.write(edge.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error creating " + EDGES_FILE + ": " + e.getMessage());
        }
    }

    private void createDefaultRoomsFile() {
        List<RoomInfo> defaultRooms = new ArrayList<>();
        defaultRooms.add(new RoomInfo("101", "Academic Block A", "1st Floor", "First Year Cell", "Smart Classroom with projector for 1st-year lectures."));
        defaultRooms.add(new RoomInfo("102", "Academic Block A", "1st Floor", "Electronics Department", "Basic Electronics Lecture Hall."));
        defaultRooms.add(new RoomInfo("105", "Academic Block A", "1st Floor", "Electronics Lab", "Hardware & Microcontroller testing lab."));
        defaultRooms.add(new RoomInfo("201", "Academic Block A", "2nd Floor", "Programming Lab", "100 i7 PCs configured for Java, C++, Python."));
        defaultRooms.add(new RoomInfo("204", "Academic Block A", "2nd Floor", "Computer Science", "CSE Department Staff Room & HOD Office."));
        defaultRooms.add(new RoomInfo("205", "Academic Block A", "2nd Floor", "AI Lab", "High performance GPU Lab for AI & Deep Learning."));
        defaultRooms.add(new RoomInfo("CSE-201", "Academic Block A", "2nd Floor", "Computer Science", "CSE Theory Classroom with interactive smart board."));
        defaultRooms.add(new RoomInfo("CSE-204", "Academic Block A", "2nd Floor", "Computer Science", "CSE Final Year Project Demonstration Lab."));
        defaultRooms.add(new RoomInfo("301", "Academic Block A", "3rd Floor", "Networking Lab", "Network Security, Router configuration Lab."));
        defaultRooms.add(new RoomInfo("304", "Academic Block A", "3rd Floor", "Information Technology", "IT Department HOD Office & Seminar Hall."));
        defaultRooms.add(new RoomInfo("LAB-01", "Academic Block A", "2nd Floor", "Programming Lab", "Central Coding & Competitive Programming Lab."));
        defaultRooms.add(new RoomInfo("LAB-02", "Academic Block A", "3rd Floor", "Networking Lab", "Cybersecurity & Network Systems Lab."));
        defaultRooms.add(new RoomInfo("MECH-101", "Academic Block B", "1st Floor", "Mechanical Engineering", "Thermodynamics & Fluid Mechanics Hall."));
        defaultRooms.add(new RoomInfo("CIVIL-201", "Academic Block B", "2nd Floor", "Civil Engineering", "Surveying & Structural Engineering Design Lab."));
        defaultRooms.add(new RoomInfo("ADM-01", "Admin Block", "Ground Floor", "Accounts Section", "Fee payment, scholarships, student accounting counter."));
        defaultRooms.add(new RoomInfo("ADM-02", "Admin Block", "1st Floor", "Registrar Office", "Student registration, marksheet, and degree verification."));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ROOMS_FILE))) {
            for (RoomInfo r : defaultRooms) {
                writer.write(r.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error creating " + ROOMS_FILE + ": " + e.getMessage());
        }
    }

    private void createDefaultBusFile() {
        List<BusRoute> defaultBuses = new ArrayList<>();
        defaultBuses.add(new BusRoute("Main Gate Bus Stop", "Route 101", "City Railway Station", "Every 20 mins (7:00 AM - 9:00 PM)", "Main Gate, Admin Block, Security Office"));
        defaultBuses.add(new BusRoute("Main Gate Bus Stop", "Route 102", "Downtown / Central Bus Stand", "Every 30 mins (7:30 AM - 8:30 PM)", "Main Gate, Admin Block"));
        defaultBuses.add(new BusRoute("Main Gate Bus Stop", "Route 105", "International Airport", "Hourly (6:00 AM - 8:00 PM)", "Main Gate, Security Office"));
        defaultBuses.add(new BusRoute("Library Bus Stop", "Campus Shuttle A", "Hostel Complex & Sports Center", "Every 15 mins (8:00 AM - 8:00 PM)", "Central Library, Academic Block A, Auditorium"));
        defaultBuses.add(new BusRoute("Hostel Bus Stop", "Campus Shuttle B", "Academic Complex & Main Gate", "Every 15 mins (7:00 AM - 9:00 PM)", "Boys Hostel A & B, Girls Hostel, Sports Complex"));
        defaultBuses.add(new BusRoute("Hostel Bus Stop", "Weekend Special", "Metro Station & Shopping Mall", "Weekends Every 1 hr (9:00 AM - 7:00 PM)", "Hostel Complex, Guest House"));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BUS_FILE))) {
            for (BusRoute b : defaultBuses) {
                writer.write(b.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error creating " + BUS_FILE + ": " + e.getMessage());
        }
    }

    private void loadLocations() {
        locations.clear();
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    CampusLocation loc = CampusLocation.fromFileString(line);
                    if (loc != null) {
                        locations.put(loc.getId(), loc);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading " + DATA_FILE + ": " + e.getMessage());
        }
    }

    public void saveLocations() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (CampusLocation loc : locations.values()) {
                writer.write(loc.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving to " + DATA_FILE + ": " + e.getMessage());
        }
    }

    private void loadEdges() {
        edges.clear();
        File file = new File(EDGES_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    CampusEdge edge = CampusEdge.fromFileString(line);
                    if (edge != null) {
                        edges.add(edge);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading " + EDGES_FILE + ": " + e.getMessage());
        }
    }

    private void loadRooms() {
        rooms.clear();
        File file = new File(ROOMS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    RoomInfo room = RoomInfo.fromFileString(line);
                    if (room != null) {
                        rooms.add(room);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading " + ROOMS_FILE + ": " + e.getMessage());
        }
    }

    private void loadBusRoutes() {
        busRoutes.clear();
        File file = new File(BUS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    BusRoute b = BusRoute.fromFileString(line);
                    if (b != null) {
                        busRoutes.add(b);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading " + BUS_FILE + ": " + e.getMessage());
        }
    }

    public void buildGraph() {
        graph.clear();
        for (CampusLocation loc : locations.values()) {
            graph.addLocation(loc);
        }
        for (CampusEdge edge : edges) {
            graph.addEdge(edge.getSourceId(), edge.getDestinationId(), edge.getDistance());
        }
    }

    public synchronized boolean addLocation(CampusLocation loc) {
        if (loc == null || loc.getId().isEmpty() || locations.containsKey(loc.getId())) {
            return false;
        }
        locations.put(loc.getId(), loc);
        saveLocations();
        buildGraph();
        return true;
    }

    public synchronized boolean updateLocation(CampusLocation loc) {
        if (loc == null || !locations.containsKey(loc.getId())) {
            return false;
        }
        locations.put(loc.getId(), loc);
        saveLocations();
        buildGraph();
        return true;
    }

    public synchronized boolean deleteLocation(String id) {
        if (id == null || !locations.containsKey(id)) {
            return false;
        }
        locations.remove(id);
        saveLocations();
        buildGraph();
        return true;
    }

    public CampusLocation getLocationById(String id) {
        return locations.get(id);
    }

    public CampusLocation getLocationByName(String name) {
        if (name == null) return null;
        for (CampusLocation loc : locations.values()) {
            if (loc.getName().equalsIgnoreCase(name.trim())) {
                return loc;
            }
        }
        return null;
    }

    public Collection<CampusLocation> getAllLocations() {
        return locations.values();
    }

    public List<CampusLocation> getLocationsByCategory(String category) {
        List<CampusLocation> result = new ArrayList<>();
        for (CampusLocation loc : locations.values()) {
            if (loc.getCategory().equalsIgnoreCase(category.trim())) {
                result.add(loc);
            }
        }
        return result;
    }

    public List<CampusLocation> searchLocations(String query) {
        List<CampusLocation> matches = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return matches;
        }

        String clean = query.trim().toLowerCase();

        if (clean.equals("lib")) clean = "library";
        else if (clean.equals("cse")) clean = "computer science";
        else if (clean.equals("hostel")) clean = "hostel";
        else if (clean.equals("admin")) clean = "admin block";

        for (CampusLocation loc : locations.values()) {
            if (loc.matches(clean) || loc.matches(query.trim())) {
                matches.add(loc);
            }
        }
        return matches;
    }

    public List<RoomInfo> searchRooms(String query) {
        List<RoomInfo> matches = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return matches;
        }
        for (RoomInfo r : rooms) {
            if (r.matches(query)) {
                matches.add(r);
            }
        }
        return matches;
    }

    public List<RoomInfo> getAllRooms() {
        return rooms;
    }

    public List<BusRoute> getAllBusRoutes() {
        return busRoutes;
    }

    public CampusGraph getGraph() {
        return graph;
    }

    public String getEmergencyContactsFormatted() {
        StringBuilder sb = new StringBuilder();
        sb.append("=====================================================\n");
        sb.append(" CAMPUS EMERGENCY CONTACTS & HELPLINES\n");
        sb.append("=====================================================\n\n");
        sb.append("  [!] All numbers below are official demo campus helplines.\n\n");
        sb.append("  • Campus Security Control:   +91 (0) 1234-567801  [24/7 Gate & Patrol]\n");
        sb.append("  • Central Reception / Info:  +91 (0) 1234-567802  [9:00 AM - 6:00 PM]\n");
        sb.append("  • Campus Medical Room / Doc: +91 (0) 1234-567803  [24/7 First Aid & Ambulance]\n");
        sb.append("  • Fire Safety Emergency:     +91 (0) 1234-567804  [24/7 Rapid Response]\n");
        sb.append("  • Campus Ambulance Service:  +91 (0) 1234-567805  [Emergency On-Call]\n");
        sb.append("  • Women's Safety Helpline:   +91 (0) 1234-567806  [24/7 Dedicated Support]\n");
        sb.append("  • Hostel Chief Warden:       +91 (0) 1234-567807  [Hostel Support]\n");
        sb.append("  • IT Support / Helpdesk:     +91 (0) 1234-567808  [Tech Assistance]\n\n");
        sb.append("=====================================================");
        return sb.toString();
    }

    public String getTimingsSummaryFormatted() {
        StringBuilder sb = new StringBuilder();
        sb.append("=====================================================\n");
        sb.append(" CAMPUS WORKING HOURS & SCHEDULE\n");
        sb.append("=====================================================\n\n");
        for (CampusLocation loc : locations.values()) {
            sb.append(String.format("  %-25s : %s\n", loc.getName(), loc.getTimings()));
        }
        sb.append("\n=====================================================");
        return sb.toString();
    }
}
