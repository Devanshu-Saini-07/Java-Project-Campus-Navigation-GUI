package com.campusnavigation.gui;

import java.awt.*;
import java.awt.event.*;
import com.campusnavigation.model.User;
import com.campusnavigation.model.UserRole;
import com.campusnavigation.repository.CampusDataManager;
import com.campusnavigation.repository.FavoriteManager;
import com.campusnavigation.repository.SearchHistory;
import com.campusnavigation.repository.UserManager;

/**
 * Main entry point for the Campus Navigation GUI application.
 * Manages user authentication, theme switching, and switching between panels using CardLayout.
 */
public class CampusNavigation extends Frame implements ActionListener {

    private CardLayout cardLayout;
    private Panel cards;
    private LoginPanel loginPanel;
    private DashboardPanel dashboardPanel;

    // Managers & Security
    private CampusDataManager dataManager;
    private FavoriteManager favoriteManager;
    private SearchHistory searchHistory;
    private UserManager userManager;
    private User currentUser;

    // Status bar
    private Panel statusBar;
    private Label statusLabel;

    public CampusNavigation() {
        super("Campus Navigation Login");

        // Initialize managers & security
        this.dataManager = new CampusDataManager();
        this.favoriteManager = new FavoriteManager();
        this.searchHistory = new SearchHistory();
        this.userManager = new UserManager();

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
        statusBar = new Panel(new BorderLayout());
        statusBar.setBackground(new Color(220, 225, 235));
        statusLabel = new Label(" Status: Ready ", Label.LEFT);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setBackground(new Color(220, 225, 235));
        statusLabel.setForeground(new Color(30, 45, 75));
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

    public UserManager getUserManager() { return userManager; }
    public User getCurrentUser() { return currentUser; }

    // From LoginPanel callback
    public void onLoginSuccess(User user) {
        this.currentUser = user;
        dashboardPanel.onUserSessionChanged(user);
        cardLayout.show(cards, "DASHBOARD");
        setTitle("Campus Navigation Dashboard - " + user.getDisplayName() + " [" + user.getRole().getTitle() + "]");
        statusLabel.setText(" Status: Logged in as " + user.getUsername() + " (" + user.getRole().getTitle() + ") | Application Ready ");
    }

    public void onLogout() {
        this.currentUser = null;
        cardLayout.show(cards, "LOGIN");
        setTitle("Campus Navigation Login");
        statusLabel.setText(" Status: Logged out ");
    }

    public void setStatus(String status) {
        statusLabel.setText(" Status: " + status + " ");
    }

    public void applyTheme(boolean darkMode) {
        Color bg = darkMode ? new Color(22, 26, 35) : new Color(240, 244, 250);
        Color statusBg = darkMode ? new Color(18, 22, 30) : new Color(220, 225, 235);
        Color statusFg = darkMode ? new Color(205, 220, 240) : new Color(30, 45, 75);

        setBackground(bg);
        if (statusBar != null) {
            statusBar.setBackground(statusBg);
        }
        if (statusLabel != null) {
            statusLabel.setBackground(statusBg);
            statusLabel.setForeground(statusFg);
        }
        if (loginPanel != null) {
            loginPanel.applyTheme(darkMode);
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