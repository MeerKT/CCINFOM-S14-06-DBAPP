package View;

import Model.AccountType;
import Model.LoanOptions;
import Model.TransactionHistory;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class EmployeeOptions extends JFrame {
    public static void showOptions() {
        JFrame frame = new JFrame("Employee Options");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1, 10, 10));

        JButton addLoanButton = new JButton("Add Loan Options");
        JButton viewAccountTypesButton = new JButton("View Account Types");
        JButton viewCustomersButton = new JButton("View Customers");
        JButton viewTransactionHistoryButton = new JButton("View Transaction History");
        JButton viewLoanHistoryButton = new JButton("View Loan Payment History");
        JButton viewAnnualTransactionButton = new JButton("View Annual Transaction Volume");
        JButton viewAnnualLoanButton = new JButton("View Annual Loan Payment Volume");
        
        addLoanButton.addActionListener(e -> addLoanOptions());
        viewAccountTypesButton.addActionListener(e -> AccountType.showAccountTypes());
        viewCustomersButton.addActionListener(e -> showCustomersOfBank());
        viewTransactionHistoryButton.addActionListener(e -> TransactionHistory.viewTransactionHistoryOfAccount());
        viewLoanHistoryButton.addActionListener(e -> TransactionHistory.viewLoanPaymentHistoryOfAccount());
        viewAnnualTransactionButton.addActionListener(e -> TransactionHistory.generateAnnualTransaction());
        viewAnnualLoanButton.addActionListener(e -> TransactionHistory.generateAnnualLoanPayment());
        
        panel.add(addLoanButton);
        panel.add(viewAccountTypesButton);
        panel.add(viewCustomersButton);
        panel.add(viewTransactionHistoryButton);
        panel.add(viewLoanHistoryButton);
        panel.add(viewAnnualTransactionButton);
        panel.add(viewAnnualLoanButton);
        
        frame.add(panel);
        frame.setVisible(true);
    }
    
    private static void addLoanOptions() {
        String loanType = JOptionPane.showInputDialog("Enter Loan Type:");
        String interestRateStr = JOptionPane.showInputDialog("Enter Interest Rate (decimal):");
        String loanDurStr = JOptionPane.showInputDialog("Enter Loan Duration (months):");
        String maxLoanStr = JOptionPane.showInputDialog("Enter Max Loan Price:");
        String minLoanStr = JOptionPane.showInputDialog("Enter Minimum Loan Price:");
        
        if (loanType != null && interestRateStr != null && loanDurStr != null && maxLoanStr != null && minLoanStr != null) {
            try {
                double interestRate = Double.parseDouble(interestRateStr);
                int loanDur = Integer.parseInt(loanDurStr);
                double maxLoan = Double.parseDouble(maxLoanStr);
                double minLoan = Double.parseDouble(minLoanStr);
                LoanOptions.addLoanOption(loanType, interestRate, loanDur, maxLoan, minLoan);
                JOptionPane.showMessageDialog(null, "Loan Option Added Successfully!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid Input. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void showCustomersOfBank() {
        String url = "jdbc:mysql://localhost:3307/dbapp_bankdb";
        String user = "root";
        String password = "1234";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String getInfoQuery = "SELECT customer_ID, first_name, last_name FROM customer_records ORDER BY last_name ASC, first_name ASC";
            PreparedStatement preparedStatementInfo = connection.prepareStatement(getInfoQuery);
            ResultSet res = preparedStatementInfo.executeQuery();

            List<String[]> customerData = new ArrayList<>();

            while (res.next()) {
                String customerID = res.getString("customer_ID");
                String firstName = res.getString("first_name");
                String lastName = res.getString("last_name");

                customerData.add(new String[]{customerID, lastName, firstName});
            }

            if (customerData.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No customers found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create Table Model
            String[] columnNames = {"Customer ID", "Last Name", "First Name"};
            DefaultTableModel model = new DefaultTableModel(customerData.toArray(new Object[0][]), columnNames);
            JTable table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            // Add selection listener to display customer details
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                        String selectedCustomerID = (String) table.getValueAt(table.getSelectedRow(), 0);

                        try {
                            // Fetch and display customer details
                            String viewQuery = "SELECT * FROM customer_records WHERE customer_ID = ?";
                            try (PreparedStatement statement = connection.prepareStatement(viewQuery)) {
                                statement.setString(1, selectedCustomerID);
                                ResultSet typeRes = statement.executeQuery();

                                if (typeRes.next()) {
                                    String customerInfo = "Customer ID: " + typeRes.getString("customer_ID") + "\n" +
                                            "First Name: " + typeRes.getString("first_name") + "\n" +
                                            "Last Name: " + typeRes.getString("last_name") + "\n" +
                                            "Birth Date: " + typeRes.getString("birthdate") + "\n" +
                                            "Phone No: " + typeRes.getString("phone_number") + "\n" +
                                            "Email Address: " + typeRes.getString("email_address");

                                    JOptionPane.showMessageDialog(null, customerInfo, "Customer Details", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Customer record doesn't exist", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error fetching customer details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });

            // Show table in scrollable pane
            JOptionPane.showMessageDialog(null, new JScrollPane(table), "Customer Records", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}