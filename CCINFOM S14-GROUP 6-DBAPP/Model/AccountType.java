package Model;

import HelperClass.UserInput;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AccountType {

    public static void showAccountTypes() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb",
                    "root",
                    "1234"
            );

            String getInfoQuery = "SELECT * FROM account_type";
            PreparedStatement preparedStatementInfo = connection.prepareStatement(getInfoQuery);
            ResultSet res = preparedStatementInfo.executeQuery();

            // Store account type data
            List<String[]> accountData = new ArrayList<>();
            List<String> types = new ArrayList<>();

            while (res.next()) {
                String type = res.getString("account_type");
                double interestRate = res.getDouble("interest_rate");
                double minBalance = res.getDouble("min_balance");

                accountData.add(new String[]{type, String.valueOf(interestRate), String.valueOf(minBalance)});
                types.add(type);
            }

            if (accountData.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No account types found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create Table Model
            String[] columnNames = {"Account Type", "Interest Rate", "Minimum Balance"};
            DefaultTableModel model = new DefaultTableModel(accountData.toArray(new Object[0][]), columnNames);
            JTable table = new JTable(model);

            // Show table in scrollable pane
            JOptionPane.showMessageDialog(null, new JScrollPane(table), "Account Types", JOptionPane.INFORMATION_MESSAGE);

            // Ask the user to select an account type
            String selectedType = (String) JOptionPane.showInputDialog(
                    null,
                    "Choose an account type to view information:",
                    "Select Account Type",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    types.toArray(),
                    types.get(0)
            );

            if (selectedType == null) return; // User pressed Cancel

            // Fetch details for selected account type
            String viewQuery = "SELECT * FROM account_type WHERE account_type = ?";
            PreparedStatement statement = connection.prepareStatement(viewQuery);
            statement.setString(1, selectedType);
            ResultSet typeRes = statement.executeQuery();

            if (typeRes.next()) {
                String details = "Account Type: " + typeRes.getString("account_type") +
                        "\nInterest Rate: " + typeRes.getDouble("interest_rate") +
                        "\nMinimum Balance: " + typeRes.getDouble("min_balance");

                // Show details in GUI
                int choice = JOptionPane.showOptionDialog(
                        null,
                        details + "\n\nWhat would you like to do?",
                        "Account Type Details",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"Change Interest Rate", "Change Minimum Balance"},
                        "Change Interest Rate"
                );

                if (choice == 0) {
                    changeInterestRate(selectedType);
                } else if (choice == 1) {
                    changeMinimumBalance(selectedType);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Account type doesn't exist", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving account types.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void changeInterestRate(String accType){
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb",
                    "root",
                    "1234"
            );

            System.out.print("Enter new interest rate: ");
            double rate = Double.parseDouble(UserInput.getScanner().nextLine());

            String updateQuery = "UPDATE account_type SET interest_rate = ? WHERE account_type = ?;";

            try (PreparedStatement statement = connection.prepareStatement(updateQuery)){
                statement.setDouble(1, rate);
                statement.setString(2, accType);
                int rowsAffected =  statement.executeUpdate();

                if(rowsAffected > 0){
                    System.out.println("Updated interest rate");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void changeMinimumBalance(String accType){
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb",
                    "root",
                    "1234"
            );

            System.out.print("Enter new  minimum balance: ");
            double min = Double.parseDouble(UserInput.getScanner().nextLine());

            String updateQuery = "UPDATE account_type SET min_balance = ? WHERE account_type = ?;";

            try (PreparedStatement statement = connection.prepareStatement(updateQuery)){
                statement.setDouble(1, min);
                statement.setString(2, accType);
                int rowsAffected =  statement.executeUpdate();

                if(rowsAffected > 0){
                    System.out.println("Updated minimum balance");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addDefaultAccountTypes(){
        try {
        Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb",
                    "root",
                    "1234"
        );

        String check1 = "SELECT * from account_type WHERE account_type = 'Personal'";
        String check2 = "SELECT * from account_type WHERE account_type = 'Business'";
        String check3 = "SELECT * from account_type WHERE account_type = 'Special'";

        Statement statement1 = connection.createStatement();
        Statement statement2 = connection.createStatement();
        Statement statement3 = connection.createStatement();

        ResultSet results1 = statement1.executeQuery(check1);
        ResultSet results2 = statement2.executeQuery(check2);
        ResultSet results3 = statement3.executeQuery(check3);

        if(!results1.next()) {
            String checkings = "INSERT INTO account_type (account_type, interest_rate, min_balance) " +
                    "VALUES ('Personal', 1.5, 5000)";
            statement1.executeUpdate(checkings);
        }

        if(!results2.next()) {
            String passbook = "INSERT INTO account_type (account_type, interest_rate, min_balance) " +
                    "VALUES ('Business', 0.8, 30000)";
            statement2.executeUpdate(passbook);
        }

        if(!results3.next()) {
            String savings = "INSERT INTO account_type (account_type, interest_rate, min_balance) " +
                    "VALUES ('Special', 2.5, 50000)";
            statement3.executeUpdate(savings);
        }

    } catch(SQLException e){
        e.printStackTrace();
    }}

}