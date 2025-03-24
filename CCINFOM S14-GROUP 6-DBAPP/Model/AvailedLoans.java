package Model;
import java.sql.*;
import java.awt.*;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


import HelperClass.UserInput;

public class AvailedLoans {

    enum LoanStatus {
        FullyPaid,
        Unpaid,
        Ongoing

    }

    public static void showAvailedLoans(int customer_id) {
        JFrame frame = new JFrame("Availed Loans");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Vertical layout

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(scrollPane);

        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb", "root", "1234"
            );

            String getInfoQuery = "SELECT * FROM availed_loans WHERE customer_ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(getInfoQuery);
            preparedStatement.setInt(1, customer_id);
            ResultSet infoResultSet = preparedStatement.executeQuery();

            if (!infoResultSet.isBeforeFirst()) {
                JOptionPane.showMessageDialog(frame, "No Availed Loans Found", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            while (infoResultSet.next()) {
                JPanel loanPanel = new JPanel();
                loanPanel.setLayout(new GridLayout(0, 1)); // One column, multiple rows
                loanPanel.setBorder(BorderFactory.createTitledBorder("Loan ID: " + infoResultSet.getInt("loan_ID")));

                loanPanel.add(new JLabel("Loan Option ID: " + infoResultSet.getInt("loan_option_ID")));
                loanPanel.add(new JLabel("Principal Amount: PHP " + infoResultSet.getDouble("principal_amt")));
                loanPanel.add(new JLabel("First Month Amortization: PHP " + infoResultSet.getDouble("first_month_principal_amortization")));
                loanPanel.add(new JLabel("Succeeding Principal Amortization: PHP " + infoResultSet.getDouble("succeding_principal_amortization")));
                loanPanel.add(new JLabel("Interest Amortization: PHP " + infoResultSet.getDouble("interest_amortization")));
                loanPanel.add(new JLabel("Principal Balance: PHP " + infoResultSet.getDouble("principal_balance")));
                loanPanel.add(new JLabel("Interest Balance: PHP " + infoResultSet.getDouble("interest_balance")));
                loanPanel.add(new JLabel("Start Date: " + infoResultSet.getDate("start_date")));
                loanPanel.add(new JLabel("End Date: " + infoResultSet.getDate("end_date")));
                loanPanel.add(new JLabel("Monthly Payment Day: " + infoResultSet.getDate("month_payment_day")));
                loanPanel.add(new JLabel("Loan Status: " + infoResultSet.getString("loan_status")));

                panel.add(loanPanel);
            }

            connection.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        frame.setVisible(true);
    }

    public static void loanAppli(int customer_id) {
        JFrame frame = new JFrame("Loan Application");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(0, 1));

        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb", "root", "1234"
            );

            String checkIDQuery = "SELECT COUNT(customer_ID) c FROM availed_loans WHERE customer_ID = ?";
            PreparedStatement prepedStatementCheck = connection.prepareStatement(checkIDQuery);
            prepedStatementCheck.setInt(1, customer_id);
            ResultSet checkIDResult = prepedStatementCheck.executeQuery();

            if (checkIDResult.next() && checkIDResult.getInt("c") >= 2) {
                JOptionPane.showMessageDialog(frame, "You cannot avail of more loans. You already have 2 loans.", "Loan Limit Reached", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String loanTypesQuery = "SELECT * FROM loan_options";
            Statement statement1 = connection.createStatement();
            ResultSet loanTypesSet = statement1.executeQuery(loanTypesQuery);

            JComboBox<String> loanTypeDropdown = new JComboBox<>();
            while (loanTypesSet.next()) {
                loanTypeDropdown.addItem(loanTypesSet.getInt("loan_option_ID") + ": " + loanTypesSet.getString("loan_option_type"));
            }

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Select Loan Type:"));
            panel.add(loanTypeDropdown);

            int result = JOptionPane.showConfirmDialog(frame, panel, "Loan Selection", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            String selectedLoan = (String) loanTypeDropdown.getSelectedItem();
            int chosenLoan = Integer.parseInt(selectedLoan.split(":")[0]);

            String loanAmtRange = "SELECT * FROM loan_options WHERE loan_option_ID = ?";
            PreparedStatement prepStmtLoanAmt = connection.prepareStatement(loanAmtRange);
            prepStmtLoanAmt.setInt(1, chosenLoan);
            ResultSet loanAmtRangeSet = prepStmtLoanAmt.executeQuery();

            double min = 0, max = 0;
            int loanTerm = 0;
            double interestRate = 0;

            if (loanAmtRangeSet.next()) {
                min = loanAmtRangeSet.getDouble("min_loan_amt");
                max = loanAmtRangeSet.getDouble("max_loan_amt");
                loanTerm = loanAmtRangeSet.getInt("loan_duration_month");
                interestRate = loanAmtRangeSet.getDouble("interest_rate");
            }

            double loanPrin = 0;
            boolean validInput = false;
            while (!validInput) {
                JTextField loanAmountField = new JTextField();
                panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Enter Loan Amount (PHP " + min + " - " + max + "):"));
                panel.add(loanAmountField);

                result = JOptionPane.showConfirmDialog(frame, panel, "Loan Amount", JOptionPane.OK_CANCEL_OPTION);
                if (result != JOptionPane.OK_OPTION) {
                    return;
                }

                try {
                    loanPrin = Double.parseDouble(loanAmountField.getText());
                    if (loanPrin >= min && loanPrin <= max) {
                        validInput = true;
                    } else {
                        JOptionPane.showMessageDialog(frame, "Amount must be between PHP " + min + " and PHP " + max, "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(frame, "Invalid number entered!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            double principleAmort = loanPrin / loanTerm;
            double firstMonthPay = principleAmort + (loanPrin * interestRate);
            double interestAmort = (loanPrin * interestRate) / loanTerm;

            LocalDate currentDate = LocalDate.now();
            LocalDate endDate = currentDate.plusMonths(loanTerm);

            // Ensure month_payment_day is a valid date
            LocalDate monthPaymentDay = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 20);

            String newApplicationQuery = "INSERT INTO availed_loans "
                    + "(loan_option_ID, principal_amt, first_month_principal_amortization, succeding_principal_amortization, "
                    + "interest_amortization, principal_balance, interest_balance, start_date, end_date, month_payment_day, loan_status, customer_ID) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement preparedStatementInput = connection.prepareStatement(newApplicationQuery);
            preparedStatementInput.setInt(1, chosenLoan);
            preparedStatementInput.setDouble(2, loanPrin);
            preparedStatementInput.setDouble(3, firstMonthPay);
            preparedStatementInput.setDouble(4, principleAmort);
            preparedStatementInput.setDouble(5, interestAmort);
            preparedStatementInput.setDouble(6, loanPrin);
            preparedStatementInput.setDouble(7, interestAmort * loanTerm);
            preparedStatementInput.setDate(8, Date.valueOf(currentDate)); // Start date
            preparedStatementInput.setDate(9, Date.valueOf(endDate)); // End date
            preparedStatementInput.setDate(10, Date.valueOf(monthPaymentDay)); // Corrected month_payment_day
            preparedStatementInput.setString(11, LoanStatus.Ongoing.name());
            preparedStatementInput.setInt(12, customer_id);

            int rowsInserted = preparedStatementInput.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(frame, "Loan Application Approved!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            connection.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    public static double firstMonthPrincipalAmortizationLoanFormula(double principalAmount, int loanTerm, double roundedPrincipleAmort) {
        double answer;
        answer = principalAmount - (roundedPrincipleAmort * (loanTerm - 1));
        return Math.round(answer * 100.0) / 100.0;
    }

    public static double succMonthPrincipalAmortizationLoanFormula(double principalAmount, int loanTerm) {
        double answer;
        answer = principalAmount / loanTerm;
        return Math.round(answer * 100.0) / 100.0;
    }

    public static double interest_amortization(double principalAmount, int loanTerm, double interestRate) {
        double answer;
        answer=(principalAmount * interestRate) / loanTerm;
        return Math.round(answer * 100.0) / 100.0;
    }

    public static String loanPayment(int customer_id, int loan_id, int account_id) {
        Date startDate = null;
        double firstMonthAmort = 0, monthlyAmort = 0, interestAmort = 0;
        double principleBal = 0, interestBal = 0, outstandingBal;
        boolean monthChecker = false;
        double monthPayment, currentMoney = 0, accountMinBal = 0;
        LoanStatus loanStatus;
        double accountDeduction, lateLoanFee;
        String accountType = null;
    
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb",
                    "root",
                    "1234"
            );
    
            String getAmountInfoQuery = "SELECT * FROM availed_loans WHERE loan_ID = ?";
            PreparedStatement preparedStatementAmountInfo = connection.prepareStatement(getAmountInfoQuery);
            preparedStatementAmountInfo.setInt(1, loan_id);
            ResultSet amtInfoResultSet = preparedStatementAmountInfo.executeQuery();
    
            LocalDate currentDate = LocalDate.now();
    
            if (amtInfoResultSet.next()) {
                startDate = amtInfoResultSet.getDate("start_date");
                firstMonthAmort = amtInfoResultSet.getDouble("first_month_principal_amortization");
                monthlyAmort = amtInfoResultSet.getDouble("succeding_principal_amortization");
                interestAmort = amtInfoResultSet.getDouble("interest_amortization");
                principleBal = amtInfoResultSet.getDouble("principal_balance");
                interestBal = amtInfoResultSet.getDouble("interest_balance");
    
                if (startDate.toLocalDate().getMonthValue() == currentDate.getMonthValue()) {
                    monthChecker = true;
                }
            } else {
                return "Loan ID not found!";
            }
    
            monthPayment = monthChecker ? firstMonthAmort : monthlyAmort;
            lateLoanFee = lateLoanAmount(loan_id, firstMonthAmort, monthlyAmort, interestAmort, startDate);
            outstandingBal = monthPayment + interestAmort + lateLoanFee;
    
            String moneyCheckQuery = "SELECT * FROM account_records WHERE account_ID = ? AND customer_ID = ?";
            PreparedStatement preparedStatementMoneyQuery = connection.prepareStatement(moneyCheckQuery);
            preparedStatementMoneyQuery.setInt(1, account_id);
            preparedStatementMoneyQuery.setInt(2, customer_id);
            ResultSet moneyResultSet = preparedStatementMoneyQuery.executeQuery();
    
            if (moneyResultSet.next()) {
                currentMoney = moneyResultSet.getDouble("current_balance");
            } else {
                return "No matching account found!";
            }
    
            String accountTypeQuery = "SELECT account_type_ID FROM account_records WHERE account_id = ?";
            PreparedStatement preparedStatementAccountType = connection.prepareStatement(accountTypeQuery);
            preparedStatementAccountType.setInt(1, account_id);
            ResultSet accountTypeResultSet = preparedStatementAccountType.executeQuery();
    
            if (accountTypeResultSet.next()) {
                accountType = accountTypeResultSet.getString("account_type_ID");
            }
    
            String moneyCheckQuery2 = "SELECT min_balance FROM account_type WHERE account_type = ?";
            PreparedStatement preparedStatementMinimumBal = connection.prepareStatement(moneyCheckQuery2);
            preparedStatementMinimumBal.setString(1, accountType);
            ResultSet minimumBalResultSet = preparedStatementMinimumBal.executeQuery();
    
            if (minimumBalResultSet.next()) {
                accountMinBal = minimumBalResultSet.getDouble("min_balance");
            }
    
            if (currentMoney < outstandingBal) {
                return "Insufficient funds!";
            } else if (currentMoney - outstandingBal < accountMinBal) {
                return "Cannot pay: balance will go below minimum required!";
            } else {
                principleBal -= monthPayment;
                interestBal -= interestAmort;
                loanStatus = (principleBal + interestBal == 0) ? LoanStatus.FullyPaid : LoanStatus.Ongoing;
    
                String updateAvailedLoansQuery = "UPDATE availed_loans SET principal_balance = ?, interest_balance = ?, loan_status = ? WHERE loan_id = ?";
                PreparedStatement preparedStatementUpdate = connection.prepareStatement(updateAvailedLoansQuery);
                preparedStatementUpdate.setDouble(1, principleBal);
                preparedStatementUpdate.setDouble(2, interestBal);
                preparedStatementUpdate.setString(3, loanStatus.name());
                preparedStatementUpdate.setInt(4, loan_id);
    
                if (preparedStatementUpdate.executeUpdate() > 0) {
                    accountDeduction = currentMoney - outstandingBal;
                    String updateAccountQuery = "UPDATE account_records SET current_balance = ? WHERE account_ID = ?";
                    PreparedStatement preparedStatementUpdate2 = connection.prepareStatement(updateAccountQuery);
                    preparedStatementUpdate2.setDouble(1, accountDeduction);
                    preparedStatementUpdate2.setInt(2, account_id);
    
                    if (preparedStatementUpdate2.executeUpdate() > 0) {
                        TransactionHistory.generateLoanTransactionRecord(account_id, loan_id, outstandingBal);
                        return "Loan payment successful!";
                    }
                }
            }
        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        }
    
        return "Payment failed!";
    }

    public static double lateLoanAmount(int loan_id, double firstMonthPayment, double succeedingMonthPayment, double interestPayment, Date loanAvailed){

        double answer = 0;
        LocalDate currDate = LocalDate.now();
        Date lastDatePaid = null;
        double lateFee = 500;


        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb",
                    "root",
                    "1234"
            );

            String getLastDatePaidQuery = "SELECT * FROM loan_transaction_history WHERE borrower_acc_ID = ? ORDER BY loan_transaction_date DESC LIMIT 1 ";
            PreparedStatement preparedStatement = connection.prepareStatement(getLastDatePaidQuery);
            preparedStatement.setInt(1,loan_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                lastDatePaid =  resultSet.getDate("loan_transaction_date");
            }

            if (lastDatePaid == null) {
                if (currDate.getYear() == (loanAvailed.getYear() + 1900) && currDate.getMonthValue() == (loanAvailed.getMonth() + 1)){
                    System.out.println("You don't have late fees!");
                    return answer;
                } else {
                    int currDateYear = currDate.getYear();
                    int currDateMonth = currDate.getMonthValue();
                    int availedLoanYear = loanAvailed.getYear() + 1900;
                    int availedLoanMonth = loanAvailed.getMonth() + 1;

                    int monthsBetween = ((availedLoanYear - currDateYear) * 12 + (availedLoanMonth - currDateMonth));
                    //System.out.println("TEST Months Between: " + monthsBetween);

                    if (monthsBetween == 0) {
                        monthsBetween = 1;
                    } else if (monthsBetween < 0) {
                        monthsBetween = monthsBetween * -1;
                    }
                    answer = ((firstMonthPayment + interestPayment + succeedingMonthPayment) * (monthsBetween - 1)) + (lateFee * monthsBetween);
                }

            } else {
                if (currDate.getYear() == (lastDatePaid.getYear() + 1900) && currDate.getMonthValue() == (lastDatePaid.getMonth() + 1)){
                    System.out.println("You don't have late fees!");
                    return answer;
                } else {
                    int currDateYear = currDate.getYear();
                    int currDateMonth = currDate.getMonthValue();
                    int lastPaidYear = lastDatePaid.getYear() + 1900;
                    int lastPaidMonth = lastDatePaid.getMonth() + 1;

                    int monthsBetween = ((lastPaidYear - currDateYear) * 12 + (lastPaidMonth - currDateMonth));
                    //System.out.println("TEST Months Between: " + monthsBetween);

                    if (monthsBetween == 0) {
                        monthsBetween = 1;
                    } else if (monthsBetween < 0) {
                        monthsBetween = monthsBetween * -1;
                    }
                    answer = ((succeedingMonthPayment + interestPayment) * monthsBetween) + (lateFee * monthsBetween);
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return Math.round(answer * 100.0) / 100.0;
    }

} // end