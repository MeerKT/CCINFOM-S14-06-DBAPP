package Model;

import javax.swing.*;
import java.sql.*;
import java.text.DecimalFormat;

public class InterestCalculator {
    private String url = "jdbc:mysql://localhost:3307/dbapp_bankdb";
    private String user = "root";
    private String password = "1234";

    public void calculateInterest(int accountID) {
        String query = "SELECT ar.account_type_ID, at.account_type, at.interest_rate, ar.current_balance " +
                "FROM account_records ar " +
                "JOIN account_type at ON ar.account_type_ID = at.account_type_ID " +
                "WHERE ar.account_ID = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, accountID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String accountType = rs.getString("account_type");
                double interestRate = rs.getDouble("interest_rate");
                double currentBalance = rs.getDouble("current_balance");

                // Calculate interest
                double interest = currentBalance * interestRate;

                // Format monetary values
                DecimalFormat df = new DecimalFormat("#,###.00");

                // **Display in GUI instead of console**
                String message = "**Interest Calculation Result**\n\n" +
                        "**Account Type:** " + accountType + "\n" +
                        "**Interest Rate:** " + df.format(interestRate * 100) + "%\n" +
                        "**Current Balance:** ₱" + df.format(currentBalance) + "\n" +
                        "**Calculated Interest:** ₱" + df.format(interest);

                JOptionPane.showMessageDialog(null, message, "Interest Calculation", JOptionPane.INFORMATION_MESSAGE);

            } else {
                JOptionPane.showMessageDialog(null, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}