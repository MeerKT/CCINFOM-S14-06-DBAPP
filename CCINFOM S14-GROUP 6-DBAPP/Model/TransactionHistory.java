package Model;

import HelperClass.UserInput;

import java.sql.*;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistory {
    private int transaction_id, sender_id, receiver_id;
    private double amount;
    private Date transaction_date;
    private String transaction_status;

    public static void generateAccountTransactionRecord(Integer sender_id, Integer receiver_id, double amount){
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb",
                    "root",
                    "1234");

            String insert = "INSERT INTO account_transaction_history (amount, " +
                            "transaction_date, transaction_status, sender_acc_ID, receiver_acc_ID)" +
                            "VALUES (?, NOW(), ?, ?, ?)";

            try (PreparedStatement preparedStatement = con.prepareStatement(insert)) {
                preparedStatement.setDouble(1, amount);
                preparedStatement.setString(2, "success");
                if(sender_id == null && receiver_id != null){
                    preparedStatement.setNull(3,Types.INTEGER);
                    preparedStatement.setInt(4, receiver_id);
                } else if (sender_id != null && receiver_id == null){
                    preparedStatement.setInt(3,sender_id);
                    preparedStatement.setNull(4, Types.INTEGER);
                } else {
                    preparedStatement.setInt(3,sender_id);
                    preparedStatement.setInt(4, receiver_id);
                }



                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Transaction recorded!");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void generateLoanTransactionRecord(int sender_id, int receiver_id, double amount){
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb",
                    "root",
                    "1234");

            String insert = "INSERT INTO loan_transaction_history (amount, " +
                    "transaction_date, transaction_status, sender_acc_ID, receiver_loan_ID)" +
                    "VALUES (?, NOW(), ?, ?, ?)";

            try (PreparedStatement preparedStatement = con.prepareStatement(insert)) {
                preparedStatement.setDouble(1, amount);
                preparedStatement.setString(2, "success");
                preparedStatement.setInt(3,sender_id);
                preparedStatement.setInt(4, receiver_id);




                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Transaction recorded!");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void generateAnnualTransaction(){

        double totalOutgoing = 0;
        double totalIncoming = 0;
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/bank_db",
                    "root",
                    "1234"
            );

            String yearStr = JOptionPane.showInputDialog(null, "Enter year to view transactions:", "Annual Transaction Report", JOptionPane.QUESTION_MESSAGE);
            if (yearStr == null) return; 

            int year;
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid year.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Calendar calendar = GregorianCalendar.getInstance();
            calendar.set(Calendar.YEAR, year);

            String depositQuery = "SELECT SUM(amount) FROM account_transaction_history WHERE YEAR(transaction_date) = ? "+
            "AND transaction_status = 'Successful'";
            PreparedStatement incomingStatement = connection.prepareStatement(depositQuery);
            incomingStatement.setDate(1, new java.sql.Date(calendar.getTime().getTime()));
            ResultSet depositResult = incomingStatement.executeQuery();

            String withdrawQuery = "SELECT SUM(amount) FROM account_transaction_history WHERE YEAR(transaction_date) = ? " +
                    "AND transaction_status = 'Pending'";
            PreparedStatement outgoingStatement = connection.prepareStatement(withdrawQuery);
            outgoingStatement.setDate(1, new java.sql.Date(calendar.getTime().getTime()));
            ResultSet withdrawResult = outgoingStatement.executeQuery();

            if (withdrawResult.next()) {
                totalOutgoing = withdrawResult.getDouble(1);
            }

            if (depositResult.next()) {
                totalIncoming = depositResult.getDouble(1);
            }

            // Display results in a message dialog
            JOptionPane.showMessageDialog(null, 
                    "Transaction Volume for the Year " + year + ":\n\n" +
                    "Total Outgoing: PHP " + totalOutgoing + "\n" +
                    "Total Incoming: PHP " + totalIncoming, 
                    "Annual Transaction Report", 
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void generateAnnualLoanPayment() {
        double totalLoanPayment = 0;
        int totalNumberOfLoanPayment = 0;

        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb",
                    "root",
                    "1234"
            );

            // Get the year input using JOptionPane
            String yearInput = JOptionPane.showInputDialog(null, "Enter Year:", "Annual Loan Payment Report", JOptionPane.QUESTION_MESSAGE);
            
            if (yearInput == null || yearInput.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Year input is required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int year = Integer.parseInt(yearInput.trim());

            Calendar calendar = GregorianCalendar.getInstance();
            calendar.set(Calendar.YEAR, year);

            // Query for annual loan payment volume
            String getAnnualReportString = "SELECT * FROM loan_transaction_history WHERE YEAR(loan_transaction_date) = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(getAnnualReportString);
            preparedStatement.setInt(1, year);
            ResultSet reportResult = preparedStatement.executeQuery();

            while (reportResult.next()) {
                totalLoanPayment += reportResult.getDouble("loan_amount");
                totalNumberOfLoanPayment++;
            }

            // Format output message
            String reportMessage = "**Annual Loan Payment Volume Report - " + year + "**\n\n"
                                 + "**Total Loan Payment Made:** ₱" + String.format("%,.2f", totalLoanPayment) + "\n"
                                 + "**Total Number of Loan Payments:** " + totalNumberOfLoanPayment;

            // Display report in a message dialog
            JOptionPane.showMessageDialog(null, reportMessage, "Annual Loan Payment Report", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid year input. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving annual loan payment data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void generateMonthlySavings(int customer_id, String yearToGenerate) {
        double totalOutgoing = 0;
        double totalIncoming = 0;
    
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb",
                    "root",
                    "1234"
            );
    
            String accountQuery = "SELECT account_ID FROM account_records WHERE customer_ID = ?";
            PreparedStatement accountStmt = connection.prepareStatement(accountQuery);
            accountStmt.setInt(1, customer_id);
            ResultSet accountResult = accountStmt.executeQuery();
    
            while (accountResult.next()) {
                int accountId = accountResult.getInt("account_ID");
    
                String outgoingQuery = "SELECT SUM(amount) AS totalOutgoing FROM account_transaction_history " +
                        "WHERE sender_acc_id = ? AND DATE_FORMAT(transaction_date, '%Y') = ?";
                PreparedStatement outgoingStmt = connection.prepareStatement(outgoingQuery);
                outgoingStmt.setInt(1, accountId);
                outgoingStmt.setString(2, yearToGenerate);
                ResultSet outgoingResult = outgoingStmt.executeQuery();
    
                if (outgoingResult.next()) {
                    totalOutgoing += outgoingResult.getDouble("totalOutgoing");
                }
    
                String incomingQuery = "SELECT SUM(amount) AS totalIncoming FROM account_transaction_history " +
                        "WHERE receiver_acc_ID = ? AND DATE_FORMAT(transaction_date, '%Y') = ?";
                PreparedStatement incomingStmt = connection.prepareStatement(incomingQuery);
                incomingStmt.setInt(1, accountId);
                incomingStmt.setString(2, yearToGenerate);
                ResultSet incomingResult = incomingStmt.executeQuery();
    
                if (incomingResult.next()) {
                    totalIncoming += incomingResult.getDouble("totalIncoming");
                }
            }
    
            double yearlySavings = totalIncoming - totalOutgoing;
    
            // Format the output message
            String reportMessage = "Yearly Savings Report for " + yearToGenerate + "\n" +
                    "--------------------------------\n" +
                    "Total Incoming: PHP " + String.format("%.2f", totalIncoming) + "\n" +
                    "Total Outgoing: PHP " + String.format("%.2f", totalOutgoing) + "\n" +
                    "Net Savings: PHP " + String.format("%.2f", yearlySavings);
    
            // Display report in GUI instead of CLI
            JOptionPane.showMessageDialog(null, reportMessage, "Savings Report", JOptionPane.INFORMATION_MESSAGE);
    
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving savings report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void viewTransactionHistoryOfAccount() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb",
                    "root",
                    "1234"
            );

            // Fetch loan transaction history
            String query = "SELECT loan_transaction_ID, loan_amount, loan_transaction_date, loan_transaction_status, lender_acc_ID, borrower_acc_ID FROM loan_transaction_history ORDER BY loan_transaction_date DESC";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Store data
            List<String[]> transactionData = new ArrayList<>();
            while (resultSet.next()) {
                String transactionId = resultSet.getString("loan_transaction_ID");
                String amount = String.valueOf(resultSet.getDouble("loan_amount"));
                String date = resultSet.getString("loan_transaction_date");
                String status = resultSet.getString("loan_transaction_status");
                String lenderId = resultSet.getString("lender_acc_ID");
                String borrowerId = resultSet.getString("borrower_acc_ID");

                transactionData.add(new String[]{transactionId, amount, date, status, lenderId, borrowerId});
            }

            if (transactionData.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No loan transactions found.", "Loan Transaction History", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create table model
            String[] columnNames = {"Transaction ID", "Amount", "Date", "Status", "Lender ID", "Borrower ID"};
            DefaultTableModel model = new DefaultTableModel(transactionData.toArray(new Object[0][]), columnNames);
            JTable table = new JTable(model);

            // Show table in a scrollable pane
            JOptionPane.showMessageDialog(null, new JScrollPane(table), "Loan Transaction History", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving loan transaction history.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void viewLoanPaymentHistoryOfAccount() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb",
                    "root",
                    "1234"
            );
    
            while (true) { // Loop to allow the user to go back
                String idStr = JOptionPane.showInputDialog("Enter Account ID to view Loan Payment History:");
                if (idStr == null) return; // Exit if user cancels
    
                int accountId;
                try {
                    accountId = Integer.parseInt(idStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid Account ID. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    continue; // Restart the loop
                }
    
                // Ask for Sorting Option with "Back" button
                String[] sortOptions = {"Sort by Date", "Sort by Payment Amount", "Back"};
                int sortChoice = JOptionPane.showOptionDialog(
                        null,
                        "Choose Sorting Option:",
                        "Sort Payments",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        sortOptions,
                        sortOptions[0]
                );
    
                if (sortChoice == 2 || sortChoice == JOptionPane.CLOSED_OPTION) {
                    continue; // Go back to Account ID input
                }
    
                // Determine Sorting Order
                String orderClause = (sortChoice == 1) ? "ORDER BY loan_amount DESC" : "ORDER BY loan_transaction_date DESC";
    
                // SQL Query to Retrieve Loan Payment History
                String query = "SELECT loan_transaction_ID, loan_transaction_date, lender_acc_ID, borrower_acc_ID, loan_amount " +
                               "FROM loan_transaction_history WHERE lender_acc_ID = ? " + orderClause;
    
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, accountId);
                    ResultSet res = statement.executeQuery();
    
                    // Store Loan Payments in a List
                    List<String> payments = new ArrayList<>();
                    while (res.next()) {
                        String paymentInfo = "Payment ID: " + res.getInt("loan_transaction_ID") +
                                "\nDate: " + res.getDate("loan_transaction_date") +
                                "\nSender Acc ID: " + res.getInt("lender_acc_ID") +
                                "\nReceiver Loan ID: " + res.getInt("borrower_acc_ID") +
                                "\nAmount: PHP " + res.getDouble("loan_amount") + "\n------------------------";
                        payments.add(paymentInfo);
                    }
    
                    // Display Results
                    if (payments.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No loan payment history found for this account.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, String.join("\n", payments), "Loan Payment History", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                break; // Exit loop if everything is completed
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
