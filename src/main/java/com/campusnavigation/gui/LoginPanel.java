package com.campusnavigation.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * Enhanced College-themed Login Portal Panel.
 * Features an academic header banner, official university crest graphics,
 * an elevated card-based login box, and crisp dark/light collegiate themes.
 */
public class LoginPanel extends Panel implements ActionListener {

    private CampusNavigation parent;
    private TextField usernameField;
    private TextField passwordField;
    private Label messageLabel;
    private Button loginButton;
    private Button exitButton;

    private Label lblCardHeader;
    private Label lblUser;
    private Label lblPass;
    private Label lblHint;
    private Label lblRoleTag;

    private boolean isDarkMode = false;

    public LoginPanel(CampusNavigation parent) {
        this.parent = parent;
        setLayout(null); // Absolute positioning within the centered card
        setBackground(new Color(237, 242, 247));

        initializeComponents();
    }

    private void initializeComponents() {
        Color cardFill = Color.WHITE;

        // Card Header
        lblCardHeader = new Label("Campus Portal Sign In");
        lblCardHeader.setFont(new Font("Arial", Font.BOLD, 16));
        lblCardHeader.setForeground(new Color(25, 45, 95));
        lblCardHeader.setBackground(cardFill);
        lblCardHeader.setAlignment(Label.CENTER);

        // Form Labels
        lblUser = new Label("Student / Staff / Admin ID:");
        lblUser.setFont(new Font("Arial", Font.BOLD, 11));
        lblUser.setForeground(new Color(40, 55, 80));
        lblUser.setBackground(cardFill);

        lblPass = new Label("Security Password:");
        lblPass.setFont(new Font("Arial", Font.BOLD, 11));
        lblPass.setForeground(new Color(40, 55, 80));
        lblPass.setBackground(cardFill);

        // Text Fields
        usernameField = new TextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 13));
        usernameField.setText("admin"); // Friendly default

        passwordField = new TextField(20);
        passwordField.setEchoChar('•');
        passwordField.setFont(new Font("Arial", Font.PLAIN, 13));
        passwordField.setText("123");   // Friendly default
        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                passwordField.setSelectionStart(0);
                passwordField.setSelectionEnd(passwordField.getText().length());
            }
        });

        // Error / Status Message
        messageLabel = new Label(" ");
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        messageLabel.setForeground(new Color(195, 30, 30));
        messageLabel.setBackground(cardFill);
        messageLabel.setAlignment(Label.CENTER);

        // Action Buttons
        loginButton = new Button("Sign In to Campus Map");
        loginButton.setBackground(new Color(29, 78, 216)); // Collegiate Royal Blue
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.addActionListener(this);

        exitButton = new Button("Exit");
        exitButton.setBackground(new Color(185, 28, 28)); // Academic Crimson
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("Arial", Font.BOLD, 12));
        exitButton.addActionListener(this);

        // Footnotes
        lblHint = new Label("Roles: admin (123) | student (student123) | faculty (faculty123)");
        lblHint.setFont(new Font("Arial", Font.PLAIN, 10));
        lblHint.setForeground(new Color(95, 110, 130));
        lblHint.setBackground(cardFill);
        lblHint.setAlignment(Label.CENTER);

        lblRoleTag = new Label("Official Campus Wayfinding & Navigation Directory");
        lblRoleTag.setFont(new Font("Arial", Font.PLAIN, 10));
        lblRoleTag.setForeground(new Color(110, 125, 145));
        lblRoleTag.setBackground(cardFill);
        lblRoleTag.setAlignment(Label.CENTER);

        // Add all components
        add(lblCardHeader);
        add(lblUser);
        add(usernameField);
        add(lblPass);
        add(passwordField);
        add(messageLabel);
        add(loginButton);
        add(exitButton);
        add(lblHint);
        add(lblRoleTag);

        // Enter key navigation
        usernameField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
    }

    public void applyTheme(boolean dark) {
        this.isDarkMode = dark;
        Color bg = dark ? new Color(17, 24, 39) : new Color(237, 242, 247);
        Color fg = dark ? new Color(243, 244, 246) : new Color(30, 45, 75);
        Color tfBg = dark ? new Color(45, 55, 72) : Color.WHITE;
        Color tfFg = dark ? Color.WHITE : Color.BLACK;
        Color cardFill = dark ? new Color(31, 41, 55) : Color.WHITE;

        setBackground(bg);
        if (lblCardHeader != null) {
            lblCardHeader.setBackground(cardFill);
            lblCardHeader.setForeground(dark ? new Color(191, 219, 254) : new Color(25, 45, 95));
        }
        if (lblUser != null) {
            lblUser.setBackground(cardFill);
            lblUser.setForeground(fg);
        }
        if (lblPass != null) {
            lblPass.setBackground(cardFill);
            lblPass.setForeground(fg);
        }
        if (messageLabel != null) {
            messageLabel.setBackground(cardFill);
        }
        if (lblHint != null) {
            lblHint.setBackground(cardFill);
            lblHint.setForeground(dark ? new Color(156, 163, 175) : new Color(95, 110, 130));
        }
        if (lblRoleTag != null) {
            lblRoleTag.setBackground(cardFill);
            lblRoleTag.setForeground(dark ? new Color(140, 155, 175) : new Color(110, 125, 145));
        }
        if (usernameField != null) {
            usernameField.setBackground(tfBg);
            usernameField.setForeground(tfFg);
        }
        if (passwordField != null) {
            passwordField.setBackground(tfBg);
            passwordField.setForeground(tfFg);
        }
        repaint();
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);

        int centerX = width / 2;
        int cardW = 390;
        int cardH = 325;
        int cardX = centerX - (cardW / 2);
        int cardY = Math.max(95, (height - cardH) / 2 + 25);

        lblCardHeader.setBounds(cardX + 20, cardY + 18, 350, 26);
        lblUser.setBounds(cardX + 25, cardY + 54, 340, 18);
        usernameField.setBounds(cardX + 25, cardY + 74, 340, 27);
        lblPass.setBounds(cardX + 25, cardY + 109, 340, 18);
        passwordField.setBounds(cardX + 25, cardY + 129, 340, 27);
        messageLabel.setBounds(cardX + 20, cardY + 162, 350, 18);

        // Buttons row
        loginButton.setBounds(cardX + 25, cardY + 188, 220, 34);
        exitButton.setBounds(cardX + 255, cardY + 188, 110, 34);

        lblHint.setBounds(cardX + 20, cardY + 236, 350, 18);
        lblRoleTag.setBounds(cardX + 20, cardY + 258, 350, 18);
    }

    @Override
    public void paint(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) {
            super.paint(g);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. Overall Background
        g2d.setColor(isDarkMode ? new Color(17, 24, 39) : new Color(237, 242, 247));
        g2d.fillRect(0, 0, width, height);

        // 2. Top Academic Navy Banner
        int bannerH = 75;
        g2d.setColor(new Color(15, 30, 58));
        g2d.fillRect(0, 0, width, bannerH);

        // 3. Warm Gold Accent Line
        g2d.setColor(new Color(217, 140, 20));
        g2d.fillRect(0, bannerH, width, 4);

        // 4. University Emblem / Crest
        int crestX = Math.max(25, (width / 2) - 275);
        drawAcademicCrest(g2d, crestX, 16);

        // Banner Text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("UNIVERSITY CAMPUS NAVIGATION PORTAL", crestX + 54, 34);

        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.setColor(new Color(235, 195, 100)); // Gold subtitle
        g2d.drawString("Department of Campus Planning & Student Services • Smart Wayfinding", crestX + 54, 52);

        // 5. Centered Card Drop Shadow & Card Frame
        int centerX = width / 2;
        int cardW = 390;
        int cardH = 325;
        int cardX = centerX - (cardW / 2);
        int cardY = Math.max(95, (height - cardH) / 2 + 25);

        // Drop Shadow
        g2d.setColor(isDarkMode ? new Color(5, 8, 14, 180) : new Color(170, 185, 205, 130));
        g2d.fillRoundRect(cardX + 4, cardY + 4, cardW, cardH, 14, 14);

        // Card Fill
        g2d.setColor(isDarkMode ? new Color(31, 41, 55) : Color.WHITE);
        g2d.fillRoundRect(cardX, cardY, cardW, cardH, 14, 14);

        // Card Border
        g2d.setColor(isDarkMode ? new Color(55, 65, 81) : new Color(203, 213, 225));
        g2d.setStroke(new BasicStroke(1.4f));
        g2d.drawRoundRect(cardX, cardY, cardW, cardH, 14, 14);

        // Collegiate Top Accent Bar on Card
        g2d.setColor(new Color(29, 78, 216)); // Royal Blue
        g2d.fillRoundRect(cardX, cardY, cardW, 8, 14, 14);
        g2d.fillRect(cardX, cardY + 4, cardW, 4);

        // 6. Bottom System Footer
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(isDarkMode ? new Color(120, 135, 155) : new Color(110, 125, 145));
        String footer = "© Smart University Navigation System • DSA Shortest Path Engine • All Rights Reserved";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(footer, (width - fm.stringWidth(footer)) / 2, height - 12);

        // Super paints all child labels, textfields, and buttons on top
        super.paint(g);
    }

    /**
     * Draws a crisp vector academic crest (Shield + Mortarboard cap).
     */
    private void drawAcademicCrest(Graphics2D g2d, int x, int y) {
        // Gold Outer Ring
        g2d.setColor(new Color(217, 140, 20));
        g2d.fillOval(x, y, 42, 42);

        // Deep Navy Inner Circle
        g2d.setColor(new Color(20, 40, 75));
        g2d.fillOval(x + 2, y + 2, 38, 38);

        // Academic Cap Diamond
        g2d.setColor(Color.WHITE);
        int[] px = {x + 21, x + 34, x + 21, x + 8};
        int[] py = {y + 13, y + 20, y + 27, y + 20};
        g2d.fillPolygon(px, py, 4);

        // Skullcap Base
        int[] bx = {x + 13, x + 29, x + 26, x + 16};
        int[] by = {y + 22, y + 22, y + 29, y + 29};
        g2d.fillPolygon(bx, by, 4);

        // Gold Tassel
        g2d.setColor(new Color(250, 204, 21));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(x + 21, y + 20, x + 33, y + 24);
        g2d.fillOval(x + 32, y + 24, 3, 4);
    }

    private void performLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.trim().isEmpty() || pass.trim().isEmpty()) {
            messageLabel.setText("Please enter both ID and Password.");
            return;
        }

        com.campusnavigation.model.User authenticatedUser = parent.getUserManager().authenticate(user, pass);
        if (authenticatedUser != null) {
            messageLabel.setText(" ");
            parent.onLoginSuccess(authenticatedUser);
        } else {
            messageLabel.setText("Invalid credentials. Try admin/123 or student/student123");
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            performLogin();
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }
}
