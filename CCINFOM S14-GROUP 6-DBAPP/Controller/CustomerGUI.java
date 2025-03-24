package Controller;

import Model.Account;
import Model.AvailedLoans;
import Model.LoanOptions;
import Model.TransactionHistory;

import Model.Customer;
import Model.InterestCalculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerGUI {
    public static void signUpGUI() {
        JFrame frame = new JFrame("Sign Up");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 10, 10));

        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField dobField = new JTextField();
        JButton submitButton = new JButton("Sign Up");

        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email Address:"));
        panel.add(emailField);
        panel.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        panel.add(dobField);
        panel.add(submitButton);

        submitButton.addActionListener(e -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String dob = dobField.getText();

            // Call the signUp method
            boolean success = Customer.signUp(firstName, lastName, phone, email, dob);
            if (success) {
                JOptionPane.showMessageDialog(frame, "Sign Up Successful!");
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Sign Up Failed! User may already exist.");
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void loginGUI() {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JButton loginButton = new JButton("Login");

        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(loginButton);

        loginButton.addActionListener(e -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();

            Customer customer = Customer.login(firstName, lastName);
            if (customer != null) {
                JOptionPane.showMessageDialog(frame, "Login Successful!");
                frame.dispose();
                showCustomerActionsGUI(customer);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials!");
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void showCustomerActionsGUI(Customer customer) {
        JFrame frame = new JFrame("Customer Actions");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
    
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));
    
        JButton viewAccountsButton = new JButton("View Accounts");
        JButton openAccountButton = new JButton("Open New Account");
        JButton viewLoansButton = new JButton("View Loans");
        JButton availLoanButton = new JButton("Avail New Loan");
        JButton savingsReportButton = new JButton("View Annual Savings Report");
        JButton payLoanButton = new JButton("Pay Loan");
        JButton calculateInterestButton = new JButton("Calculate Interest");
    
        viewAccountsButton.addActionListener(e -> {
            new AccountGUI(customer); // Open AccountGUI with the current customer
        });
    
        openAccountButton.addActionListener(e -> {
            JFrame accountFrame = new JFrame("Open New Account");
            accountFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            accountFrame.setSize(300, 250);
        
            JPanel panel2 = new JPanel();
            panel2.setLayout(new GridLayout(4, 2, 10, 10));
        
            JLabel accountTypeLabel = new JLabel("Select Account Type:");
            String[] accountTypes = {"Personal", "Business", "Special"};
            JComboBox<String> accountTypeBox = new JComboBox<>(accountTypes);
        
            JLabel depositLabel = new JLabel("Initial Deposit:");
            JTextField depositField = new JTextField();
        
            JButton createAccountButton = new JButton("Create Account");
        
            panel.add(accountTypeLabel);
            panel.add(accountTypeBox);
            panel.add(depositLabel);
            panel.add(depositField);
            panel.add(new JLabel()); // Empty space
            panel.add(createAccountButton);
        
            createAccountButton.addActionListener(ev -> {
                String selectedType = (String) accountTypeBox.getSelectedItem();
                String depositText = depositField.getText();
        
                // Validate deposit input
                try {
                    double initialDeposit = Double.parseDouble(depositText);
        
                    // Ensure 'customer' object exists and has a valid ID
                    if (customer == null) {
                        JOptionPane.showMessageDialog(accountFrame, "Customer information not found. Please log in again.");
                        return;
                    }
        
                    int customerID = customer.getCustomer_id(); // Ensure this method exists and returns an int
                    boolean success = Account.createNewAccount(customerID, selectedType, initialDeposit);
        
                    if (success) {
                        JOptionPane.showMessageDialog(accountFrame, "New " + selectedType + " account created successfully!");
                        accountFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(accountFrame, "Account creation failed. Please try again.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(accountFrame, "Invalid deposit amount. Please enter a valid number.");
                }
            });
        
            accountFrame.add(panel);
            accountFrame.setVisible(true);
        });
    
        viewLoansButton.addActionListener(e -> {
            AvailedLoans.showAvailedLoans(customer.getCustomer_id());
        });
    
        availLoanButton.addActionListener(e -> {
            AvailedLoans.loanAppli(customer.getCustomer_id());
            JOptionPane.showMessageDialog(frame, "Loan application submitted!");
        });
    
        savingsReportButton.addActionListener(e -> {
            String yearToGenerate = JOptionPane.showInputDialog(frame, "Enter the year (YYYY)T for savings report:");
            if (yearToGenerate != null && yearToGenerate.matches("\\d{4}")) {
                try {
                    TransactionHistory transactionHistory = new TransactionHistory();
                    transactionHistory.generateMonthlySavings(customer.getCustomer_id(), yearToGenerate);
                    JOptionPane.showMessageDialog(frame, "Savings report generated for " + yearToGenerate);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error generating report: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid year format. Please enter in YYYY format.");
            }
        });
    
        payLoanButton.addActionListener(e -> {
            LoanPaymentGUI.showLoanPaymentGUI(customer.getCustomer_id());
        });

        calculateInterestButton.addActionListener(e -> {
            String accountIDInput = JOptionPane.showInputDialog(frame, "Enter Account ID to calculate interest:");
            if (accountIDInput != null && accountIDInput.matches("\\d+")) {
                int accountID = Integer.parseInt(accountIDInput);
                InterestCalculator interestCalculator = new InterestCalculator();
                interestCalculator.calculateInterest(accountID);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Account ID. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        panel.add(viewAccountsButton);
        panel.add(openAccountButton);
        panel.add(viewLoansButton);
        panel.add(availLoanButton);
        panel.add(calculateInterestButton);
        panel.add(savingsReportButton);
        panel.add(payLoanButton);
        

        frame.add(panel);
        frame.setVisible(true);
    }
    
}