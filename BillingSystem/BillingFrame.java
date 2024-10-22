import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class BillingFrame extends JFrame {

    private Connection conn;  // Reuse the same connection
    private DefaultTableModel tableModel;  // Model for updating the JTable

    // Constructor
    public BillingFrame() {
        setTitle("Billing System");

        // Components for adding products
        JLabel productLabel = new JLabel("Product Name:");
        JTextField productField = new JTextField(20);
        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField(10);
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField(5);
        JButton addButton = new JButton("Add Product");

        // Table to display added products
        String[] columns = {"Product", "Price", "Quantity", "Total"};
        tableModel = new DefaultTableModel(columns, 0);  // Set up table model
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton generateBillButton = new JButton("Generate Bill");

        // Layout
        setLayout(new GridLayout(5, 2));
        add(productLabel);
        add(productField);
        add(priceLabel);
        add(priceField);
        add(quantityLabel);
        add(quantityField);
        add(addButton);
        add(new JLabel()); // Empty space
        add(scrollPane);
        add(generateBillButton);

        setSize(600, 400);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Database connection
        connectToDB();

        // Add button action
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = productField.getText();
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                double total = price * quantity;
                addProduct(name, price, quantity, total);

                // Add product to the table
                tableModel.addRow(new Object[]{name, price, quantity, total});

                // Clear input fields
                productField.setText("");
                priceField.setText("");
                quantityField.setText("");
            }
        });

        // Generate bill action
        generateBillButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateBill();
            }
        });
    }

    // Connect to SQLite database
    public void connectToDB() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:billing_system.db");
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Add product to the database
    public void addProduct(String name, double price, int quantity, double total) {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO products(name, price, quantity, total) VALUES(?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, total);
            pstmt.executeUpdate();
            System.out.println("Product added: " + name);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            // Close the PreparedStatement to prevent locking
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Generate bill (simple console print for now)
    public void generateBill() {
        Statement stmt = null;
        try {
            String query = "SELECT * FROM products";
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("----- Invoice -----");
            while (rs.next()) {
                System.out.println("Product: " + rs.getString("name"));
                System.out.println("Price: " + rs.getDouble("price"));
                System.out.println("Quantity: " + rs.getInt("quantity"));
                System.out.println("Total: " + rs.getDouble("total"));
                System.out.println("------------------");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            // Close the Statement and ResultSet to prevent locking
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new BillingFrame();
    }
}
