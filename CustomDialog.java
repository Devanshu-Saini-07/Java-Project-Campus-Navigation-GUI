import java.awt.*;
import java.awt.event.*;

/**
 * Custom AWT Modal Dialog for displaying user-friendly alerts, errors, and confirmations.
 * Adheres strictly to Java AWT without relying on Swing JOptionPane.
 */
public class CustomDialog extends Dialog implements ActionListener {
    private Button okButton;

    public CustomDialog(Frame parent, String title, String message, boolean isError) {
        super(parent, title, true);
        setLayout(new BorderLayout(10, 10));
        setSize(420, 200);
        setLocationRelativeTo(parent);
        setBackground(new Color(245, 248, 252));

        // Header Panel
        Panel headerPanel = new Panel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        headerPanel.setBackground(isError ? new Color(220, 53, 69) : new Color(13, 110, 253));
        Label headerLabel = new Label(isError ? "[!] Alert / Error" : "[i] Information");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Content Area
        Panel contentPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        Label messageLabel = new Label(message);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        messageLabel.setForeground(new Color(33, 37, 41));
        contentPanel.add(messageLabel);
        add(contentPanel, BorderLayout.CENTER);

        // Button Panel
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        okButton = new Button("  OK  ");
        okButton.setBackground(new Color(230, 230, 230));
        okButton.setFont(new Font("Arial", Font.BOLD, 12));
        okButton.addActionListener(this);
        buttonPanel.add(okButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            dispose();
        }
    }

    public static void showMessage(Frame parent, String title, String message) {
        CustomDialog dialog = new CustomDialog(parent, title, message, false);
        dialog.setVisible(true);
    }

    public static void showError(Frame parent, String title, String message) {
        CustomDialog dialog = new CustomDialog(parent, title, message, true);
        dialog.setVisible(true);
    }
}
