import java.awt.*;
import java.awt.event.*;
import java.util.*;

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

    public AdminPanel(CampusDataManager dataManager, AdminRefreshCallback callback) {
        this.dataManager = dataManager;
        this.refreshCallback = callback;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        buildComponents();
    }

    private void buildComponents() {
        // Left: Input Form
        Panel formPanel = new Panel(new GridLayout(11, 2, 5, 5));
        formPanel.setBackground(new Color(245, 248, 252));

        formPanel.add(new Label("Location Name:"));
        txtName = new TextField();
        formPanel.add(txtName);

        formPanel.add(new Label("Category:"));
        txtCategory = new TextField();
        formPanel.add(txtCategory);

        formPanel.add(new Label("Building:"));
        txtBuilding = new TextField();
        formPanel.add(txtBuilding);

        formPanel.add(new Label("Floor:"));
        txtFloor = new TextField();
        formPanel.add(txtFloor);

        formPanel.add(new Label("Description:"));
        txtDesc = new TextField();
        formPanel.add(txtDesc);

        formPanel.add(new Label("Nearby Places:"));
        txtNearby = new TextField();
        formPanel.add(txtNearby);

        formPanel.add(new Label("Timings:"));
        txtTimings = new TextField();
        formPanel.add(txtTimings);

        formPanel.add(new Label("X Coordinate (map):"));
        txtX = new TextField();
        formPanel.add(txtX);

        formPanel.add(new Label("Y Coordinate (map):"));
        txtY = new TextField();
        formPanel.add(txtY);

        formPanel.add(new Label("Aliases (comma separated):"));
        txtAliases = new TextField();
        formPanel.add(txtAliases);

        // Buttons
        Panel btnPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 5));
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

        formPanel.add(new Label("") );
        formPanel.add(btnPanel);

        // Right: Info Area
        infoArea = new TextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

        // Top: Selection dropdown
        Panel topSelection = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topSelection.setBackground(new Color(245, 248, 252));
        topSelection.add(new Label("Select Existing Location:"));
        locChoice = new Choice();
        locChoice.add("-- Select --");
        refreshLocationChoice();
        topSelection.add(locChoice);

        add(topSelection, BorderLayout.NORTH);
        add(formPanel, BorderLayout.WEST);
        add(infoArea, BorderLayout.CENTER);

        refreshInfo();
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