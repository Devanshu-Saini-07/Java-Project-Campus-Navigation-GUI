import java.awt.*;
import java.awt.event.*;

/**
 * Room Finder Panel - Allows user to search for specific rooms (101, CSE-201, LAB-01, etc.)
 */
public class RoomFinderPanel extends Panel implements ActionListener {

    private CampusDataManager dataManager;
    private TextField roomQueryField;
    private Button searchBtn;
    private Button clearBtn;
    private TextArea resultArea;

    public RoomFinderPanel(CampusDataManager dataManager) {
        this.dataManager = dataManager;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        initializeComponents();
        buildLayout();
    }

    private void initializeComponents() {
        roomQueryField = new TextField();
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

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    private void buildLayout() {
        // Top: Input
        Panel inputPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(new Color(245, 248, 252));
        inputPanel.add(new Label("Enter Room Number / Code (e.g. 101, CSE-201, LAB-01):"));
        inputPanel.add(roomQueryField);
        inputPanel.add(searchBtn);
        inputPanel.add(clearBtn);

        // Center: Results
        Panel resultPanel = new Panel(new BorderLayout(5, 5));
        resultPanel.add(new Label("Room Finder Results:", Label.LEFT), BorderLayout.NORTH);
        resultPanel.add(resultArea, BorderLayout.CENTER);

        add(inputPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchBtn || e.getSource() == roomQueryField) {
            searchRoom();
        } else if (e.getSource() == clearBtn) {
            clearFields();
        }
    }

    private void searchRoom() {
        String query = roomQueryField.getText().trim();
        if (query.isEmpty()) {
            resultArea.setText("Please enter a room number or code to search (e.g. 101, CSE-201, LAB-01).");
            return;
        }

        java.util.List<RoomInfo> rooms = dataManager.searchRooms(query);
        if (rooms.isEmpty()) {
            resultArea.setText("No room found for: \"" + query + "\"\n\nAvailable formats: Room numbers (101, 204), Department codes (CSE-201), or Lab codes (LAB-01).");
            return;
        }

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
        resultArea.setText("Enter a room number or code (e.g., 101, CSE-201, LAB-01) above,\nthen click 'Find Room' to retrieve building, floor, department, and details.\n\nSample data includes all major academic blocks and labs.");
    }
}