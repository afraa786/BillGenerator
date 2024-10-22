import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class BillingFrame extends JFrame {

    private Connection conn;  // Database connection
    private DefaultTableModel tableModel;  // Model for JTable
    private JTextField productField, priceField, quantityField;  // Input fields

    // Constructor
    public BillingFrame() {
        setTitle("Billing System");
        initComponents();
        connectToDB();
    }

    // Initialize GUI Components
    private void initComponents() {
        // Input fields and labels
        JLabel productLabel = new JLabel("Product Name:");
        productField = new JTextField(20);
        JLabel priceLabel = new JLabel("Price:");
        priceField = new JTextField(10);
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityField = new JTextField(5);

        // Buttons
        JButton addButton = new JButton("Add Product");
        JButton generateBillButton = new JButton("Generate Bill");

        // Table to display added products
        String[] columns = {"Product", "Price", "Quantity", "Total"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Layout configuration
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(productLabel);
        inputPanel.add(productField);
        inputPanel.add(priceLabel);
        inputPanel.add(priceField);
        inputPanel.add(quantityLabel);
        inputPanel.add(quantityField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(generateBillButton);

        // Main panel
        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Event Listeners
        addButton.addActionListener(e -> addProductAction());
        generateBillButton.addActionListener(e -> generateBill());
    }

    // Add product to JTable and database
    private void addProductAction() {
        try {
            String name = productField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            double total = price * quantity;

            // Add product to JTable
            tableModel.addRow(new Object[]{name, price, quantity, total});

            // Save product to database
            addProductToDB(name, price, quantity, total);

            // Clear input fields
            productField.setText("");
            priceField.setText("");
            quantityField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input, please enter numbers for price and quantity.");
        }
    }

    // Connect to SQLite database
    private void connectToDB() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:billing_system.db");
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Add product to the database
    private void addProductToDB(String name, double price, int quantity, double total) {
        String sql = "INSERT INTO products(name, price, quantity, total) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, total);
            pstmt.executeUpdate();
            System.out.println("Product added: " + name);
        } catch (SQLException e) {
            System.out.println("Error adding product to database: " + e.getMessage());
        }
    }

    // Generate and save bill to a text file
    private void generateBill() {
        String query = "SELECT * FROM products";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             BufferedWriter writer = new BufferedWriter(new FileWriter("invoice.txt"))) {

            // Write invoice header
            writer.write("----- Invoice -----");
            writer.newLine();

            // Fetch data from the database and write to the file
            while (rs.next()) {
                writer.write("Product: " + rs.getString("name"));
                writer.newLine();
                writer.write("Price: " + rs.getDouble("price"));
                writer.newLine();
                writer.write("Quantity: " + rs.getInt("quantity"));
                writer.newLine();
                writer.write("Total: " + rs.getDouble("total"));
                writer.newLine();
                writer.write("------------------");
                writer.newLine();
            }

            System.out.println("Invoice generated: invoice.txt");
        } catch (SQLException | IOException e) {
            System.out.println("Error generating invoice: " + e.getMessage());
        }
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BillingFrame::new);  // Start the GUI on the Event Dispatch Thread
    }
}
