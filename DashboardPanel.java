import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Collection;
import java.util.Set;

/**
 * Main Dashboard Panel after successful login.
 * Implements BorderLayout with:
 * - NORTH: Top Bar (Title, Search, Dark/Light Toggle, Logout)
 * - WEST:  Sidebar (Categories, Favorites, History, Emergency)
 * - CENTER: Main Content Area (CardLayout for different views)
 * - SOUTH: Status Bar
 */
public class DashboardPanel extends Panel implements ActionListener, AdminPanel.AdminRefreshCallback {

    private CampusNavigation parent;
    private CampusDataManager dataManager;
    private FavoriteManager favoriteManager;
    private SearchHistory searchHistory;

    // Top Bar Components
    private Label titleLabel;
    private TextField searchField;
    private Button searchBtn;
    private Checkbox darkModeToggle;
    private Button logoutBtn;

    // Sidebar Components
    private Button btnBuildings;
    private Button btnDepartments;
    private Button btnHostels;
    private Button btnLabs;
    private Button btnBusStops;
    private Button btnMap;
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
    private TextArea categoryDisplayArea;
    private Panel mapPanel;
    private CampusMap campusMap;
    private RouteFinderPanel routeFinderPanel;
    private RoomFinderPanel roomFinderPanel;
    private AdminPanel adminPanel;

    // State
    private boolean isDarkMode = false;
    private String currentCategory = "";

    public DashboardPanel(CampusNavigation parent, CampusDataManager dataManager,
                          FavoriteManager favoriteManager, SearchHistory searchHistory) {
        this.parent = parent;
        this.dataManager = dataManager;
        this.favoriteManager = favoriteManager;
        this.searchHistory = searchHistory;

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.LIGHT_GRAY);
        initializeComponents();
        buildLayout();
    }

    private void initializeComponents() {
        // --- Top Bar ---
        titleLabel = new Label("Campus Navigation Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 50, 90));

        searchField = new TextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));

        searchBtn = new Button("Search");
        searchBtn.setBackground(new Color(60, 130, 70));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        searchBtn.addActionListener(this);

        darkModeToggle = new Checkbox("Dark Mode");
        darkModeToggle.setFont(new Font("Arial", Font.PLAIN, 11));
        darkModeToggle.addItemListener(e -> toggleDarkMode(darkModeToggle.getState()));

        logoutBtn = new Button("Logout");
        logoutBtn.setBackground(new Color(180, 60, 60));
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
        btnRouteFinder = createSidebarButton("Route Finder");
        btnRoomFinder = createSidebarButton("Room Finder");
        btnBusRoutes = createSidebarButton("Bus Routes");
        btnFavorites = createSidebarButton("Favorites");
        btnHistory = createSidebarButton("History");
        btnEmergency = createSidebarButton("Emergency");
        btnAdmin = createSidebarButton("Admin Panel");
        btnClear = createSidebarButton("Clear/Reset");
        btnExit = createSidebarButton("Exit App");

        // --- Card Layout for Main Content ---
        cardLayout = new CardLayout();
        cardPanel = new Panel(cardLayout);

        // Category Display Panel
        categoryDisplayPanel = new Panel(new BorderLayout(5, 5));
        categoryDisplayArea = new TextArea();
        categoryDisplayArea.setEditable(false);
        categoryDisplayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        categoryDisplayPanel.add(categoryDisplayArea, BorderLayout.CENTER);

        // Map Panel
        mapPanel = new Panel(new BorderLayout());
        campusMap = new CampusMap(dataManager);
        campusMap.setSelectionListener(loc -> onMapLocationSelected(loc));
        mapPanel.add(campusMap, BorderLayout.CENTER);

        // Route Finder Panel
        routeFinderPanel = new RouteFinderPanel(dataManager);
        routeFinderPanel.setRouteCallback(locList -> {
            campusMap.setActiveRoute(locList);
            // Also switch to map tab to show route
            cardLayout.show(cardPanel, "MAP");
        });

        // Room Finder Panel
        roomFinderPanel = new RoomFinderPanel(dataManager);

        // Admin Panel
        adminPanel = new AdminPanel(dataManager, this);

        // Add cards
        cardPanel.add(categoryDisplayPanel, "CATEGORY");
        cardPanel.add(mapPanel, "MAP");
        cardPanel.add(routeFinderPanel, "ROUTE");
        cardPanel.add(roomFinderPanel, "ROOM");
        cardPanel.add(adminPanel, "ADMIN");

        // Default view
        cardLayout.show(cardPanel, "CATEGORY");
        showAllCategories();
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 11));
        btn.addActionListener(this);
        return btn;
    }

    private void buildLayout() {
        // NORTH: Top Bar
        Panel topBar = new Panel(new BorderLayout(10, 5));
        topBar.setBackground(new Color(240, 242, 248));

        // Title left
        Panel leftTop = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftTop.setBackground(new Color(240, 242, 248));
        leftTop.add(titleLabel);

        // Search center
        Panel centerTop = new Panel(new BorderLayout(5, 0));
        centerTop.setBackground(new Color(240, 242, 248));
        centerTop.add(searchField, BorderLayout.CENTER);
        centerTop.add(searchBtn, BorderLayout.EAST);

        // Right side controls
        Panel rightTop = new Panel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightTop.setBackground(new Color(240, 242, 248));
        rightTop.add(darkModeToggle);
        rightTop.add(logoutBtn);

        topBar.add(leftTop, BorderLayout.WEST);
        topBar.add(centerTop, BorderLayout.CENTER);
        topBar.add(rightTop, BorderLayout.EAST);

        // WEST: Sidebar
        Panel sidebar = new Panel(new GridLayout(0, 1, 5, 5));
        sidebar.setBackground(new Color(235, 238, 245));
        sidebar.setPreferredSize(new Dimension(180, 0));

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

        // SOUTH: Status Bar
        Panel statusBar = new Panel(new BorderLayout(5, 5));
        statusBar.setBackground(new Color(220, 225, 235));
        statusBar.setPreferredSize(new Dimension(0, 30));
        // We'll use parent's status label

        // Assemble
        add(topBar, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);
    }

    private Label createSectionLabel(String text) {
        Label lbl = new Label("  " + text);
        lbl.setFont(new Font("Arial", Font.BOLD, 9));
        lbl.setForeground(new Color(80, 80, 100));
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
            clearDisplay();
        } else if (src == btnExit) {
            System.exit(0);
        }
    }

    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            parent.setStatus("Search query cannot be empty.");
            return;
        }
        searchHistory.addSearch(query);
        List<CampusLocation> results = dataManager.searchLocations(query);
        if (results.isEmpty()) {
            categoryDisplayArea.setText("No location found for: " + query);
            parent.setStatus("Search completed: No matches.");
        } else if (results.size() == 1) {
            showLocationDetails(results.get(0));
            parent.setStatus("Found: " + results.get(0).getName());
        } else {
            displaySearchResults(results, query);
            parent.setStatus("Search completed: " + results.size() + " matches.");
        }
        cardLayout.show(cardPanel, "CATEGORY");
    }

    private void displaySearchResults(List<CampusLocation> results, String query) {
        StringBuilder sb = new StringBuilder();
        sb.append("Search Results for: \"").append(query).append("\"\n");
        sb.append("Found ").append(results.size()).append(" matches:\n\n");
        for (int i = 0; i < results.size(); i++) {
            CampusLocation loc = results.get(i);
            sb.append(String.format("%2d. %-25s [%s] - %s\n",
                i + 1, loc.getName(), loc.getCategory(), loc.getBuilding()));
        }
        categoryDisplayArea.setText(sb.toString());
    }

    private void showCategory(String category) {
        currentCategory = category;
        List<CampusLocation> locations = dataManager.getLocationsByCategory(category);
        StringBuilder sb = new StringBuilder();
        sb.append(category.toUpperCase()).append("S\n");
        sb.append("========================================\n\n");
        for (CampusLocation loc : locations) {
            String favStar = favoriteManager.isFavorite(loc.getId()) ? " ★" : "";
            sb.append("  • ").append(loc.getName()).append(favStar).append("\n");
            sb.append("     ").append(loc.getDescription()).append("\n");
            sb.append("     Building: ").append(loc.getBuilding())
              .append(" | Floor: ").append(loc.getFloor())
              .append(" | Timings: ").append(loc.getTimings()).append("\n\n");
        }
        categoryDisplayArea.setText(sb.toString());
        cardLayout.show(cardPanel, "CATEGORY");
        parent.setStatus("Showing: " + category + "s (" + locations.size() + ")");
    }

    private void showAllCategories() {
        StringBuilder sb = new StringBuilder();
        sb.append("CAMPUS OVERVIEW - ALL CATEGORIES\n");
        sb.append("========================================\n\n");
        String[] cats = {"Building", "Department", "Hostel", "Laboratory", "Bus Stop", "Facility"};
        for (String cat : cats) {
            List<CampusLocation> list = dataManager.getLocationsByCategory(cat);
            if (!list.isEmpty()) {
                sb.append(cat.toUpperCase()).append("S (").append(list.size()).append(")\n");
                for (CampusLocation loc : list) {
                    String fav = favoriteManager.isFavorite(loc.getId()) ? " ★" : "";
                    sb.append("  - ").append(loc.getName()).append(fav).append("\n");
                }
                sb.append("\n");
            }
        }
        categoryDisplayArea.setText(sb.toString());
        cardLayout.show(cardPanel, "CATEGORY");
        parent.setStatus("Campus Overview loaded. Select a category from sidebar.");
    }

    private void showMap() {
        cardLayout.show(cardPanel, "MAP");
        campusMap.clearHighlights();
        parent.setStatus("Interactive Campus Map active. Click a building to view details.");
    }

    private void onMapLocationSelected(CampusLocation loc) {
        showLocationDetails(loc);
        // Keep map visible but update details
    }

    private void showLocationDetails(CampusLocation loc) {
        StringBuilder sb = new StringBuilder();
        sb.append(loc.getDetailsFormatted()).append("\n\n");

        // Favorite button info
        if (favoriteManager.isFavorite(loc.getId())) {
            sb.append("  ★ This location is in your Favorites.\n");
        } else {
            sb.append("  ☐ Click 'Add Favorite' in sidebar to bookmark this location.\n");
        }

        categoryDisplayArea.setText(sb.toString());
        cardLayout.show(cardPanel, "CATEGORY");
        parent.setStatus("Details: " + loc.getName());
    }

    private void showRouteFinder() {
        cardLayout.show(cardPanel, "ROUTE");
        parent.setStatus("Route Finder: Select Source and Destination to find shortest path.");
    }

    private void showRoomFinder() {
        cardLayout.show(cardPanel, "ROOM");
        parent.setStatus("Room Finder: Enter room number (e.g., 101, CSE-201, LAB-01).");
    }

    private void showBusRoutes() {
        List<BusRoute> routes = dataManager.getAllBusRoutes();
        StringBuilder sb = new StringBuilder();
        sb.append("CAMPUS BUS ROUTES & TIMINGS\n");
        sb.append("========================================\n\n");
        for (BusRoute b : routes) {
            sb.append(b.getFormattedDetails()).append("\n");
        }
        categoryDisplayArea.setText(sb.toString());
        cardLayout.show(cardPanel, "CATEGORY");
        parent.setStatus("Bus Routes information displayed.");
    }

    private void showFavorites() {
        Set<String> favIds = favoriteManager.getFavoriteIds();
        StringBuilder sb = new StringBuilder();
        sb.append("YOUR FAVORITE LOCATIONS\n");
        sb.append("========================================\n\n");
        if (favIds.isEmpty()) {
            sb.append("No favorites yet. Select a location and click 'Add to Favorites'.\n");
        } else {
            for (String id : favIds) {
                CampusLocation loc = dataManager.getLocationById(id);
                if (loc != null) {
                    sb.append("  ★ ").append(loc.getName()).append(" [").append(loc.getCategory()).append("]\n");
                    sb.append("     ").append(loc.getBuilding()).append(" - ").append(loc.getTimings()).append("\n\n");
                }
            }
        }
        categoryDisplayArea.setText(sb.toString());
        cardLayout.show(cardPanel, "CATEGORY");
        parent.setStatus("Favorites loaded (" + favIds.size() + ").");
    }

    private void showHistory() {
        List<String> history = searchHistory.getHistory();
        StringBuilder sb = new StringBuilder();
        sb.append("RECENT SEARCH HISTORY\n");
        sb.append("========================================\n\n");
        if (history.isEmpty()) {
            sb.append("No recent searches.\n");
        } else {
            for (int i = 0; i < history.size(); i++) {
                sb.append(String.format("%2d. %s\n", i + 1, history.get(i)));
            }
        }
        categoryDisplayArea.setText(sb.toString());
        cardLayout.show(cardPanel, "CATEGORY");
        parent.setStatus("Search History displayed (" + history.size() + " entries).");
    }

    private void showEmergency() {
        String emergency = dataManager.getEmergencyContactsFormatted();
        categoryDisplayArea.setText(emergency);
        cardLayout.show(cardPanel, "CATEGORY");
        parent.setStatus("Emergency Contacts displayed.");
    }

    private void showAdmin() {
        cardLayout.show(cardPanel, "ADMIN");
        adminPanel.refreshTable();
        parent.setStatus("Admin Panel active. Manage campus locations.");
    }

    private void clearDisplay() {
        categoryDisplayArea.setText("");
        searchField.setText("");
        campusMap.clearHighlights();
        routeFinderPanel.clearFields();
        roomFinderPanel.clearFields();
        parent.setStatus("Display cleared. Ready for new search.");
    }

    public void addFavoriteToCurrent() {
        // This is called from outside - would need to track current selection
        // For simplicity, we use the currently displayed single location
    }

    public void toggleDarkMode(boolean dark) {
        isDarkMode = dark;
        applyTheme(dark);
        campusMap.setDarkMode(dark);
        parent.applyTheme(dark);
    }

    private void applyTheme(boolean dark) {
        Color bg = dark ? new Color(40, 45, 55) : Color.LIGHT_GRAY;
        Color fg = dark ? Color.WHITE : Color.BLACK;
        Color panelBg = dark ? new Color(35, 38, 45) : new Color(235, 238, 245);
        Color topBg = dark ? new Color(30, 33, 40) : new Color(240, 242, 248);
        Color statusBg = dark ? new Color(25, 28, 35) : new Color(220, 225, 235);

        setBackground(bg);
        setForeground(fg);

        titleLabel.setForeground(dark ? new Color(180, 200, 255) : new Color(30, 50, 90));

        // Recursively update colors - simplified for AWT
        updateComponentColors(this, dark);
    }

    private void updateComponentColors(Component comp, boolean dark) {
        if (comp instanceof Container) {
            comp.setBackground(dark ? new Color(45, 50, 60) : comp.getBackground());
            comp.setForeground(dark ? Color.WHITE : Color.BLACK);
            for (Component c : ((Container) comp).getComponents()) {
                updateComponentColors(c, dark);
            }
        } else {
            if (comp instanceof Button) {
                // Buttons keep their custom colors
            } else {
                comp.setForeground(dark ? Color.WHITE : Color.BLACK);
            }
        }
    }

    // Implementation of AdminPanel.AdminRefreshCallback
    public void refreshData() {
        dataManager.initializeData();
        campusMap = new CampusMap(dataManager);
        campusMap.setSelectionListener(loc -> onMapLocationSelected(loc));
        campusMap.setDarkMode(isDarkMode);
        mapPanel.removeAll();
        mapPanel.add(campusMap, BorderLayout.CENTER);
        mapPanel.validate();
        routeFinderPanel.refreshChoices();
    }
}