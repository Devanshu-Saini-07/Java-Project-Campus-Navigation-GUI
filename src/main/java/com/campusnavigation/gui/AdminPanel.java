package com.campusnavigation.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import com.campusnavigation.model.CampusLocation;
import com.campusnavigation.repository.CampusDataManager;

/**
 * Admin Panel for managing campus locations (Add, Update, Delete, View All).
 * Uses simple forms and a Table display using AWT components.
 */
public class AdminPanel extends Panel implements ActionListener {

    public interface AdminRefreshCallback {
        void refreshData();
    }

    private CampusDataManager dataManager;
    private AdminRefreshCallback refreshCallback;

    private TextField txtName, txtCategory, txtBuilding, txtFloor, txtDesc, txtNearby, txtTimings, txtX, txtY, txtAliases;
    private Button btnAdd, btnUpdate, btnDelete, btnRefresh, btnClearForm;
    private TextArea infoArea;
    private Choice locChoice;

    private Panel formPanel;
    private Panel topSelection;
    private Panel btnPanel;
    private Label lblSelection;
    private java.util.List<Label> formLabels = new ArrayList<>();
    private java.util.List<TextField> formFields = new ArrayList<>();

    public AdminPanel(CampusDataManager dataManager, AdminRefreshCallback callback) {
        this.dataManager = dataManager;
        this.refreshCallback = callback;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        buildComponents();
    }

    private void addField(Panel p, String labelText, TextField tf) {
        Label lbl = new Label(labelText);
        formLabels.add(lbl);
        formFields.add(tf);
        p.add(lbl);
        p.add(tf);
    }

    private void buildComponents() {
        // Left: Input Form
        formPanel = new Panel(new GridLayout(11, 2, 5, 5));
        formPanel.setBackground(new Color(245, 248, 252));

        txtName = new TextField(); addField(formPanel, "Location Name:", txtName);
        txtCategory = new TextField(); addField(formPanel, "Category:", txtCategory);
        txtBuilding = new TextField(); addField(formPanel, "Building:", txtBuilding);
        txtFloor = new TextField(); addField(formPanel, "Floor:", txtFloor);
        txtDesc = new TextField(); addField(formPanel, "Description:", txtDesc);
        txtNearby = new TextField(); addField(formPanel, "Nearby Places:", txtNearby);
        txtTimings = new TextField(); addField(formPanel, "Timings:", txtTimings);
        txtX = new TextField(); addField(formPanel, "X Coordinate (map):", txtX);
        txtY = new TextField(); addField(formPanel, "Y Coordinate (map):", txtY);
        txtAliases = new TextField(); addField(formPanel, "Aliases (comma separated):", txtAliases);

        // Buttons
        btnPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(new Color(245, 248, 252));

        btnAdd = new Button("Add Location");
        btnUpdate = new Button("Update Location");
        btnDelete = new Button("Delete");
        btnRefresh = new Button("Refresh List");
        btnClearForm = new Button("Clear Form");

        btnAdd.setBackground(new Color(60, 130, 70));
        btnAdd.setForeground(Color.WHITE);
        btnUpdate.setBackground(new Color(60, 130, 140));
        btnUpdate.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(180, 60, 60));
        btnDelete.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(100, 110, 130));
        btnRefresh.setForeground(Color.WHITE);
        btnClearForm.setBackground(new Color(120, 120, 120));
        btnClearForm.setForeground(Color.WHITE);

        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);
        btnRefresh.addActionListener(this);
        btnClearForm.addActionListener(this);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnClearForm);

        Panel spacer = new Panel();
        spacer.setBackground(new Color(245, 248, 252));
        formPanel.add(spacer);
        formPanel.add(btnPanel);

        // Right: Info Area
        infoArea = new TextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

        // Top: Selection dropdown
        topSelection = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topSelection.setBackground(new Color(245, 248, 252));
        lblSelection = new Label("Select Existing Location:");
        topSelection.add(lblSelection);
        locChoice = new Choice();
        locChoice.add("-- Select --");
        refreshLocationChoice();
        topSelection.add(locChoice);

        add(topSelection, BorderLayout.NORTH);
        add(formPanel, BorderLayout.WEST);
        add(infoArea, BorderLayout.CENTER);

        refreshInfo();
    }

    public void applyTheme(boolean dark) {
        Color bg = dark ? new Color(28, 34, 46) : Color.WHITE;
        Color formBg = dark ? new Color(34, 40, 54) : new Color(245, 248, 252);
        Color fg = dark ? Color.WHITE : Color.BLACK;
        Color textBg = dark ? new Color(22, 26, 36) : Color.WHITE;

        setBackground(bg);
        if (topSelection != null) topSelection.setBackground(formBg);
        if (formPanel != null) formPanel.setBackground(formBg);
        if (btnPanel != null) btnPanel.setBackground(formBg);
        if (lblSelection != null) {
            lblSelection.setBackground(formBg);
            lblSelection.setForeground(fg);
        }

        for (Label l : formLabels) {
            l.setBackground(formBg);
            l.setForeground(fg);
        }
        for (TextField tf : formFields) {
            tf.setBackground(dark ? new Color(38, 44, 58) : Color.WHITE);
            tf.setForeground(fg);
        }
        if (locChoice != null) {
            locChoice.setBackground(dark ? new Color(38, 44, 58) : Color.WHITE);
            locChoice.setForeground(dark ? Color.WHITE : Color.BLACK);
        }
        if (infoArea != null) {
            infoArea.setBackground(textBg);
            infoArea.setForeground(fg);
        }
    }

    public void refreshTable() {
        refreshLocationChoice();
        refreshInfo();
        if (refreshCallback != null) refreshCallback.refreshData();
    }

    private void refreshLocationChoice() {
        locChoice.removeAll();
        locChoice.add("-- Select --");
        for (CampusLocation loc : dataManager.getAllLocations()) {
            locChoice.add(loc.getName() + " (ID: " + loc.getId() + ")");
        }
    }

    private void refreshInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ADMIN PANEL: CAMPUS LOCATION MANAGER ===\n\n");
        sb.append("Total Locations in Database: ").append(dataManager.getAllLocations().size()).append("\n\n");
        for (CampusLocation loc : dataManager.getAllLocations()) {
            sb.append(String.format("%-20s | %s | %s | %s\n",
                loc.getName(), loc.getCategory(), loc.getBuilding(), loc.getTimings()));
        }
        infoArea.setText(sb.toString());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnAdd) {
            addNewLocation();
        } else if (src == btnUpdate) {
            updateLocation();
        } else if (src == btnDelete) {
            deleteLocation();
        } else if (src == btnRefresh) {
            refreshTable();
        } else if (src == btnClearForm) {
            clearForm();
        }
    }

    private void addNewLocation() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            infoArea.setText("Error: Name is required to add a new location.");
            return;
        }

        String id = "LOC" + (dataManager.getAllLocations().size() + 1);
        int x = 300; // Default map coordinate
        int y = 200;
        try {
            x = Integer.parseInt(txtX.getText().trim());
            y = Integer.parseInt(txtY.getText().trim());
        } catch (NumberFormatException ignored) {}

        CampusLocation loc = new CampusLocation(
            id, name,
            txtCategory.getText().isEmpty() ? "Facility" : txtCategory.getText(),
            txtBuilding.getText(),
            txtFloor.getText(),
            txtDesc.getText(),
            txtNearby.getText(),
            txtTimings.getText(),
            x, y,
            txtAliases.getText()
        );

        if (dataManager.addLocation(loc)) {
            infoArea.setText("Successfully added: " + name + " (ID: " + id + ")\n\nData saved to campus_data.txt");
            refreshTable();
            clearForm();
        } else {
            infoArea.setText("Error: Could not add location. ID may already exist.");
        }
    }

    private void updateLocation() {
        String selectedDisplay = locChoice.getSelectedItem();
        if (selectedDisplay == null || selectedDisplay.equals("-- Select --")) {
            infoArea.setText("Error: Please select an existing location from the dropdown to update.");
            return;
        }

        // Extract ID from selection
        String id = selectedDisplay.substring(selectedDisplay.lastIndexOf("(") + 1, selectedDisplay.lastIndexOf(")"));
        id = id.replace("ID: ", "").trim();

        CampusLocation existing = dataManager.getLocationById(id);
        if (existing == null) {
            infoArea.setText("Error: Could not find location with ID: " + id);
            return;
        }

        existing.setName(txtName.getText().trim());
        existing.setCategory(txtCategory.getText());
        existing.setBuilding(txtBuilding.getText());
        existing.setFloor(txtFloor.getText());
        existing.setDescription(txtDesc.getText());
        existing.setNearby(txtNearby.getText());
        existing.setTimings(txtTimings.getText());
        existing.setAliases(txtAliases.getText());
        try {
            existing.setX(Integer.parseInt(txtX.getText().trim()));
            existing.setY(Integer.parseInt(txtY.getText().trim()));
        } catch (NumberFormatException ignored) {}

        dataManager.updateLocation(existing);
        infoArea.setText("Updated location: " + existing.getName() + " (ID: " + id + ")");
        refreshTable();
    }

    private void deleteLocation() {
        String selectedDisplay = locChoice.getSelectedItem();
        if (selectedDisplay == null || selectedDisplay.equals("-- Select --")) {
            infoArea.setText("Error: Please select a location to delete.");
            return;
        }
        String id = selectedDisplay.substring(selectedDisplay.lastIndexOf("(") + 1, selectedDisplay.lastIndexOf(")"));
        id = id.replace("ID: ", "").trim();

        if (dataManager.deleteLocation(id)) {
            infoArea.setText("Deleted location with ID: " + id);
            refreshTable();
            clearForm();
        } else {
            infoArea.setText("Error: Could not delete location.");
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtCategory.setText("");
        txtBuilding.setText("");
        txtFloor.setText("");
        txtDesc.setText("");
        txtNearby.setText("");
        txtTimings.setText("");
        txtX.setText("");
        txtY.setText("");
        txtAliases.setText("");
    }
}
