package Controller;

import Controller.LoanPaymentGUI;
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

public class LoanPaymentGUI {
    public static void showLoanPaymentGUI(int customerId) {
        JFrame frame = new JFrame("Loan Payment");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel loanIdLabel = new JLabel("Enter Loan ID:");
        JTextField loanIdField = new JTextField();

        JLabel accountIdLabel = new JLabel("Enter Account ID:");
        JTextField accountIdField = new JTextField();

        JButton payButton = new JButton("Pay Loan");
        JButton cancelButton = new JButton("Cancel");

        payButton.addActionListener(e -> {
            try {
                int loanId = Integer.parseInt(loanIdField.getText());
                int accountId = Integer.parseInt(accountIdField.getText());

                // Call the loan payment function and get the result message
                String result = AvailedLoans.loanPayment(customerId, loanId, accountId);

                // Show result in a GUI message box
                JOptionPane.showMessageDialog(frame, result);

                // Close window if payment is successful
                if (result.contains("successful")) {
                    frame.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input! Please enter numbers only.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> frame.dispose());

        // Add components to panel
        panel.add(loanIdLabel);
        panel.add(loanIdField);
        panel.add(accountIdLabel);
        panel.add(accountIdField);
        panel.add(payButton);
        panel.add(cancelButton);

        // Add panel to frame
        frame.add(panel);
        frame.setVisible(true);
        }
    }