package com.campusnavigation.gui;

import java.awt.*;
import java.awt.event.*;
import com.campusnavigation.repository.CampusDataManager;
import com.campusnavigation.repository.FavoriteManager;
import com.campusnavigation.repository.SearchHistory;

/**
 * Main entry point for the Campus Navigation GUI application.
 * Manages login, theme switching, and switching between panels using CardLayout.
 */
public class CampusNavigation extends Frame implements ActionListener {

    private CardLayout cardLayout;
    private Panel cards;
    private LoginPanel loginPanel;
    private DashboardPanel dashboardPanel;

    // Managers
    private CampusDataManager dataManager;
    private FavoriteManager favoriteManager;
    private SearchHistory searchHistory;

    // Status bar
    private Label statusLabel;

    public CampusNavigation() {
        super("Campus Navigation Login");

        // Initialize managers
        this.dataManager = new CampusDataManager();
        this.favoriteManager = new FavoriteManager();
        this.searchHistory = new SearchHistory();

        // Setup main window
        setLayout(new BorderLayout());
        setSize(800, 600);
        setBackground(Color.LIGHT_GRAY);

        // Create card layout for switching between login and dashboard
        cardLayout = new CardLayout();
        cards = new Panel(cardLayout);

        // Create login panel
        loginPanel = new LoginPanel(this);

        // Add status bar FIRST (so statusLabel exists before dashboard uses it)
        Panel statusBar = new Panel(new BorderLayout());
        statusBar.setBackground(new Color(220, 225, 235));
        statusLabel = new Label(" Status: Ready ", Label.LEFT);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusBar.add(statusLabel, BorderLayout.WEST);

        // Create dashboard
        dashboardPanel = new DashboardPanel(this, dataManager, favoriteManager, searchHistory);

        cards.add("LOGIN", loginPanel);
        cards.add("DASHBOARD", dashboardPanel);

        // Add components
        add(cards, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // Window close handler
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        // Show login screen initially
        cardLayout.show(cards, "LOGIN");

        setVisible(true);
    }

    // From LoginPanel callback
    public void onLoginSuccess() {
        cardLayout.show(cards, "DASHBOARD");
        setTitle("Campus Navigation Dashboard");
        statusLabel.setText(" Status: Logged in as admin | Application Ready ");
    }

    public void onLogout() {
        cardLayout.show(cards, "LOGIN");
        setTitle("Campus Navigation Login");
        statusLabel.setText(" Status: Logged out ");
    }

    public void setStatus(String status) {
        statusLabel.setText(" Status: " + status + " ");
    }

    public void applyTheme(boolean darkMode) {
        Color bg = darkMode ? new Color(40, 45, 55) : Color.LIGHT_GRAY;
        Color fg = darkMode ? Color.WHITE : Color.BLACK;

        setBackground(bg);

        // Update all child components recursively
        updateComponentColors(this, darkMode);
    }

    private void updateComponentColors(Component comp, boolean darkMode) {
        if (comp instanceof Container) {
            comp.setBackground(darkMode ? new Color(45, 50, 60) : Color.WHITE);
            comp.setForeground(darkMode ? Color.WHITE : Color.BLACK);
            for (Component child : ((Container) comp).getComponents()) {
                updateComponentColors(child, darkMode);
            }
        } else {
            if (comp instanceof Button) {
                // Buttons keep their custom colors
            } else {
                comp.setForeground(darkMode ? Color.WHITE : Color.BLACK);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Global actions could go here
    }

    public static void main(String[] args) {
        // Ensure we run on the Event Dispatch Thread
        EventQueue.invokeLater(() -> {
            new CampusNavigation();
        });
    }
}