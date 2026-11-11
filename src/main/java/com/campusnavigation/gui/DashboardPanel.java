package com.campusnavigation.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import com.campusnavigation.model.CampusLocation;
import com.campusnavigation.model.BusRoute;
import com.campusnavigation.repository.CampusDataManager;
import com.campusnavigation.repository.FavoriteManager;
import com.campusnavigation.repository.SearchHistory;
import com.campusnavigation.service.RouteFinder;

/**
 * Enhanced Campus Navigation Dashboard.
 * Includes:
 * - Interactive clickable location cards with direct routing
 * - Customizable start points for fast route calculation
 * - Wheelchair / Accessible routing mode (barrier-free, elevator & ramp pathways)
 * - Multimodal routing (comparing walking vs campus shuttle transit)
 * - Live search auto-complete suggestion chips and real-time filtering
 * - Multi-floor indoor architectural floor plan viewer
 * - Map zoom & pan with mouse wheel and drag gestures
 * - Complete dark and light collegiate themes
 */
public class DashboardPanel extends Panel implements ActionListener, AdminPanel.AdminRefreshCallback {

    private CampusNavigation parent;
    private CampusDataManager dataManager;
    private FavoriteManager favoriteManager;
    private SearchHistory searchHistory;

    // Top Bar Components
    private Panel topBar;
    private Panel leftTop;
    private Panel centerTop;
    private Panel rightTop;
    private Label titleLabel;
    private TextField searchField;
    private Button searchBtn;
    private Panel suggestionBar;
    private Checkbox darkModeToggle;
    private Button logoutBtn;

    // Sidebar Components
    private Panel sidebar;
    private List<Label> sectionLabels = new ArrayList<>();
    private List<Button> sidebarButtons = new ArrayList<>();
    private Button btnBuildings;
    private Button btnDepartments;
    private Button btnHostels;
    private Button btnLabs;
    private Button btnBusStops;
    private Button btnMap;
    private Button btnFloorPlan;
    private Button btnRouteFinder;
    private Button btnRoomFinder;
    private Button btnBusRoutes;
    private Button btnFavorites;
    private Button btnHistory;
    private Button btnEmergency;
    private Button btnAdmin;
    private Button btnClear;
    private Button btnExit;

    // Center Card Layout
    private CardLayout cardLayout;
    private Panel cardPanel;

    // Content Panels
    private Panel categoryDisplayPanel;
    private Panel headerPanel;
    private Label headerTitle;
    private Label headerSubtitle;
    private ScrollPane scrollPane;
    private Panel cardsWrapper;
    private Panel cardsContainer;

    // Map Panel with interactive Route Bar & Controls
    private Panel mapPanel;
    private CampusMap campusMap;
    private Panel mapRouteBar;
    private Choice startLocationChoice;
    private Checkbox accessibleToggle;
    private Button btnMultimodal;
    private Button btnIndoorPlan;
    private Label routeDestLabel;
    private Label routeSummaryLabel;
    private Button btnBackToList;
    private Button btnCustomRoute;
    private Button btnTurnDirections;
    private Button btnClearRoute;

    // Additional Content Panels
    private IndoorFloorPlan indoorFloorPlan;
    private RouteFinderPanel routeFinderPanel;
    private RoomFinderPanel roomFinderPanel;
    private AdminPanel adminPanel;

    // Sub-panels for consistent theming
    private Panel searchInputRow;
    private Panel mapControlsRow;
    private Label lblFrom;
    private Panel mapTextPanel;
    private Panel mapBtnPanel;
    private Panel mapNorthCombined;

    // Routing & Security State
    private com.campusnavigation.model.User currentUser = null;
    private RouteFinder.RouteResult currentRouteResult = null;
    private boolean isDarkMode = false;
    private String currentView = "ALL";
    private String lastSearchQuery = "";
    private CampusLocation currentSelectedDest = null;
    private RouteFinder.MultimodalOption currentMultimodal = null;
    private boolean showingMultimodal = false;

    public DashboardPanel(CampusNavigation parent, CampusDataManager dataManager,
                          FavoriteManager favoriteManager, SearchHistory searchHistory) {
        this.parent = parent;
        this.dataManager = dataManager;
        this.favoriteManager = favoriteManager;
        this.searchHistory = searchHistory;

        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(245, 247, 250));
        initializeComponents();
        buildLayout();
    }

    private void initializeComponents() {
        // --- Top Bar ---
        titleLabel = new Label("Campus Navigation Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 17));
        titleLabel.setForeground(new Color(30, 50, 90));
        titleLabel.setBackground(new Color(240, 244, 250));

        searchField = new TextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.addActionListener(e -> handleSearch()); // Enter key searches
        searchField.addTextListener(e -> onSearchInputChanged()); // Live auto-complete

        searchBtn = new Button("Search");
        searchBtn.setBackground(new Color(50, 135, 75));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        searchBtn.addActionListener(this);

        suggestionBar = new Panel(new FlowLayout(FlowLayout.LEFT, 4, 1));
        suggestionBar.setBackground(new Color(240, 244, 250));
        suggestionBar.setVisible(false);

        darkModeToggle = new Checkbox("Dark Mode");
        darkModeToggle.setFont(new Font("Arial", Font.PLAIN, 11));
        darkModeToggle.addItemListener(e -> toggleDarkMode(darkModeToggle.getState()));

        logoutBtn = new Button("Logout");
        logoutBtn.setBackground(new Color(195, 60, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 11));
        logoutBtn.addActionListener(this);

        // --- Sidebar Buttons ---
        btnBuildings = createSidebarButton("Buildings");
        btnDepartments = createSidebarButton("Departments");
        btnHostels = createSidebarButton("Hostels");
        btnLabs = createSidebarButton("Labs");
        btnBusStops = createSidebarButton("Bus Stops");
        btnMap = createSidebarButton("Campus Map");
        btnFloorPlan = createSidebarButton("Indoor Plans");
        btnRouteFinder = createSidebarButton("Route Finder");
        btnRoomFinder = createSidebarButton("Room Finder");
        btnBusRoutes = createSidebarButton("Bus Routes");
        btnFavorites = createSidebarButton("Favorites");
        btnHistory = createSidebarButton("History");
        btnEmergency = createSidebarButton("Emergency");
        btnAdmin = createSidebarButton("Admin Panel");
        btnClear = createSidebarButton("Overview / Reset");
        btnExit = createSidebarButton("Exit App");

        // --- Card Layout for Main Content ---
        cardLayout = new CardLayout();
        cardPanel = new Panel(cardLayout);

        // --- Category / Search / List Display Panel ---
        categoryDisplayPanel = new Panel(new BorderLayout(0, 6));
        categoryDisplayPanel.setBackground(new Color(245, 247, 250));

        headerPanel = new Panel(new BorderLayout(5, 2));
        headerPanel.setBackground(new Color(235, 241, 250));
        headerTitle = new Label(" Campus Overview - All Locations");
        headerTitle.setFont(new Font("Arial", Font.BOLD, 14));
        headerTitle.setForeground(new Color(25, 45, 85));
        headerTitle.setBackground(new Color(235, 241, 250));

        headerSubtitle = new Label(" Click any location card or button to view its route on the interactive map ");
        headerSubtitle.setFont(new Font("Arial", Font.PLAIN, 11));
        headerSubtitle.setForeground(new Color(90, 105, 125));
        headerSubtitle.setBackground(new Color(235, 241, 250));

        headerPanel.add(headerTitle, BorderLayout.NORTH);
        headerPanel.add(headerSubtitle, BorderLayout.SOUTH);

        // ScrollPane for interactive location cards
        scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        scrollPane.setBackground(new Color(245, 247, 250));

        cardsWrapper = new Panel(new BorderLayout());
        cardsWrapper.setBackground(new Color(245, 247, 250));

        cardsContainer = new Panel();
        cardsContainer.setBackground(new Color(245, 247, 250));
        cardsWrapper.add(cardsContainer, BorderLayout.NORTH);
        scrollPane.add(cardsWrapper);

        categoryDisplayPanel.add(headerPanel, BorderLayout.NORTH);
        categoryDisplayPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Map Panel with Route Details Bar ---
        mapPanel = new Panel(new BorderLayout(0, 4));
        mapPanel.setBackground(new Color(240, 245, 250));

        campusMap = new CampusMap(dataManager);
        campusMap.setSelectionListener(loc -> onMapLocationSelected(loc));
        mapPanel.add(campusMap, BorderLayout.CENTER);

        // Route bar at bottom of map
        buildMapRouteBar();
        mapPanel.add(mapRouteBar, BorderLayout.SOUTH);

        // Indoor Floor Plan Viewer
        indoorFloorPlan = new IndoorFloorPlan(dataManager, new IndoorFloorPlan.FloorPlanCallback() {
            @Override
            public void onRouteToBuilding(String buildingName) {
                CampusLocation loc = dataManager.getLocationByName(buildingName);
                if (loc == null) {
                    List<CampusLocation> matches = dataManager.searchLocations(buildingName);
                    if (!matches.isEmpty()) loc = matches.get(0);
                }
                if (loc != null) showRouteToLocation(loc);
            }
            @Override
            public void onCloseFloorPlan() {
                cardLayout.show(cardPanel, "MAP");
            }
        });

        // Route Finder Panel
        routeFinderPanel = new RouteFinderPanel(dataManager);
        routeFinderPanel.setRouteCallback(locList -> {
            campusMap.setActiveRoute(locList);
            if (locList != null && !locList.isEmpty()) {
                CampusLocation startLoc = locList.get(0);
                CampusLocation destLoc = locList.get(locList.size() - 1);
                updateMapRouteBar(startLoc, destLoc, locList);
            }
            cardLayout.show(cardPanel, "MAP");
        });

        // Room Finder Panel
        roomFinderPanel = new RoomFinderPanel(dataManager);
        roomFinderPanel.setRouteListener(buildingName -> {
            CampusLocation loc = dataManager.getLocationByName(buildingName);
            if (loc == null) {
                List<CampusLocation> matches = dataManager.searchLocations(buildingName);
                if (!matches.isEmpty()) loc = matches.get(0);
            }
            if (loc != null) {
                showRouteToLocation(loc);
            } else {
                parent.setStatus("Building '" + buildingName + "' not found on map.");
            }
        });

        // Admin Panel
        adminPanel = new AdminPanel(dataManager, this);

        // Add cards to CardLayout
        cardPanel.add(categoryDisplayPanel, "CATEGORY");
        cardPanel.add(mapPanel, "MAP");
        cardPanel.add(indoorFloorPlan, "FLOORPLAN");
        cardPanel.add(routeFinderPanel, "ROUTE");
        cardPanel.add(roomFinderPanel, "ROOM");
        cardPanel.add(adminPanel, "ADMIN");

        // Initial view
        cardLayout.show(cardPanel, "CATEGORY");
        showAllCategories();
    }

    private void buildMapRouteBar() {
        mapRouteBar = new Panel(new BorderLayout(8, 4));
        mapRouteBar.setBackground(new Color(230, 238, 248));

        // Controls Row: Start Dropdown, Wheelchair Toggle, Shuttle Option, Indoor Plan Button
        mapControlsRow = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        mapControlsRow.setBackground(new Color(230, 238, 248));

        lblFrom = new Label("From:");
        lblFrom.setFont(new Font("Arial", Font.BOLD, 10));
        lblFrom.setBackground(new Color(230, 238, 248));
        lblFrom.setForeground(new Color(30, 50, 80));
        mapControlsRow.add(lblFrom);

        startLocationChoice = new Choice();
        populateStartChoices();
        startLocationChoice.addItemListener(e -> {
            if (currentSelectedDest != null) {
                showRouteToLocation(currentSelectedDest);
            }
        });
        mapControlsRow.add(startLocationChoice);

        accessibleToggle = new Checkbox("Wheelchair / Ramp Mode");
        accessibleToggle.setFont(new Font("Arial", Font.BOLD, 10));
        accessibleToggle.setBackground(new Color(230, 238, 248));
        accessibleToggle.addItemListener(e -> {
            if (currentSelectedDest != null) {
                showRouteToLocation(currentSelectedDest);
            }
        });
        mapControlsRow.add(accessibleToggle);

        btnMultimodal = new Button("Shuttle Option");
        btnMultimodal.setFont(new Font("Arial", Font.BOLD, 10));
        btnMultimodal.setBackground(new Color(217, 119, 6)); // Amber
        btnMultimodal.setForeground(Color.WHITE);
        btnMultimodal.setVisible(false);
        btnMultimodal.addActionListener(e -> toggleMultimodalRoute());
        mapControlsRow.add(btnMultimodal);

        btnIndoorPlan = new Button("Indoor Floor Plan");
        btnIndoorPlan.setFont(new Font("Arial", Font.BOLD, 10));
        btnIndoorPlan.setBackground(new Color(139, 92, 246)); // Purple
        btnIndoorPlan.setForeground(Color.WHITE);
        btnIndoorPlan.setVisible(false);
        btnIndoorPlan.addActionListener(e -> {
            if (currentSelectedDest != null) {
                showIndoorPlanForLocation(currentSelectedDest);
            }
        });
        mapControlsRow.add(btnIndoorPlan);

        // Middle Row: Destination label & summary
        mapTextPanel = new Panel(new GridLayout(2, 1, 0, 1));
        mapTextPanel.setBackground(new Color(230, 238, 248));

        routeDestLabel = new Label("Select any building on the map or click a location card to calculate route.");
        routeDestLabel.setFont(new Font("Arial", Font.BOLD, 12));
        routeDestLabel.setForeground(new Color(25, 45, 80));
        routeDestLabel.setBackground(new Color(230, 238, 248));

        routeSummaryLabel = new Label("Zoom: Mouse Wheel or [+] [-] | Pan: Click & Drag | Double-buffered smooth graphics");
        routeSummaryLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        routeSummaryLabel.setForeground(new Color(75, 90, 115));
        routeSummaryLabel.setBackground(new Color(230, 238, 248));

        mapTextPanel.add(routeDestLabel);
        mapTextPanel.add(routeSummaryLabel);

        // Right side Action buttons
        mapBtnPanel = new Panel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        mapBtnPanel.setBackground(new Color(230, 238, 248));

        btnBackToList = new Button("< Back to List");
        btnBackToList.setFont(new Font("Arial", Font.BOLD, 11));
        btnBackToList.setBackground(new Color(60, 120, 190));
        btnBackToList.setForeground(Color.WHITE);
        btnBackToList.addActionListener(e -> cardLayout.show(cardPanel, "CATEGORY"));

        btnCustomRoute = new Button("Custom Route");
        btnCustomRoute.setFont(new Font("Arial", Font.PLAIN, 11));
        btnCustomRoute.setBackground(new Color(70, 140, 80));
        btnCustomRoute.setForeground(Color.WHITE);
        btnCustomRoute.addActionListener(e -> showRouteFinder());

        btnTurnDirections = new Button("Compass Directions");
        btnTurnDirections.setFont(new Font("Arial", Font.BOLD, 11));
        btnTurnDirections.setBackground(new Color(40, 110, 180));
        btnTurnDirections.setForeground(Color.WHITE);
        btnTurnDirections.setVisible(false);
        btnTurnDirections.addActionListener(e -> showTurnByTurnDialog());

        btnClearRoute = new Button("Clear Route");
        btnClearRoute.setFont(new Font("Arial", Font.PLAIN, 11));
        btnClearRoute.setBackground(new Color(160, 60, 60));
        btnClearRoute.setForeground(Color.WHITE);
        btnClearRoute.addActionListener(e -> clearRouteOnMap());

        mapBtnPanel.add(btnBackToList);
        mapBtnPanel.add(btnCustomRoute);
        mapBtnPanel.add(btnTurnDirections);
        mapBtnPanel.add(btnClearRoute);

        mapNorthCombined = new Panel(new BorderLayout());
        mapNorthCombined.setBackground(new Color(230, 238, 248));
        mapNorthCombined.add(mapControlsRow, BorderLayout.WEST);
        mapNorthCombined.add(mapBtnPanel, BorderLayout.EAST);

        mapRouteBar.add(mapNorthCombined, BorderLayout.NORTH);
        mapRouteBar.add(mapTextPanel, BorderLayout.CENTER);
    }

    private void populateStartChoices() {
        startLocationChoice.removeAll();
        startLocationChoice.add("Main Gate (LOC01)");
        startLocationChoice.add("Main Bus Stop (LOC21)");
        startLocationChoice.add("Admin Block (LOC02)");
        startLocationChoice.add("Library (LOC05)");
        startLocationChoice.add("Academic Block A (LOC03)");
        startLocationChoice.add("Academic Block B (LOC04)");
        startLocationChoice.add("Boys Hostel A (LOC16)");
        startLocationChoice.add("Girls Hostel (LOC18)");
        startLocationChoice.add("Sports Complex (LOC20)");
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 11));
        btn.setBackground(new Color(248, 250, 254));
        btn.setForeground(new Color(25, 35, 60));
        btn.addActionListener(this);
        sidebarButtons.add(btn);
        return btn;
    }

    private void buildLayout() {
        // NORTH: Top Bar
        topBar = new Panel(new BorderLayout(10, 4));
        topBar.setBackground(new Color(240, 244, 250));

        // Title left
        leftTop = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        leftTop.setBackground(new Color(240, 244, 250));
        leftTop.add(titleLabel);

        // Search center with suggestion bar
        centerTop = new Panel(new BorderLayout(4, 2));
        centerTop.setBackground(new Color(240, 244, 250));

        searchInputRow = new Panel(new BorderLayout(4, 0));
        searchInputRow.setBackground(new Color(240, 244, 250));
        searchInputRow.add(searchField, BorderLayout.CENTER);
        searchInputRow.add(searchBtn, BorderLayout.EAST);

        centerTop.add(searchInputRow, BorderLayout.CENTER);
        centerTop.add(suggestionBar, BorderLayout.SOUTH);

        // Right side controls
        rightTop = new Panel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        rightTop.setBackground(new Color(240, 244, 250));
        rightTop.add(darkModeToggle);
        rightTop.add(logoutBtn);

        topBar.add(leftTop, BorderLayout.WEST);
        topBar.add(centerTop, BorderLayout.CENTER);
        topBar.add(rightTop, BorderLayout.EAST);

        // WEST: Sidebar
        sidebar = new Panel(new GridLayout(0, 1, 4, 3));
        sidebar.setBackground(new Color(235, 240, 248));
        sidebar.setPreferredSize(new Dimension(175, 0));

        // Section: Info Categories
        sidebar.add(createSectionLabel("CAMPUS INFO"));
        sidebar.add(btnBuildings);
        sidebar.add(btnDepartments);
        sidebar.add(btnHostels);
        sidebar.add(btnLabs);
        sidebar.add(btnBusStops);

        // Section: Tools
        sidebar.add(createSectionLabel("TOOLS"));
        sidebar.add(btnMap);
        sidebar.add(btnFloorPlan);
        sidebar.add(btnRouteFinder);
        sidebar.add(btnRoomFinder);
        sidebar.add(btnBusRoutes);

        // Section: Personal
        sidebar.add(createSectionLabel("PERSONAL"));
        sidebar.add(btnFavorites);
        sidebar.add(btnHistory);

        // Section: System
        sidebar.add(createSectionLabel("SYSTEM"));
        sidebar.add(btnEmergency);
        sidebar.add(btnAdmin);
        sidebar.add(btnClear);
        sidebar.add(btnExit);

        // Assemble
        add(topBar, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);
    }

    private Label createSectionLabel(String text) {
        Label lbl = new Label("  " + text);
        lbl.setFont(new Font("Arial", Font.BOLD, 9));
        lbl.setForeground(new Color(85, 100, 125));
        sectionLabels.add(lbl);
        return lbl;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == searchBtn) {
            handleSearch();
        } else if (src == logoutBtn) {
            parent.onLogout();
        } else if (src == btnBuildings) {
            showCategory("Building");
        } else if (src == btnDepartments) {
            showCategory("Department");
        } else if (src == btnHostels) {
            showCategory("Hostel");
        } else if (src == btnLabs) {
            showCategory("Laboratory");
        } else if (src == btnBusStops) {
            showCategory("Bus Stop");
        } else if (src == btnMap) {
            showMap();
        } else if (src == btnFloorPlan) {
            showFloorPlan();
        } else if (src == btnRouteFinder) {
            showRouteFinder();
        } else if (src == btnRoomFinder) {
            showRoomFinder();
        } else if (src == btnBusRoutes) {
            showBusRoutes();
        } else if (src == btnFavorites) {
            showFavorites();
        } else if (src == btnHistory) {
            showHistory();
        } else if (src == btnEmergency) {
            showEmergency();
        } else if (src == btnAdmin) {
            showAdmin();
        } else if (src == btnClear) {
            showAllCategories();
        } else if (src == btnExit) {
            System.exit(0);
        }
    }

    // =========================================================================
    // Core Feature: Calculate & Display Route to Specific Location
    // =========================================================================
    public void showRouteToLocation(CampusLocation dest) {
        if (dest == null) return;
        this.currentSelectedDest = dest;
        this.showingMultimodal = false;

        // Resolve Start Location from dropdown
        String startChoiceText = startLocationChoice.getSelectedItem();
        String startId = "LOC01"; // default Main Gate
        if (startChoiceText != null && startChoiceText.contains("(") && startChoiceText.contains(")")) {
            startId = startChoiceText.substring(startChoiceText.indexOf("(") + 1, startChoiceText.indexOf(")")).trim();
        }

        CampusLocation startLoc = dataManager.getLocationById(startId);
        if (startLoc == null || startLoc.getId().equals(dest.getId())) {
            startLoc = dataManager.getLocationById("LOC01");
            if (startLoc == null || startLoc.getId().equals(dest.getId())) {
                startLoc = dataManager.getLocationById("LOC21");
            }
        }

        boolean accessibleOnly = accessibleToggle.getState();
        RouteFinder.RouteResult result = null;
        if (startLoc != null && !startLoc.getId().equals(dest.getId())) {
            result = RouteFinder.findShortestPath(dataManager.getGraph(), startLoc.getId(), dest.getId(), accessibleOnly);
        }

        // Check Multimodal (Shuttle + Walk) availability
        currentMultimodal = (startLoc != null && !startLoc.getId().equals(dest.getId()))
                            ? RouteFinder.findMultimodalRoute(dataManager.getGraph(), startLoc.getId(), dest.getId())
                            : null;

        if (currentMultimodal != null && currentMultimodal.isAvailable()) {
            btnMultimodal.setLabel("Shuttle Option (Saves " + currentMultimodal.getSavedWalkMeters() + "m walk!)");
            btnMultimodal.setVisible(true);
        } else {
            btnMultimodal.setVisible(false);
        }

        // Indoor plan button
        if (dest.getBuilding() != null && !dest.getBuilding().isEmpty()) {
            btnIndoorPlan.setLabel("Floor Plan (" + dest.getBuilding() + ")");
            btnIndoorPlan.setVisible(true);
        } else {
            btnIndoorPlan.setVisible(false);
        }

        campusMap.setSelectedLocation(dest);

        if (result != null && result.getPath() != null) {
            this.currentRouteResult = result;
            btnTurnDirections.setVisible(true);
            campusMap.setActiveRoute(result.getPath());
            updateMapRouteBar(startLoc, dest, result);
            String accessText = accessibleOnly ? " [Barrier-Free Route]" : "";
            parent.setStatus("Route: " + startLoc.getName() + " -> " + dest.getName() +
                             " (" + result.getTotalDistance() + "m, ~" + result.getEstimatedMinutes() + " min walk)" + accessText);
        } else {
            this.currentRouteResult = null;
            btnTurnDirections.setVisible(false);
            campusMap.setActiveRoute(null);
            updateMapLocationOnly(dest);
            parent.setStatus("Selected: " + dest.getName() + " on campus map.");
        }

        mapRouteBar.validate();
        cardLayout.show(cardPanel, "MAP");
    }

    private void toggleMultimodalRoute() {
        if (currentMultimodal == null || !currentMultimodal.isAvailable() || currentSelectedDest == null) return;

        showingMultimodal = !showingMultimodal;
        if (showingMultimodal) {
            // Plot transit route
            CampusLocation board = currentMultimodal.getBoardStop();
            CampusLocation disembark = currentMultimodal.getDisembarkStop();
            List<CampusLocation> transitPath = new ArrayList<>();
            transitPath.add(board);
            transitPath.add(disembark);
            transitPath.add(currentSelectedDest);

            campusMap.setActiveRoute(transitPath);
            routeDestLabel.setText("Shuttle Option: " + currentMultimodal.getSummaryText());
            routeSummaryLabel.setText("1. Walk to " + board.getName() + " -> 2. Take " + currentMultimodal.getShuttleName() +
                                     " to " + disembark.getName() + " (3 min ride) -> 3. Walk to " + currentSelectedDest.getName() +
                                     " | Total time: ~" + currentMultimodal.getTotalMinutes() + " mins");
            btnMultimodal.setLabel("Switch to Direct Walking");
            parent.setStatus("Multimodal route displayed: " + currentMultimodal.getSummaryText());
        } else {
            // Revert to direct walking
            btnMultimodal.setLabel("Shuttle Option (Saves " + currentMultimodal.getSavedWalkMeters() + "m walk!)");
            showRouteToLocation(currentSelectedDest);
        }
    }

    private void updateMapRouteBar(CampusLocation start, CampusLocation dest, RouteFinder.RouteResult result) {
        if (dest != null && result != null && result.getPath() != null) {
            String accessBadge = result.isWheelchairAccessible() ? " [Barrier-Free]" : "";
            routeDestLabel.setText("Route to: " + dest.getName() + " (" + dest.getBuilding() + ", Floor " + dest.getFloor() + ")" + accessBadge);
            String firstTurn = (result.getTurnByTurnDirections() != null && !result.getTurnByTurnDirections().isEmpty())
                               ? " | Turn 1: " + result.getTurnByTurnDirections().get(0)
                               : "";
            routeSummaryLabel.setText("Distance: " + result.getTotalDistance() + "m | Est. Walk: ~" +
                                     result.getEstimatedMinutes() + " min" + firstTurn);
        } else if (dest != null) {
            updateMapLocationOnly(dest);
        }
    }

    private void updateMapRouteBar(CampusLocation start, CampusLocation dest, List<CampusLocation> path) {
        if (dest != null && path != null && path.size() >= 2) {
            int totalDist = 0;
            for (int i = 0; i < path.size() - 1; i++) {
                int d = dataManager.getGraph().getDistance(path.get(i).getId(), path.get(i + 1).getId());
                if (d > 0) totalDist += d;
            }
            int mins = Math.max(1, (int) Math.ceil(totalDist / 70.0));
            routeDestLabel.setText("Route: " + path.get(0).getName() + " -> " + dest.getName());
            StringBuilder pathStr = new StringBuilder();
            for (int i = 0; i < path.size(); i++) {
                pathStr.append(path.get(i).getName());
                if (i < path.size() - 1) pathStr.append(" -> ");
            }
            routeSummaryLabel.setText("Distance: " + totalDist + "m | Walk: ~" + mins + " min | Path: " + pathStr.toString());
        }
    }

    private void updateMapLocationOnly(CampusLocation dest) {
        if (dest != null) {
            routeDestLabel.setText("Location: " + dest.getName() + " [" + dest.getCategory() + "] - " + dest.getBuilding());
            routeSummaryLabel.setText("Floor: " + dest.getFloor() + " | Timings: " + dest.getTimings() + " | " + dest.getDescription());
        }
    }

    private void showTurnByTurnDialog() {
        if (currentRouteResult == null || currentRouteResult.getTurnByTurnDirections() == null || currentRouteResult.getTurnByTurnDirections().isEmpty()) {
            CustomDialog.showMessage(parent, "Compass Directions", "No active route calculated. Select any building to see turn-by-turn directions.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("CAMPUS TURN-BY-TURN COMPASS DIRECTIONS\n");
        sb.append("=====================================================\n\n");
        List<String> steps = currentRouteResult.getTurnByTurnDirections();
        for (int i = 0; i < steps.size(); i++) {
            sb.append(i + 1).append(". ").append(steps.get(i)).append("\n\n");
        }
        sb.append("=====================================================\n");
        sb.append("Total Distance: ").append(currentRouteResult.getTotalDistance()).append(" meters\n");
        sb.append("Estimated Time: ~").append(currentRouteResult.getEstimatedMinutes()).append(" minute(s) walk\n");
        sb.append("Accessibility: ").append(currentRouteResult.isWheelchairAccessible() ? "Barrier-Free Ramps & Paved" : "Standard Campus Pathways");

        CustomDialog.showMessage(parent, "Turn-by-Turn Directions", sb.toString());
    }

    public void onUserSessionChanged(com.campusnavigation.model.User user) {
        this.currentUser = user;
        if (btnAdmin != null) {
            boolean isAdmin = (user != null && user.getRole().canAccessAdminPanel());
            btnAdmin.setLabel(isAdmin ? "Admin Panel" : "Admin Panel [Locked]");
        }
    }

    private void clearRouteOnMap() {
        campusMap.clearHighlights();
        currentSelectedDest = null;
        currentRouteResult = null;
        if (btnTurnDirections != null) btnTurnDirections.setVisible(false);
        btnMultimodal.setVisible(false);
        btnIndoorPlan.setVisible(false);
        routeDestLabel.setText("Select any building on the map or click a location card to calculate walking route.");
        routeSummaryLabel.setText("Zoom: Mouse Wheel or [+] [-] | Pan: Click & Drag | Live route beacon animation");
        parent.setStatus("Map route cleared.");
    }

    private void onMapLocationSelected(CampusLocation loc) {
        if (loc != null) {
            showRouteToLocation(loc);
        }
    }

    // =========================================================================
    // Live Auto-Complete Suggestions & Interactive Search
    // =========================================================================
    private void onSearchInputChanged() {
        String query = searchField.getText().trim();
        suggestionBar.removeAll();

        if (query.length() < 2) {
            suggestionBar.setVisible(false);
            suggestionBar.validate();
            topBar.validate();
            return;
        }

        List<CampusLocation> matches = dataManager.searchLocations(query);
        if (matches.isEmpty()) {
            suggestionBar.setVisible(false);
        } else {
            Label lblHint = new Label("Suggestions:");
            lblHint.setFont(new Font("Arial", Font.BOLD, 10));
            lblHint.setBackground(isDarkMode ? new Color(24, 28, 38) : new Color(240, 244, 250));
            lblHint.setForeground(isDarkMode ? new Color(160, 180, 210) : new Color(70, 85, 110));
            suggestionBar.add(lblHint);

            int count = 0;
            for (CampusLocation loc : matches) {
                if (count >= 4) break;
                Button chip = new Button(loc.getName());
                chip.setFont(new Font("Arial", Font.PLAIN, 10));
                chip.setBackground(isDarkMode ? new Color(40, 50, 70) : new Color(225, 235, 250));
                chip.setForeground(isDarkMode ? new Color(210, 230, 255) : new Color(25, 45, 90));
                chip.addActionListener(e -> {
                    searchField.setText(loc.getName());
                    suggestionBar.setVisible(false);
                    showRouteToLocation(loc);
                });
                suggestionBar.add(chip);
                count++;
            }
            suggestionBar.setVisible(true);
        }

        suggestionBar.validate();
        topBar.validate();
    }

    private void handleSearch() {
        suggestionBar.setVisible(false);
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            parent.setStatus("Search query cannot be empty.");
            return;
        }
        lastSearchQuery = query;
        searchHistory.addSearch(query);
        List<CampusLocation> results = dataManager.searchLocations(query);

        currentView = "SEARCH";
        cardsContainer.removeAll();

        if (results.isEmpty()) {
            headerTitle.setText(" Search Results for: \"" + query + "\" (0 matches)");
            headerSubtitle.setText(" No locations found matching your query. Try searching for building, department, or lab names.");
            Panel emptyCard = createInfoCard("No Matching Locations Found",
                "• Check for typos or alternative names (e.g. 'CSE' for Computer Science, 'Block A' for Academic Block A).\n" +
                "• Try browsing categories from the sidebar on the left.");
            cardsContainer.setLayout(new GridLayout(1, 1));
            cardsContainer.add(emptyCard);
            parent.setStatus("Search completed: 0 matches found.");
        } else {
            headerTitle.setText(" Search Results for: \"" + query + "\" (" + results.size() + " matches found)");
            headerSubtitle.setText(" Click any location card or 'Show Route' button to view its route on the interactive map");

            cardsContainer.setLayout(new GridLayout(results.size(), 1, 0, 6));
            for (CampusLocation loc : results) {
                cardsContainer.add(new LocationCard(loc, isDarkMode, this, favoriteManager.isFavorite(loc.getId())));
            }
            parent.setStatus("Search completed: " + results.size() + " matches found. Click any to show route.");
        }

        cardsContainer.validate();
        cardsWrapper.validate();
        scrollPane.validate();
        cardLayout.show(cardPanel, "CATEGORY");
    }

    private void showCategory(String category) {
        currentView = category;
        List<CampusLocation> list = dataManager.getLocationsByCategory(category);

        headerTitle.setText(" " + category.toUpperCase() + "S (" + list.size() + " locations)");
        headerSubtitle.setText(" Click any " + category.toLowerCase() + " card or 'Show Route' to view path on map");

        cardsContainer.removeAll();
        cardsContainer.setLayout(new GridLayout(Math.max(1, list.size()), 1, 0, 6));

        for (CampusLocation loc : list) {
            cardsContainer.add(new LocationCard(loc, isDarkMode, this, favoriteManager.isFavorite(loc.getId())));
        }

        cardsContainer.validate();
        cardsWrapper.validate();
        scrollPane.validate();
        cardLayout.show(cardPanel, "CATEGORY");
        parent.setStatus("Showing: " + category + "s (" + list.size() + "). Click any to view route.");
    }

    private void showAllCategories() {
        currentView = "ALL";
        headerTitle.setText(" Campus Overview — All Locations & Categories");
        headerSubtitle.setText(" Every section and card is interactive! Click any location to see its walking route on the map.");

        cardsContainer.removeAll();

        String[] cats = {"Building", "Department", "Hostel", "Laboratory", "Bus Stop", "Facility"};
        int totalItems = 0;
        List<Component> itemsToAdd = new ArrayList<>();

        for (String cat : cats) {
            List<CampusLocation> list = dataManager.getLocationsByCategory(cat);
            if (!list.isEmpty()) {
                final String currentCat = cat;
                itemsToAdd.add(new SectionHeader(cat + "s", list.size(), isDarkMode, () -> showCategory(currentCat)));
                totalItems++;
                for (CampusLocation loc : list) {
                    itemsToAdd.add(new LocationCard(loc, isDarkMode, this, favoriteManager.isFavorite(loc.getId())));
                    totalItems++;
                }
            }
        }

        cardsContainer.setLayout(new GridLayout(Math.max(1, totalItems), 1, 0, 6));
        for (Component comp : itemsToAdd) {
            cardsContainer.add(comp);
        }

        cardsContainer.validate();
        cardsWrapper.validate();
        scrollPane.validate();
        cardLayout.show(cardPanel, "CATEGORY");
        parent.setStatus("Campus Overview: Click any location card to see route.");
    }

    public void showIndoorPlanForLocation(CampusLocation loc) {
        if (loc != null) {
            String bName = (loc.getBuilding() != null && !loc.getBuilding().isEmpty()) ? loc.getBuilding() : loc.getName();
            indoorFloorPlan.selectBuildingAndFloor(bName, loc.getFloor());
            cardLayout.show(cardPanel, "FLOORPLAN");
            parent.setStatus("Indoor Floor Plan: " + bName + " (" + loc.getFloor() + ")");
        }
    }

    private void showFloorPlan() {
        cardLayout.show(cardPanel, "FLOORPLAN");
        parent.setStatus("Indoor Multi-Floor Architectural Plan: Inspect rooms, labs & accessible core.");
    }

    private void showFavorites() {
        currentView = "FAVORITES";
        Set<String> favIds = favoriteManager.getFavoriteIds();

        headerTitle.setText(" Your Favorite Locations (" + favIds.size() + " saved)");
        headerSubtitle.setText(" Quick access to your bookmarked campus spots. Click any to view walking route.");

        cardsContainer.removeAll();

        if (favIds.isEmpty()) {
            cardsContainer.setLayout(new GridLayout(1, 1));
            cardsContainer.add(createInfoCard("No Favorites Added Yet",
                "You haven't bookmarked any locations yet.\nClick 'Save' on any location card to bookmark it for fast access!"));
        } else {
            List<CampusLocation> favLocs = new ArrayList<>();
            for (String id : favIds) {
                CampusLocation loc = dataManager.getLocationById(id);
                if (loc != null) favLocs.add(loc);
            }
            cardsContainer.setLayout(new GridLayout(favLocs.size(), 1, 0, 6));
            for (CampusLocation loc : favLocs) {
                cardsContainer.add(new LocationCard(loc, isDarkMode, this, true));
            }
        }

        cardsContainer.validate();
        cardsWrapper.validate();
        scrollPane.validate();
        cardLayout.show(cardPanel, "CATEGORY");
        parent.setStatus("Favorites: " + favIds.size() + " location(s) saved.");
    }

    private void showHistory() {
        currentView = "HISTORY";
        List<String> history = searchHistory.getHistory();

        headerTitle.setText(" Recent Search History (" + history.size() + " entries)");
        headerSubtitle.setText(" Click 'Search Again' on any previous query to instantly re-execute that search.");

        cardsContainer.removeAll();

        if (history.isEmpty()) {
            cardsContainer.setLayout(new GridLayout(1, 1));
            cardsContainer.add(createInfoCard("Search History is Empty",
                "Searches you perform using the top search bar will be saved here for quick re-use."));
        } else {
            cardsContainer.setLayout(new GridLayout(history.size(), 1, 0, 6));
            for (String query : history) {
                cardsContainer.add(createHistoryCard(query));
            }
        }

        cardsContainer.validate();
        cardsWrapper.validate();
        scrollPane.validate();
        cardLayout.show(cardPanel, "CATEGORY");
        parent.setStatus("Recent search history displayed.");
    }

    private Panel createHistoryCard(String query) {
        Color cardBg = isDarkMode ? new Color(34, 40, 54) : Color.WHITE;
        Panel card = new Panel(new BorderLayout(10, 4));
        card.setBackground(cardBg);

        Label lbl = new Label("  \"" + query + "\"");
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setBackground(cardBg);
        lbl.setForeground(isDarkMode ? new Color(200, 220, 250) : new Color(30, 45, 80));

        Button btnReSearch = new Button("Search Again");
        btnReSearch.setFont(new Font("Arial", Font.PLAIN, 11));
        btnReSearch.setBackground(new Color(50, 130, 75));
        btnReSearch.setForeground(Color.WHITE);
        btnReSearch.addActionListener(e -> {
            searchField.setText(query);
            handleSearch();
        });

        card.add(lbl, BorderLayout.CENTER);
        card.add(btnReSearch, BorderLayout.EAST);
        return card;
    }

    private void showBusRoutes() {
        currentView = "BUS";
        List<BusRoute> routes = dataManager.getAllBusRoutes();

        headerTitle.setText(" Campus Bus Routes & Timings (" + routes.size() + " active routes)");
        headerSubtitle.setText(" Click any bus route card or 'Show on Map' button to view its location on the campus map.");

        cardsContainer.removeAll();
        cardsContainer.setLayout(new GridLayout(Math.max(1, routes.size()), 1, 0, 6));

        for (BusRoute route : routes) {
            cardsContainer.add(createBusRouteCard(route));
        }

        cardsContainer.validate();
        cardsWrapper.validate();
        scrollPane.validate();
        cardLayout.show(cardPanel, "CATEGORY");
        parent.setStatus("Bus routes displayed. Click any route to view stop on map.");
    }

    public CampusLocation resolveBusStopLocation(BusRoute route) {
        if (route == null) return null;
        String stop = route.getStopName().trim();
        CampusLocation loc = dataManager.getLocationByName(stop);
        if (loc != null) return loc;

        String sLower = stop.toLowerCase();
        if (sLower.contains("main")) {
            loc = dataManager.getLocationById("LOC21");
            if (loc != null) return loc;
        }
        if (sLower.contains("library")) {
            loc = dataManager.getLocationById("LOC22");
            if (loc != null) return loc;
        }
        if (sLower.contains("hostel")) {
            loc = dataManager.getLocationById("LOC23");
            if (loc != null) return loc;
        }

        List<CampusLocation> matches = dataManager.searchLocations(stop);
        for (CampusLocation m : matches) {
            if ("Bus Stop".equalsIgnoreCase(m.getCategory())) return m;
        }
        if (!matches.isEmpty()) return matches.get(0);
        return null;
    }

    public void showBusLocation(BusRoute route) {
        if (route == null) return;
        CampusLocation stopLoc = resolveBusStopLocation(route);
        if (stopLoc != null) {
            showRouteToLocation(stopLoc);
            routeDestLabel.setText("Bus Stop: " + stopLoc.getName() + " [" + route.getRouteNumber() + " -> " + route.getDestination() + "]");
            routeSummaryLabel.setText("Timings: " + route.getTimings() + " | Nearby: " + route.getNearbyLocations());
            parent.setStatus("Bus " + route.getRouteNumber() + " at " + stopLoc.getName() + " displayed on map.");
        } else {
            parent.setStatus("Bus stop for " + route.getRouteNumber() + " not found.");
        }
    }

    private Panel createBusRouteCard(BusRoute route) {
        Color cardBg = isDarkMode ? new Color(34, 40, 54) : Color.WHITE;

        Panel card = new Panel(new BorderLayout(8, 2)) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(450, 48);
            }
        };
        card.setBackground(cardBg);

        // Left Text Panel: Row 1 = Title/Destination, Row 2 = Timings & Nearby
        Panel textPanel = new Panel(new GridLayout(2, 1, 0, 1));
        textPanel.setBackground(cardBg);

        Label lblTitle = new Label("Bus " + route.getRouteNumber() + " -> " + route.getDestination());
        lblTitle.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitle.setBackground(cardBg);
        lblTitle.setForeground(isDarkMode ? new Color(255, 205, 110) : new Color(160, 90, 15));

        Label lblDetails = new Label("Stop: " + route.getStopName() + " | Timings: " + route.getTimings() + " | Near: " + route.getNearbyLocations());
        lblDetails.setFont(new Font("Arial", Font.PLAIN, 10));
        lblDetails.setBackground(cardBg);
        lblDetails.setForeground(isDarkMode ? new Color(175, 185, 200) : new Color(90, 100, 115));

        textPanel.add(lblTitle);
        textPanel.add(lblDetails);

        // Right Action Button
        Panel actionPanel = new Panel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        actionPanel.setBackground(cardBg);

        Button btnShow = new Button("Show on Map");
        btnShow.setFont(new Font("Arial", Font.BOLD, 11));
        btnShow.setBackground(new Color(35, 120, 215));
        btnShow.setForeground(Color.WHITE);
        btnShow.addActionListener(e -> showBusLocation(route));
        actionPanel.add(btnShow);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.EAST);

        // Entire Card Clickable
        MouseAdapter clickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showBusLocation(route);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        };
        card.addMouseListener(clickListener);
        textPanel.addMouseListener(clickListener);
        lblTitle.addMouseListener(clickListener);
        lblDetails.addMouseListener(clickListener);

        return card;
    }

    private void showEmergency() {
        currentView = "EMERGENCY";
        headerTitle.setText(" Campus Emergency Contacts & Medical Assistance");
        headerSubtitle.setText(" 24/7 Security, Medical, Fire, and Helpline Services across campus.");

        cardsContainer.removeAll();
        cardsContainer.setLayout(new GridLayout(4, 1, 0, 6));

        cardsContainer.add(createEmergencyCard("Campus Security & Main Gate Control", "Ext: 1001 / +91-98765-00001",
            "Main Gate Post - 24/7 Patrol, Surveillance, Gate Entry Control", "LOC01"));
        cardsContainer.add(createEmergencyCard("Campus Health Center / Ambulance", "Ext: 1002 / +91-98765-00002",
            "Admin Block Ground Floor - 24/7 Doctor on duty, Emergency Ambulance", "LOC02"));
        cardsContainer.add(createEmergencyCard("Campus Fire & Disaster Helpline", "Ext: 1003 / 101",
            "Near Sports Complex - Fire hydrants, alarms, rapid response team", "LOC20"));
        cardsContainer.add(createEmergencyCard("Women's Safety & Anti-Ragging Cell", "Ext: 1004 / +91-98765-00004",
            "Girls Hostel Warden Office - 24-Hour Confidential Helpline", "LOC18"));

        cardsContainer.validate();
        cardsWrapper.validate();
        scrollPane.validate();
        cardLayout.show(cardPanel, "CATEGORY");
        parent.setStatus("Emergency Contacts: 24/7 helpline numbers listed.");
    }

    private Panel createEmergencyCard(String title, String phone, String details, String locId) {
        Color emBg = isDarkMode ? new Color(38, 30, 34) : new Color(255, 245, 245);
        Panel card = new Panel(new BorderLayout(8, 4));
        card.setBackground(emBg);

        Panel info = new Panel(new GridLayout(2, 1, 0, 2));
        info.setBackground(emBg);

        Label lblTitle = new Label("  " + title + "  [Tel: " + phone + "]");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitle.setBackground(emBg);
        lblTitle.setForeground(new Color(210, 45, 45));

        Label lblSub = new Label("  " + details);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 11));
        lblSub.setBackground(emBg);
        lblSub.setForeground(isDarkMode ? new Color(200, 180, 185) : new Color(90, 70, 75));

        info.add(lblTitle);
        info.add(lblSub);

        Button btnRoute = new Button("Route on Map");
        btnRoute.setFont(new Font("Arial", Font.BOLD, 11));
        btnRoute.setBackground(new Color(195, 50, 50));
        btnRoute.setForeground(Color.WHITE);
        btnRoute.addActionListener(e -> {
            CampusLocation loc = dataManager.getLocationById(locId);
            if (loc != null) showRouteToLocation(loc);
        });

        card.add(info, BorderLayout.CENTER);
        card.add(btnRoute, BorderLayout.EAST);
        return card;
    }

    private Panel createInfoCard(String title, String message) {
        Color cardBg = isDarkMode ? new Color(32, 38, 52) : Color.WHITE;
        Panel card = new Panel(new BorderLayout(8, 6));
        card.setBackground(cardBg);

        Label lblTitle = new Label("  " + title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 13));
        lblTitle.setBackground(cardBg);
        lblTitle.setForeground(isDarkMode ? new Color(200, 225, 255) : new Color(30, 50, 90));

        TextArea ta = new TextArea(message, 3, 40, TextArea.SCROLLBARS_NONE);
        ta.setEditable(false);
        ta.setFont(new Font("Arial", Font.PLAIN, 11));
        ta.setBackground(cardBg);
        ta.setForeground(isDarkMode ? new Color(180, 190, 205) : new Color(75, 85, 100));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(ta, BorderLayout.CENTER);
        return card;
    }

    public void toggleFavorite(CampusLocation loc) {
        if (loc == null) return;
        if (favoriteManager.isFavorite(loc.getId())) {
            favoriteManager.removeFavorite(loc.getId());
        } else {
            favoriteManager.addFavorite(loc.getId());
        }
        refreshCurrentView();
    }

    private void refreshCurrentView() {
        if ("ALL".equals(currentView)) showAllCategories();
        else if ("FAVORITES".equals(currentView)) showFavorites();
        else if ("SEARCH".equals(currentView)) {
            if (!lastSearchQuery.isEmpty()) {
                searchField.setText(lastSearchQuery);
                handleSearch();
            } else {
                showAllCategories();
            }
        }
        else if ("BUS".equals(currentView)) showBusRoutes();
        else if ("HISTORY".equals(currentView)) showHistory();
        else if ("EMERGENCY".equals(currentView)) showEmergency();
        else showCategory(currentView);
    }

    private void showMap() {
        cardLayout.show(cardPanel, "MAP");
        campusMap.clearHighlights();
        parent.setStatus("Interactive Campus Map: Click any building to view its walking route.");
    }

    private void showRouteFinder() {
        cardLayout.show(cardPanel, "ROUTE");
        parent.setStatus("Route Finder: Select Source and Destination to find shortest path.");
    }

    private void showRoomFinder() {
        cardLayout.show(cardPanel, "ROOM");
        parent.setStatus("Room Finder: Search for room numbers (101, CSE-201, LAB-01).");
    }

    private void showAdmin() {
        cardLayout.show(cardPanel, "ADMIN");
        adminPanel.refreshTable();
        parent.setStatus("Admin Panel active. Manage campus locations.");
    }

    // =========================================================================
    // Dark Mode Theme Engine
    // =========================================================================
    public void toggleDarkMode(boolean dark) {
        this.isDarkMode = dark;
        applyTheme(dark);
        campusMap.setDarkMode(dark);
        indoorFloorPlan.applyTheme(dark);
        parent.applyTheme(dark);
    }

    private void applyTheme(boolean dark) {
        Color bg = dark ? new Color(20, 24, 33) : new Color(245, 247, 250);
        Color topBg = dark ? new Color(24, 28, 38) : new Color(240, 244, 250);
        Color sidebarBg = dark ? new Color(18, 22, 30) : new Color(235, 240, 248);
        Color btnBg = dark ? new Color(34, 40, 54) : new Color(248, 250, 254);
        Color btnFg = dark ? new Color(220, 230, 245) : new Color(25, 35, 60);
        Color sectionLblFg = dark ? new Color(150, 170, 200) : new Color(85, 100, 125);
        Color headerPanelBg = dark ? new Color(28, 33, 45) : new Color(235, 241, 250);
        Color mapBarBg = dark ? new Color(26, 32, 44) : new Color(230, 238, 248);

        setBackground(bg);

        // Top bar
        if (topBar != null) topBar.setBackground(topBg);
        if (leftTop != null) leftTop.setBackground(topBg);
        if (centerTop != null) centerTop.setBackground(topBg);
        if (rightTop != null) rightTop.setBackground(topBg);
        if (searchInputRow != null) searchInputRow.setBackground(topBg);
        if (suggestionBar != null) suggestionBar.setBackground(topBg);
        if (titleLabel != null) {
            titleLabel.setBackground(topBg);
            titleLabel.setForeground(dark ? new Color(195, 220, 255) : new Color(30, 50, 90));
        }
        if (searchField != null) {
            searchField.setBackground(dark ? new Color(38, 44, 58) : Color.WHITE);
            searchField.setForeground(dark ? Color.WHITE : Color.BLACK);
        }
        if (darkModeToggle != null) {
            darkModeToggle.setForeground(dark ? new Color(225, 235, 245) : Color.BLACK);
            darkModeToggle.setBackground(topBg);
        }

        // Sidebar
        if (sidebar != null) sidebar.setBackground(sidebarBg);
        for (Label lbl : sectionLabels) {
            lbl.setForeground(sectionLblFg);
            lbl.setBackground(sidebarBg);
        }
        for (Button btn : sidebarButtons) {
            btn.setBackground(btnBg);
            btn.setForeground(btnFg);
        }

        // Category display panel
        if (categoryDisplayPanel != null) categoryDisplayPanel.setBackground(bg);
        if (headerPanel != null) headerPanel.setBackground(headerPanelBg);
        if (headerTitle != null) {
            headerTitle.setBackground(headerPanelBg);
            headerTitle.setForeground(dark ? new Color(200, 225, 255) : new Color(25, 45, 85));
        }
        if (headerSubtitle != null) {
            headerSubtitle.setBackground(headerPanelBg);
            headerSubtitle.setForeground(dark ? new Color(160, 175, 195) : new Color(90, 105, 125));
        }
        if (scrollPane != null) scrollPane.setBackground(bg);
        if (cardsWrapper != null) cardsWrapper.setBackground(bg);
        if (cardsContainer != null) cardsContainer.setBackground(bg);

        // Map panel & route bar
        if (mapPanel != null) mapPanel.setBackground(dark ? new Color(24, 28, 36) : new Color(240, 245, 250));
        if (mapRouteBar != null) mapRouteBar.setBackground(mapBarBg);
        if (mapControlsRow != null) mapControlsRow.setBackground(mapBarBg);
        if (lblFrom != null) {
            lblFrom.setBackground(mapBarBg);
            lblFrom.setForeground(dark ? new Color(185, 205, 235) : new Color(30, 50, 80));
        }
        if (startLocationChoice != null) {
            startLocationChoice.setBackground(dark ? new Color(38, 44, 58) : Color.WHITE);
            startLocationChoice.setForeground(dark ? Color.WHITE : Color.BLACK);
        }
        if (accessibleToggle != null) {
            accessibleToggle.setBackground(mapBarBg);
            accessibleToggle.setForeground(dark ? new Color(210, 230, 255) : Color.BLACK);
        }
        if (mapTextPanel != null) mapTextPanel.setBackground(mapBarBg);
        if (mapBtnPanel != null) mapBtnPanel.setBackground(mapBarBg);
        if (mapNorthCombined != null) mapNorthCombined.setBackground(mapBarBg);
        if (routeDestLabel != null) {
            routeDestLabel.setBackground(mapBarBg);
            routeDestLabel.setForeground(dark ? new Color(210, 230, 255) : new Color(25, 45, 80));
        }
        if (routeSummaryLabel != null) {
            routeSummaryLabel.setBackground(mapBarBg);
            routeSummaryLabel.setForeground(dark ? new Color(165, 180, 200) : new Color(75, 90, 115));
        }

        // Child panels
        if (indoorFloorPlan != null) indoorFloorPlan.applyTheme(dark);
        if (routeFinderPanel != null) routeFinderPanel.applyTheme(dark);
        if (roomFinderPanel != null) roomFinderPanel.applyTheme(dark);
        if (adminPanel != null) adminPanel.applyTheme(dark);

        // Re-render current cards to adopt dark mode
        refreshCurrentView();
    }

    // Implementation of AdminPanel.AdminRefreshCallback
    public void refreshData() {
        dataManager.initializeData();
        campusMap = new CampusMap(dataManager);
        campusMap.setSelectionListener(loc -> onMapLocationSelected(loc));
        campusMap.setDarkMode(isDarkMode);
        mapPanel.removeAll();
        mapPanel.add(campusMap, BorderLayout.CENTER);
        mapPanel.add(mapRouteBar, BorderLayout.SOUTH);
        mapPanel.validate();
        populateStartChoices();
        routeFinderPanel.refreshChoices();
        refreshCurrentView();
    }

    // =========================================================================
    // Custom UI Components: LocationCard & SectionHeader
    // =========================================================================

    /**
     * Interactive Card for any campus location.
     * Click anywhere on the card or click 'Show Route' to view path on map.
     */
    private static class LocationCard extends Panel {
        private CampusLocation location;
        private boolean isDarkMode;
        private DashboardPanel dashboard;

        public LocationCard(CampusLocation loc, boolean isDarkMode, DashboardPanel dashboard, boolean isFavorite) {
            this.location = loc;
            this.isDarkMode = isDarkMode;
            this.dashboard = dashboard;

            Color cardBg = isDarkMode ? new Color(34, 40, 54) : Color.WHITE;

            setLayout(new BorderLayout(8, 3));
            setBackground(cardBg);

            // Left: Color category badge + text info
            Panel leftPanel = new Panel(new BorderLayout(6, 2));
            leftPanel.setBackground(cardBg);

            // Text Info: Row 1 = Name + Category, Row 2 = Building & Timings
            Panel textPanel = new Panel(new GridLayout(2, 1, 0, 1));
            textPanel.setBackground(cardBg);

            Panel titleRow = new Panel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            titleRow.setBackground(cardBg);

            Label lblName = new Label(loc.getName() + (isFavorite ? " [Fav]" : ""));
            lblName.setFont(new Font("Arial", Font.BOLD, 12));
            lblName.setBackground(cardBg);
            lblName.setForeground(isDarkMode ? new Color(205, 225, 255) : new Color(20, 45, 90));

            Label lblCat = new Label("[" + loc.getCategory() + "]");
            lblCat.setFont(new Font("Arial", Font.PLAIN, 10));
            lblCat.setBackground(cardBg);
            lblCat.setForeground(isDarkMode ? new Color(145, 165, 190) : new Color(85, 100, 120));

            titleRow.add(lblName);
            titleRow.add(lblCat);

            Label lblDetails = new Label("Building: " + loc.getBuilding() + " | Floor: " + loc.getFloor() +
                                         " | Timings: " + loc.getTimings());
            lblDetails.setFont(new Font("Arial", Font.PLAIN, 10));
            lblDetails.setBackground(cardBg);
            lblDetails.setForeground(isDarkMode ? new Color(165, 175, 190) : new Color(90, 100, 115));

            textPanel.add(titleRow);
            textPanel.add(lblDetails);
            leftPanel.add(textPanel, BorderLayout.CENTER);

            // Right side buttons
            Panel actionPanel = new Panel(new FlowLayout(FlowLayout.RIGHT, 4, 3));
            actionPanel.setBackground(cardBg);

            Button btnFav = new Button(isFavorite ? "Saved" : "Save");
            btnFav.setFont(new Font("Arial", Font.PLAIN, 10));
            btnFav.setBackground(isFavorite ? new Color(210, 150, 20) : (isDarkMode ? new Color(48, 56, 74) : new Color(230, 235, 245)));
            btnFav.setForeground(isFavorite ? Color.WHITE : (isDarkMode ? Color.WHITE : Color.BLACK));
            btnFav.addActionListener(e -> dashboard.toggleFavorite(loc));

            Button btnRoute = new Button("Show Route");
            btnRoute.setFont(new Font("Arial", Font.BOLD, 11));
            btnRoute.setBackground(new Color(35, 120, 215));
            btnRoute.setForeground(Color.WHITE);
            btnRoute.addActionListener(e -> dashboard.showRouteToLocation(loc));

            actionPanel.add(btnFav);

            // If it's a building or has floor info, add Indoor Plan button
            if (loc.getCategory().contains("Building") || loc.getBuilding().contains("Block")) {
                Button btnPlan = new Button("Floor Plan");
                btnPlan.setFont(new Font("Arial", Font.PLAIN, 10));
                btnPlan.setBackground(new Color(139, 92, 246));
                btnPlan.setForeground(Color.WHITE);
                btnPlan.addActionListener(e -> dashboard.showIndoorPlanForLocation(loc));
                actionPanel.add(btnPlan);
            }

            actionPanel.add(btnRoute);

            add(leftPanel, BorderLayout.CENTER);
            add(actionPanel, BorderLayout.EAST);

            // Click listener on entire card
            MouseAdapter clickListener = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    dashboard.showRouteToLocation(loc);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            };

            addMouseListener(clickListener);
            leftPanel.addMouseListener(clickListener);
            textPanel.addMouseListener(clickListener);
            titleRow.addMouseListener(clickListener);
            lblName.addMouseListener(clickListener);
            lblCat.addMouseListener(clickListener);
            lblDetails.addMouseListener(clickListener);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(450, 48);
        }
    }

    /**
     * Visual Section Header separating categories in the Overview list.
     */
    private static class SectionHeader extends Panel {
        public SectionHeader(String title, int count, boolean isDarkMode, Runnable onViewOnly) {
            Color secBg = isDarkMode ? new Color(25, 30, 42) : new Color(225, 232, 244);
            setLayout(new BorderLayout());
            setBackground(secBg);

            Label lbl = new Label("  " + title.toUpperCase() + " (" + count + ")");
            lbl.setFont(new Font("Arial", Font.BOLD, 11));
            lbl.setBackground(secBg);
            lbl.setForeground(isDarkMode ? new Color(175, 200, 240) : new Color(30, 50, 90));
            add(lbl, BorderLayout.WEST);

            if (onViewOnly != null) {
                Button btn = new Button("View Only " + title);
                btn.setFont(new Font("Arial", Font.PLAIN, 10));
                btn.setBackground(isDarkMode ? new Color(38, 46, 62) : new Color(240, 244, 252));
                btn.setForeground(isDarkMode ? new Color(200, 220, 245) : new Color(30, 50, 85));
                btn.addActionListener(e -> onViewOnly.run());
                add(btn, BorderLayout.EAST);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(450, 26);
        }
    }
}
