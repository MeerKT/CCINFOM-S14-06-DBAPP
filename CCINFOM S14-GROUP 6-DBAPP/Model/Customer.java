package Model;

import java.sql.*;
import java.util.Date;

import HelperClass.UserInput;

public class Customer {
    private int customer_id, account_id, loan_id;
    private String customer_first_name, customer_last_name;
    private Date birth_date;
    private String phone_number, email_address;

    public Customer(int id, String firstName, String lastName, java.sql.Date birthDate, String phoneNum, String email) {
        customer_id = id;
        customer_first_name = firstName;
        customer_last_name = lastName;
        birth_date = new Date(birthDate.getTime());
        phone_number = phoneNum;
        email_address = email;
    }

    public static boolean signUp(String firstName, String lastName, String phone, String email, String dob) {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3307/dbapp_bankdb",
                "root",
                "1234")) {

            // Check if user already exists
            String checkQuery = "SELECT * FROM customer_records WHERE first_name = ? AND last_name = ? ";
            try (PreparedStatement checkStatement = con.prepareStatement(checkQuery)) {
                checkStatement.setString(1, firstName);
                checkStatement.setString(2, lastName);

                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return false; // Account already exists
                    }
                }
            }

            // Insert new customer
            String insertQuery = "INSERT INTO customer_records (first_name, last_name, birthdate, phone_number, email_address) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = con.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, dob);
                preparedStatement.setString(4, phone);
                preparedStatement.setString(5, email);

                return preparedStatement.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Customer login(String firstName, String lastName) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/dbapp_bankdb", "root", "1234")) {
            String checkQuery = "SELECT * FROM customer_records WHERE first_name = ? AND last_name = ?";
            try (PreparedStatement statement = con.prepareStatement(checkQuery)) {
                statement.setString(1, firstName);
                statement.setString(2, lastName);

                try (ResultSet res = statement.executeQuery()) {
                    if (res.next()) {
                        return new Customer(
                                res.getInt("customer_id"),
                                // res.getInt("account_id"),
                                res.getString("first_name"),
                                res.getString("last_name"),
                                res.getDate("birthdate"),
                                res.getString("phone_number"),
                                res.getString("email_address"));
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static void showCustomerActions(Customer loggedInSession) {
        int option;

        do {
            System.out.println("1 - View Accounts\n2 - Open New Account\n3 - View Loans\n4 - Avail New Loan\n" +
                    "5 - View Annual Savings Report\n6 - Pay Loan");
            System.out.print("Choose option: ");
            option = Integer.parseInt(UserInput.getScanner().nextLine());

            switch (option) {
                case 1: //
                    System.out.println("View Accounts");
                    Account.showAccounts(loggedInSession.customer_id);
                    System.out.print("Input Account ID to Open Account: ");
                    int acc_id = Integer.parseInt(UserInput.getScanner().nextLine());
                    Account.viewAccountInfo(acc_id, loggedInSession.customer_id);
                    break;

                case 2: //open new account
                    System.out.println("Open New Account");
                    //Account.createNewAccount(loggedInSession.customer_id );
                    break;

                case 3: //view loans
                    System.out.println("View Loans");
                    AvailedLoans.showAvailedLoans(loggedInSession.customer_id);
                    break;

                case 4: //avail loans
                    System.out.println("Avail Loans");
                    AvailedLoans.loanAppli(loggedInSession.customer_id);
                    break;

                case 5:
                    System.out.println("View Monthly Savings Report");

                    // Retrieve the customer ID from the logged-in session or context
                    int customer_id = loggedInSession.getCustomer_id(); // Replace with actual logic

                    System.out.print("Enter the year (YYYY): ");
                    String yearToGenerate = UserInput.getScanner().nextLine();
                    if (!yearToGenerate.matches("\\d{4}")) {
                        System.out.println("Invalid year format. Please enter in YYYY format.");
                        break;
                    }

                    try {
                        TransactionHistory transactionHistory = new TransactionHistory();
                        transactionHistory.generateMonthlySavings(customer_id, yearToGenerate);
                    } catch (Exception e) {
                        System.out.println("An error occurred while generating the report: " + e.getMessage());
                    }
                    break;

                default:
                    System.out.println("\nInvalid option. Please choose again.");
                case 6:
                    AvailedLoans.loanPayment(loggedInSession.customer_id, loggedInSession.loan_id, loggedInSession.account_id);
                    break;
            }
        } while (option < 1 || option > 6);
    }


    public int getCustomer_id() { return this.customer_id; }
    public int getAccount_id() {return this.account_id; }
    public int getLoan_id() {return this.loan_id; }
    public String getCustomer_first_name() { return customer_first_name; }
    public String getCustomer_last_name() { return customer_last_name; }
    public String getPhone_number() { return phone_number; }
    public String getEmail_address() { return email_address; }
    public Date getBirth_date() { return birth_date; }
}