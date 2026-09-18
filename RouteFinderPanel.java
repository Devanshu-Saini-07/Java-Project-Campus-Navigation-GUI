import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Route Finder Panel - Allows user to select source and destination
 * and displays the shortest path using Dijkstra's algorithm.
 */
public class RouteFinderPanel extends Panel implements ActionListener {

    public interface RouteCallback {
        void onRouteFound(List<CampusLocation> route);
    }

    private CampusDataManager dataManager;
    private Choice sourceChoice;
    private Choice destChoice;
    private Button findRouteBtn;
    private Button clearBtn;
    private TextArea resultArea;
    private RouteCallback routeCallback;

    public RouteFinderPanel(CampusDataManager dataManager) {
        this.dataManager = dataManager;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        initializeComponents();
        buildLayout();
    }

    private void initializeComponents() {
        sourceChoice = new Choice();
        destChoice = new Choice();

        findRouteBtn = new Button("Find Shortest Route");
        findRouteBtn.setBackground(new Color(60, 130, 70));
        findRouteBtn.setForeground(Color.WHITE);
        findRouteBtn.setFont(new Font("Arial", Font.BOLD, 12));
        findRouteBtn.addActionListener(this);

        clearBtn = new Button("Clear");
        clearBtn.setBackground(new Color(180, 60, 60));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearBtn.addActionListener(this);

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    private void buildLayout() {
        // Top panel with dropdowns
        Panel topPanel = new Panel(new GridLayout(3, 2, 10, 10));
        topPanel.setBackground(new Color(245, 248, 252));
        // Simple inset using an empty panel wrapper
        Panel topWrapper = new Panel(new BorderLayout());
        topWrapper.add(topPanel, BorderLayout.CENTER);

        topPanel.add(new Label("From Location:"));
        topPanel.add(sourceChoice);
        topPanel.add(new Label("To Location:"));
        topPanel.add(destChoice);

        Panel btnPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(new Color(245, 248, 252));
        btnPanel.add(findRouteBtn);
        btnPanel.add(clearBtn);
        topPanel.add(new Label("")); // spacer
        topPanel.add(btnPanel);

        // Center: Results
        Panel centerPanel = new Panel(new BorderLayout(5, 5));
        Panel centerWrapper = new Panel(new BorderLayout());
        centerWrapper.add(centerPanel, BorderLayout.CENTER);
        centerPanel.add(new Label("Route Details:", Label.LEFT), BorderLayout.NORTH);
        centerPanel.add(resultArea, BorderLayout.CENTER);

        add(topWrapper, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);

        populateChoices();
    }

    private void populateChoices() {
        sourceChoice.removeAll();
        destChoice.removeAll();

        // Add a placeholder
        sourceChoice.add("-- Select Source --");
        destChoice.add("-- Select Destination --");

        for (CampusLocation loc : dataManager.getAllLocations()) {
            String display = loc.getName() + " (" + loc.getCategory() + ")";
            sourceChoice.add(display);
            destChoice.add(display);
        }
    }

    public void refreshChoices() {
        populateChoices();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == findRouteBtn) {
            findShortestRoute();
        } else if (e.getSource() == clearBtn) {
            clearFields();
        }
    }

    private void findShortestRoute() {
        String sourceDisplay = sourceChoice.getSelectedItem();
        String destDisplay = destChoice.getSelectedItem();

        if (sourceDisplay.equals("-- Select Source --") || destDisplay.equals("-- Select Destination --")) {
            resultArea.setText("Error: Please select both Source and Destination locations.");
            return;
        }

        // Extract location name from display string (before " (")
        String sourceName = sourceDisplay.substring(0, sourceDisplay.indexOf(" (")).trim();
        String destName = destDisplay.substring(0, destDisplay.indexOf(" (")).trim();

        if (sourceName.equals(destName)) {
            resultArea.setText("Error: Source and Destination cannot be the same location.");
            return;
        }

        CampusLocation sourceLoc = dataManager.getLocationByName(sourceName);
        CampusLocation destLoc = dataManager.getLocationByName(destName);

        if (sourceLoc == null || destLoc == null) {
            resultArea.setText("Error: Could not resolve selected locations.");
            return;
        }

        // Run Dijkstra
        RouteFinder.RouteResult result = RouteFinder.findShortestPath(
            dataManager.getGraph(), sourceLoc.getId(), destLoc.getId()
        );

        if (result == null) {
            resultArea.setText("No route found between " + sourceName + " and " + destName + ".\n\nThere may be no connecting path in the current campus graph.");
            return;
        }

        // Display result
        resultArea.setText(result.getFormattedRoute());

        // Callback to highlight route on map
        if (routeCallback != null) {
            routeCallback.onRouteFound(result.getPath());
        }
    }

    public void setRouteCallback(RouteCallback callback) {
        this.routeCallback = callback;
    }

    public void clearFields() {
        sourceChoice.select(0);
        destChoice.select(0);
        resultArea.setText("Select Source and Destination locations from the dropdowns above,\nthen click 'Find Shortest Route' to calculate the optimal walking path.\n\nUses Dijkstra's Algorithm for shortest path calculation.\nDistance in meters. Walking time estimated at ~70m/minute.");
    }
}