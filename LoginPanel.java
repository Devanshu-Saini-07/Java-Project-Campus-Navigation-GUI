import java.awt.*;
import java.awt.event.*;

/**
 * Clean AWT Login Panel with absolute positioning for simplicity.
 * Uses null layout but keeps component ordering clear.
 */
public class LoginPanel extends Panel implements ActionListener {

    private CampusNavigation parent;
    private TextField usernameField;
    private TextField passwordField;
    private Label messageLabel;
    private Button loginButton;
    private Button exitButton;
    private Label lblTitle;
    private Label lblSubtitle;
    private Label lblUser;
    private Label lblPass;

    public LoginPanel(CampusNavigation parent) {
        this.parent = parent;
        setLayout(null); // Use null layout for precise positioning
        setBackground(new Color(250, 252, 254));

        // Initialize components
        lblTitle = new Label("Campus Navigation System");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(40, 60, 120));
        lblTitle.setAlignment(Label.CENTER);

        lblSubtitle = new Label("Please login to access the campus map and routing tools");
        lblSubtitle.setFont(new Font("Arial", Font.ITALIC, 12));
        lblSubtitle.setForeground(new Color(100, 100, 100));
        lblSubtitle.setAlignment(Label.CENTER);

        lblUser = new Label("Username:");
        lblUser.setFont(new Font("Arial", Font.PLAIN, 12));

        lblPass = new Label("Password:");
        lblPass.setFont(new Font("Arial", Font.PLAIN, 12));

        usernameField = new TextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 12));

        passwordField = new TextField(20);
        passwordField.setEchoChar('*');
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                passwordField.setSelectionStart(0);
                passwordField.setSelectionEnd(passwordField.getText().length());
            }
        });

        messageLabel = new Label(" ");
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        messageLabel.setForeground(new Color(180, 0, 0));
        messageLabel.setAlignment(Label.CENTER);

        loginButton = new Button("Login");
        loginButton.setBackground(new Color(40, 120, 200));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 13));
        loginButton.addActionListener(this);

        exitButton = new Button("Exit");
        exitButton.setBackground(new Color(200, 60, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("Arial", Font.BOLD, 13));
        exitButton.addActionListener(this);

        // Add all components to panel
        add(lblTitle);
        add(lblSubtitle);
        add(lblUser);
        add(usernameField);
        add(lblPass);
        add(passwordField);
        add(messageLabel);
        add(loginButton);
        add(exitButton);

        // Enter key support
        usernameField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        // Now position components based on the panel's size
        int centerX = width / 2;
        int startY = height / 4;
        int componentWidth = 180;
        int fieldWidth = 160;
        int spacing = 30;

        lblTitle.setBounds(centerX - 150, startY, 300, 30);
        lblSubtitle.setBounds(centerX - 150, startY + 35, 300, 25);
        lblUser.setBounds(centerX - fieldWidth/2 - 20, startY + 80, 80, 25);
        usernameField.setBounds(centerX - fieldWidth/2 + 60, startY + 80, fieldWidth, 25);
        lblPass.setBounds(centerX - fieldWidth/2 - 20, startY + 80 + spacing, 80, 25);
        passwordField.setBounds(centerX - fieldWidth/2 + 60, startY + 80 + spacing, fieldWidth, 25);
        messageLabel.setBounds(centerX - 150, startY + 80 + 2*spacing, 300, 25);
        loginButton.setBounds(centerX - 80, startY + 80 + 3*spacing, 80, 30);
        exitButton.setBounds(centerX, startY + 80 + 3*spacing, 80, 30);
    }

    private void performLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.trim().isEmpty() || pass.trim().isEmpty()) {
            messageLabel.setText("Please enter both Username and Password.");
            return;
        }

        if (user.equals("admin") && pass.equals("123")) {
            messageLabel.setText(" ");
            parent.onLoginSuccess();
        } else {
            messageLabel.setText("Invalid username or password.");
            passwordField.setText("");
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