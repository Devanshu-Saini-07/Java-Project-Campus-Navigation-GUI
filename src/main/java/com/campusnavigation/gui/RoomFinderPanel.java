package com.campusnavigation.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import com.campusnavigation.model.RoomInfo;
import com.campusnavigation.repository.CampusDataManager;

/**
 * Room Finder Panel - Allows user to search for specific rooms (101, CSE-201, LAB-01, etc.)
 */
public class RoomFinderPanel extends Panel implements ActionListener {

    public interface RoomRouteListener {
        void onRouteToBuilding(String buildingName);
    }

    private CampusDataManager dataManager;
    private TextField roomQueryField;
    private Button searchBtn;
    private Button clearBtn;
    private Button routeToBuildingBtn;
    private TextArea resultArea;
    private RoomRouteListener routeListener;
    private String lastFoundBuilding;

    private Panel inputPanel;
    private Panel resultPanel;
    private Label lblPrompt;
    private Label lblResults;

    public RoomFinderPanel(CampusDataManager dataManager) {
        this.dataManager = dataManager;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        initializeComponents();
        buildLayout();
    }

    private void initializeComponents() {
        roomQueryField = new TextField(16);
        roomQueryField.addActionListener(this);

        searchBtn = new Button("Find Room");
        searchBtn.setBackground(new Color(60, 130, 70));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        searchBtn.addActionListener(this);

        clearBtn = new Button("Clear");
        clearBtn.setBackground(new Color(180, 60, 60));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearBtn.addActionListener(this);

        routeToBuildingBtn = new Button("Show Route to Building");
        routeToBuildingBtn.setBackground(new Color(35, 120, 215));
        routeToBuildingBtn.setForeground(Color.WHITE);
        routeToBuildingBtn.setFont(new Font("Arial", Font.BOLD, 12));
        routeToBuildingBtn.setVisible(false);
        routeToBuildingBtn.addActionListener(this);

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    private void buildLayout() {
        // Top: Input
        inputPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        inputPanel.setBackground(new Color(245, 248, 252));
        lblPrompt = new Label("Room Number / Code (e.g. 101, CSE-201, LAB-01):");
        inputPanel.add(lblPrompt);
        inputPanel.add(roomQueryField);
        inputPanel.add(searchBtn);
        inputPanel.add(clearBtn);
        inputPanel.add(routeToBuildingBtn);

        // Center: Results
        resultPanel = new Panel(new BorderLayout(5, 5));
        lblResults = new Label("Room Finder Results:", Label.LEFT);
        resultPanel.add(lblResults, BorderLayout.NORTH);
        resultPanel.add(resultArea, BorderLayout.CENTER);

        add(inputPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);
    }

    public void setRouteListener(RoomRouteListener listener) {
        this.routeListener = listener;
    }

    public void applyTheme(boolean dark) {
        Color bg = dark ? new Color(28, 34, 46) : Color.WHITE;
        Color inputBg = dark ? new Color(34, 40, 54) : new Color(245, 248, 252);
        Color fg = dark ? Color.WHITE : Color.BLACK;
        Color textBg = dark ? new Color(22, 26, 36) : Color.WHITE;

        setBackground(bg);
        if (inputPanel != null) inputPanel.setBackground(inputBg);
        if (resultPanel != null) resultPanel.setBackground(bg);
        if (lblPrompt != null) {
            lblPrompt.setBackground(inputBg);
            lblPrompt.setForeground(fg);
        }
        if (lblResults != null) {
            lblResults.setBackground(bg);
            lblResults.setForeground(fg);
        }
        if (roomQueryField != null) {
            roomQueryField.setBackground(dark ? new Color(38, 44, 58) : Color.WHITE);
            roomQueryField.setForeground(fg);
        }
        if (resultArea != null) {
            resultArea.setBackground(textBg);
            resultArea.setForeground(fg);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchBtn || e.getSource() == roomQueryField) {
            searchRoom();
        } else if (e.getSource() == clearBtn) {
            clearFields();
        } else if (e.getSource() == routeToBuildingBtn) {
            if (lastFoundBuilding != null && routeListener != null) {
                routeListener.onRouteToBuilding(lastFoundBuilding);
            }
        }
    }

    private void searchRoom() {
        String query = roomQueryField.getText().trim();
        if (query.isEmpty()) {
            resultArea.setText("Please enter a room number or code to search (e.g. 101, CSE-201, LAB-01).");
            routeToBuildingBtn.setVisible(false);
            return;
        }

        List<RoomInfo> rooms = dataManager.searchRooms(query);
        if (rooms.isEmpty()) {
            resultArea.setText("No room found for: \"" + query + "\"\n\nAvailable formats: Room numbers (101, 204), Department codes (CSE-201), or Lab codes (LAB-01).");
            routeToBuildingBtn.setVisible(false);
            return;
        }

        lastFoundBuilding = rooms.get(0).getBuilding();
        routeToBuildingBtn.setLabel("📍 Route to " + lastFoundBuilding);
        routeToBuildingBtn.setVisible(true);

        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(rooms.size()).append(" room(s) matching '\"");
        sb.append(query).append("':\n");
        sb.append("=====================================================\n\n");
        for (RoomInfo r : rooms) {
            sb.append(r.getFormattedDetails()).append("\n");
        }
        resultArea.setText(sb.toString());
    }

    public void clearFields() {
        roomQueryField.setText("");
        lastFoundBuilding = null;
        routeToBuildingBtn.setVisible(false);
        resultArea.setText("Enter a room number or code (e.g., 101, CSE-201, LAB-01) above,\nthen click 'Find Room' to retrieve building, floor, department, and details.\n\nSample data includes all major academic blocks and labs.");
    }
}
