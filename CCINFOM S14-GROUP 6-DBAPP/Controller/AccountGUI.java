package Controller;

import Controller.CustomerGUI;
import Model.Customer;
import Model.Account;
import Model.AvailedLoans;
import Model.LoanOptions;
import Model.TransactionHistory;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AccountGUI {
    private JFrame frame;
    private Customer customer;

    public AccountGUI(Customer customer) { // Fixed constructor name
        this.customer = customer;
        frame = new JFrame("Account Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new FlowLayout());

        JButton viewAccountsButton = new JButton("View Accounts");
        viewAccountsButton.addActionListener(e -> showAccountSelection());

        frame.add(viewAccountsButton);
        frame.setVisible(true);
    }

    private void showAccountSelection() {
        Account.showAccounts(customer.getCustomer_id());

        String accIdStr = JOptionPane.showInputDialog(frame, "Enter Account ID to view details:");
        if (accIdStr != null && accIdStr.matches("\\d+")) {
            int acc_id = Integer.parseInt(accIdStr);
            showAccountInfo(acc_id);
        }
    }

    private void showAccountInfo(int account_id) {
        String accountInfo = getAccountDetails(account_id, customer.getCustomer_id());

        if (accountInfo == null) {
            JOptionPane.showMessageDialog(frame, "Account ID doesn't exist for this customer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog accountDialog = new JDialog(frame, "Account Details", true);
        accountDialog.setSize(400, 300);
        accountDialog.setLayout(new BorderLayout());

        JTextArea accountDetailsArea = new JTextArea(accountInfo);
        accountDetailsArea.setEditable(false);
        accountDialog.add(new JScrollPane(accountDetailsArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton transferButton = new JButton("Transfer");
        JButton closeButton = new JButton("Close Account");

        depositButton.addActionListener(e -> depositToAccount(account_id));
        withdrawButton.addActionListener(e -> withdrawFromAccount(account_id));
        transferButton.addActionListener(e -> transferToAnother(account_id));
        closeButton.addActionListener(e -> closeAccount(account_id));

        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(transferButton);
        buttonPanel.add(closeButton);

        accountDialog.add(buttonPanel, BorderLayout.SOUTH);
        accountDialog.setVisible(true);
    }

    private String getAccountDetails(int account_id, int customer_id) {
        StringBuilder accountInfo = new StringBuilder();

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/dbapp_bankdb", "root", "1234")) {
            String query = "SELECT ar.account_ID, ar.customer_ID, ar.current_balance, ar.account_type_ID, at.account_type, ar.date_opened, ar.date_closed, ar.account_status "
                         + "FROM account_records ar JOIN account_type at ON ar.account_type_ID = at.account_type_ID "
                         + "WHERE ar.account_ID = ? AND ar.customer_ID = ? AND ar.account_status = 'Active'";

            try (PreparedStatement statement = con.prepareStatement(query)) {
                statement.setInt(1, account_id);
                statement.setInt(2, customer_id);
                try (ResultSet res = statement.executeQuery()) {
                    if (res.next()) {
                        accountInfo.append("Account ID: ").append(res.getInt("account_ID")).append("\n");
                        accountInfo.append("Account Type: ").append(res.getString("account_type")).append("\n");
                        accountInfo.append("Current Balance: ").append(res.getDouble("current_balance")).append("\n");
                        accountInfo.append("Date Opened: ").append(res.getDate("date_opened")).append("\n");
                        accountInfo.append("Status: ").append(res.getString("account_status")).append("\n");
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error retrieving account details.";
        }
        return accountInfo.toString();
    }

    private void depositToAccount(int account_id) {
        String amountStr = JOptionPane.showInputDialog(frame, "Enter amount to deposit:");
        if (amountStr == null || !amountStr.matches("\\d+(\\.\\d{1,2})?")) return;

        double amount = Double.parseDouble(amountStr);

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/dbapp_bankdb", "root", "1234")) {
            String query = "UPDATE account_records SET current_balance = current_balance + ? WHERE account_ID = ? AND account_status = 'Active'";

            try (PreparedStatement statement = con.prepareStatement(query)) {
                statement.setDouble(1, amount);
                statement.setInt(2, account_id);
                if (statement.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(frame, "Deposit successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void withdrawFromAccount(int account_id) {
        String amountStr = JOptionPane.showInputDialog(frame, "Enter amount to withdraw:");
        if (amountStr == null || !amountStr.matches("\\d+(\\.\\d{1,2})?")) return;

        double amount = Double.parseDouble(amountStr);

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/dbapp_bankdb", "root", "1234")) {
            String query = "SELECT current_balance FROM account_records WHERE account_ID = ? AND account_status = 'Active'";
            try (PreparedStatement statement = con.prepareStatement(query)) {
                statement.setInt(1, account_id);
                try (ResultSet res = statement.executeQuery()) {
                    if (res.next() && amount > res.getDouble("current_balance")) {
                        JOptionPane.showMessageDialog(frame, "Insufficient Balance", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            String updateQuery = "UPDATE account_records SET current_balance = current_balance - ? WHERE account_ID = ? AND account_status = 'Active'";
            try (PreparedStatement statement = con.prepareStatement(updateQuery)) {
                statement.setDouble(1, amount);
                statement.setInt(2, account_id);
                if (statement.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(frame, "Withdrawal successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void transferToAnother(int account_id) {
        String amountStr = JOptionPane.showInputDialog(frame, "Enter amount to transfer:");
        if (amountStr == null || !amountStr.matches("\\d+(\\.\\d{1,2})?")) return;

        double amount = Double.parseDouble(amountStr);
        String destIdStr = JOptionPane.showInputDialog(frame, "Enter recipient Account ID:");
        if (destIdStr == null || !destIdStr.matches("\\d+")) return;

        int dest_id = Integer.parseInt(destIdStr);

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/dbapp_bankdb", "root", "1234")) {
            String updateQuery = "UPDATE account_records SET current_balance = current_balance - ? WHERE account_ID = ? AND account_status = 'Active'";
            try (PreparedStatement statement = con.prepareStatement(updateQuery)) {
                statement.setDouble(1, amount);
                statement.setInt(2, account_id);
                if (statement.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(frame, "Transfer successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeAccount(int account_id) {
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to close this account?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/dbapp_bankdb", "root", "1234")) {
            String updateQuery = "UPDATE account_records SET account_status = 'Closed' WHERE account_ID = ?";
            try (PreparedStatement statement = con.prepareStatement(updateQuery)) {
                statement.setInt(1, account_id);
                if (statement.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(frame, "Account closed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
