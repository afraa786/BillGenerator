import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterFrame extends JFrame {

    Connection conn;

    // Constructor to set up the register window
    public RegisterFrame() {
        setTitle("Register");

        // Create components for registration
        JLabel newUsernameLabel = new JLabel("New Username:");
        JTextField newUsernameField = new JTextField(20);
        JLabel newPasswordLabel = new JLabel("New Password:");
        JPasswordField newPasswordField = new JPasswordField(20);
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField(20);

        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back to Login");

        // Set layout and add components
        setLayout(new GridLayout(5, 2));
        add(newUsernameLabel);
        add(newUsernameField);
        add(newPasswordLabel);
        add(newPasswordField);
        add(confirmPasswordLabel);
        add(confirmPasswordField);
        add(new JLabel()); // Empty space
        add(registerButton);
        add(new JLabel()); // Empty space
        add(backButton);

        // Set frame properties
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Connect to database
        connectToDB();

        // Action for the register button
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String user = newUsernameField.getText();
                String pass = new String(newPasswordField.getPassword());
                String confirmPass = new String(confirmPasswordField.getPassword());

                if (pass.equals(confirmPass)) {
                    register(user, pass);
                } else {
                    JOptionPane.showMessageDialog(null, "Passwords do not match!");
                }
            }
        });

        // Action for the back button (switch back to LoginFrame)
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Close the registration window and go back to login
                dispose();
                new LoginFrame();
            }
        });
    }

    // Database connection
    public void connectToDB() {
        try {
            String dbPath = getClass().getClassLoader().getResource("billing_system.db").getPath();
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Register method
    public void register(String username, String password) {
        try {
            String query = "INSERT INTO users(username, password) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Registration successful!");
            new LoginFrame();
            dispose(); // Close the register window after successful registration
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        new RegisterFrame();
    }
}
