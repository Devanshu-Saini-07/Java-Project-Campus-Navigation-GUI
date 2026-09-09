import java.awt.*;
import java.awt.event.*;

public class CampusNavigation extends Frame implements ActionListener {

    Label loginUsernameLabel, loginPasswordLabel, loginMessage;
    TextField loginUsernameField, loginPasswordField;
    Button loginButton;
    Label title, searchLabel, status;
    TextField searchField;
    Button searchBtn, buildingBtn, deptBtn, hostelBtn, labBtn, busBtn;
    TextArea display;

    CampusNavigation() {
        setTitle("AWT Login Form");
        setSize(350, 300);
        setLayout(null);

        loginUsernameLabel = new Label("Username:");
        loginPasswordLabel = new Label("Password:");
        loginMessage = new Label("");
        loginUsernameField = new TextField();
        loginPasswordField = new TextField();
        loginPasswordField.setEchoChar('*');
        loginButton = new Button("Login");

        loginUsernameLabel.setBounds(50, 50, 80, 30);
        loginUsernameField.setBounds(150, 50, 120, 30);
        loginPasswordLabel.setBounds(50, 100, 80, 30);
        loginPasswordField.setBounds(150, 100, 120, 30);
        loginButton.setBounds(100, 160, 80, 30);
        loginMessage.setBounds(50, 210, 250, 30);

        add(loginUsernameLabel);
        add(loginUsernameField);
        add(loginPasswordLabel);
        add(loginPasswordField);
        add(loginButton);
        add(loginMessage);
        loginButton.addActionListener(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void showNavigation() {
        removeAll();

        setTitle("Campus Navigation Information Management System");
        setSize(700, 500);
        setBackground(new Color(230, 240, 255));

        // Heading
        title = new Label("Campus Navigation Information Management System");
        title.setBounds(120, 40, 500, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title);

        // Search Label
        searchLabel = new Label("Search Location:");
        searchLabel.setBounds(50, 100, 110, 25);
        add(searchLabel);

        // Search TextField
        searchField = new TextField();
        searchField.setBounds(170, 100, 250, 25);
        add(searchField);

        // Search Button
        searchBtn = new Button("Search");
        searchBtn.setBounds(440, 100, 80, 25);
        searchBtn.addActionListener(this);
        add(searchBtn);

        // Menu Buttons
        buildingBtn = new Button("Buildings");
        buildingBtn.setBounds(50, 150, 90, 30);
        buildingBtn.addActionListener(this);
        add(buildingBtn);

        deptBtn = new Button("Departments");
        deptBtn.setBounds(150, 150, 100, 30);
        deptBtn.addActionListener(this);
        add(deptBtn);

        hostelBtn = new Button("Hostels");
        hostelBtn.setBounds(260, 150, 90, 30);
        hostelBtn.addActionListener(this);
        add(hostelBtn);

        labBtn = new Button("Labs");
        labBtn.setBounds(360, 150, 90, 30);
        labBtn.addActionListener(this);
        add(labBtn);

        busBtn = new Button("Bus Stops");
        busBtn.setBounds(460, 150, 90, 30);
        busBtn.addActionListener(this);
        add(busBtn);

        // Display Area
        display = new TextArea();
        display.setBounds(50, 210, 600, 180);
        display.setEditable(false);
        add(display);

        // Status
        status = new Label("Status: Welcome to Campus Navigation System");
        status.setBounds(50, 420, 500, 20);
        add(status);

        validate();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == loginButton) {
            String user = loginUsernameField.getText();
            String pass = loginPasswordField.getText();

            if (user.equals("admin") && pass.equals("123")) {
                showNavigation();
            } else {
                loginMessage.setText("Invalid username or password.");
            }
        }

        else if (e.getSource() == buildingBtn) {
            display.setText(
                "Buildings Available:\n\n" +
                "1. Admin Block\n" +
                "2. Academic Block A\n" +
                "3. Academic Block B\n" +
                "4. Library\n" +
                "5. Auditorium");
        }

        else if (e.getSource() == deptBtn) {
            display.setText(
                "Departments:\n\n" +
                "- Computer Science\n" +
                "- Information Technology\n" +
                "- Mechanical Engineering\n" +
                "- Civil Engineering\n" +
                "- Electronics");
        }

        else if (e.getSource() == hostelBtn) {
            display.setText(
                "Hostels:\n\n" +
                "Boys Hostel A\n" +
                "Boys Hostel B\n" +
                "Girls Hostel\n" +
                "Guest House");
        }

        else if (e.getSource() == labBtn) {
            display.setText(
                "Laboratories:\n\n" +
                "Programming Lab\n" +
                "Networking Lab\n" +
                "AI Lab\n" +
                "Electronics Lab");
        }

        else if (e.getSource() == busBtn) {
            display.setText(
                "Bus Stops:\n\n" +
                "Main Gate\n" +
                "Library Stop\n" +
                "Hostel Stop\n" +
                "Sports Complex");
        }

        else if (e.getSource() == searchBtn) {

            String location = searchField.getText();

            if(location.equalsIgnoreCase("Library"))
                display.setText("Library\n\nLocation: Near Academic Block B");
            else if(location.equalsIgnoreCase("Hostel"))
                display.setText("Hostel\n\nLocation: Behind Sports Complex");
            else if(location.equalsIgnoreCase("CSE"))
                display.setText("Computer Science Department\n\nLocation: 2nd Floor, Academic Block A");
            else
                display.setText("Location not found.");
        }

        status.setText("Status: Ready");
    }

    public static void main(String args[]) {
        new CampusNavigation();
    }
}