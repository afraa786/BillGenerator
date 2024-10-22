import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    Connection conn;

    // Constructor to set up the login window
    public LoginFrame() {
        setTitle("Login");

        // Create components for login
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Go to Register");

        // Set layout and add components
        setLayout(new GridLayout(4, 2));
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(new JLabel()); // Empty space
        add(loginButton);
        add(new JLabel()); // Empty space
        add(registerButton);

        // Set frame properties
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Connect to database
        connectToDB();

        // Action for the login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                login(username, password);
            }
        });

        // Action for the register button (switch to RegisterFrame)
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Close current login window and open register window
                dispose();
                new RegisterFrame();
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

    // Login method
    public void login(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Login successful!");
                // Proceed to the billing system after login
                new BillingFrame();
                dispose(); // Close the login window
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
