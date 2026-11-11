package com.campusnavigation.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import com.campusnavigation.model.CampusLocation;
import com.campusnavigation.repository.CampusDataManager;
import com.campusnavigation.service.RouteFinder;

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

    private Panel topPanel;
    private Panel topWrapper;
    private Panel btnPanel;
    private Panel centerPanel;
    private Panel centerWrapper;
    private Label lblFrom;
    private Label lblTo;
    private Label lblDetails;

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
        topPanel = new Panel(new GridLayout(3, 2, 10, 10));
        topPanel.setBackground(new Color(245, 248, 252));
        topWrapper = new Panel(new BorderLayout());
        topWrapper.add(topPanel, BorderLayout.CENTER);

        lblFrom = new Label("From Location:");
        lblTo = new Label("To Location:");
        topPanel.add(lblFrom);
        topPanel.add(sourceChoice);
        topPanel.add(lblTo);
        topPanel.add(destChoice);

        btnPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(new Color(245, 248, 252));
        btnPanel.add(findRouteBtn);
        btnPanel.add(clearBtn);
        Panel spacer = new Panel();
        spacer.setBackground(new Color(245, 248, 252));
        topPanel.add(spacer);
        topPanel.add(btnPanel);

        // Center: Results
        centerPanel = new Panel(new BorderLayout(5, 5));
        centerWrapper = new Panel(new BorderLayout());
        centerWrapper.add(centerPanel, BorderLayout.CENTER);
        lblDetails = new Label("Route Details:", Label.LEFT);
        centerPanel.add(lblDetails, BorderLayout.NORTH);
        centerPanel.add(resultArea, BorderLayout.CENTER);

        add(topWrapper, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);

        populateChoices();
    }

    public void applyTheme(boolean dark) {
        Color bg = dark ? new Color(28, 34, 46) : Color.WHITE;
        Color panelBg = dark ? new Color(34, 40, 54) : new Color(245, 248, 252);
        Color fg = dark ? Color.WHITE : Color.BLACK;
        Color textBg = dark ? new Color(22, 26, 36) : Color.WHITE;

        setBackground(bg);
        if (topPanel != null) topPanel.setBackground(panelBg);
        if (topWrapper != null) topWrapper.setBackground(panelBg);
        if (btnPanel != null) btnPanel.setBackground(panelBg);
        if (centerPanel != null) centerPanel.setBackground(bg);
        if (centerWrapper != null) centerWrapper.setBackground(bg);
        if (lblFrom != null) {
            lblFrom.setBackground(panelBg);
            lblFrom.setForeground(fg);
        }
        if (lblTo != null) {
            lblTo.setBackground(panelBg);
            lblTo.setForeground(fg);
        }
        if (lblDetails != null) {
            lblDetails.setBackground(bg);
            lblDetails.setForeground(fg);
        }
        if (sourceChoice != null) {
            sourceChoice.setBackground(dark ? new Color(38, 44, 58) : Color.WHITE);
            sourceChoice.setForeground(dark ? Color.WHITE : Color.BLACK);
        }
        if (destChoice != null) {
            destChoice.setBackground(dark ? new Color(38, 44, 58) : Color.WHITE);
            destChoice.setForeground(dark ? Color.WHITE : Color.BLACK);
        }
        if (resultArea != null) {
            resultArea.setBackground(textBg);
            resultArea.setForeground(fg);
        }
    }

    public void selectLocations(String srcName, String destName) {
        for (int i = 0; i < sourceChoice.getItemCount(); i++) {
            if (sourceChoice.getItem(i).startsWith(srcName)) {
                sourceChoice.select(i);
                break;
            }
        }
        for (int i = 0; i < destChoice.getItemCount(); i++) {
            if (destChoice.getItem(i).startsWith(destName)) {
                destChoice.select(i);
                break;
            }
        }
        findShortestRoute();
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
