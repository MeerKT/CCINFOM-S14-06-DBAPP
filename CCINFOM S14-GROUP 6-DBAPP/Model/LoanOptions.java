package Model;
import java.sql.*;

public class LoanOptions {

    public static void defaultLoan(String url, String user, String password){
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb",
                    "root",
                    "1234"
            );

            Statement statement1 = connection.createStatement();
            Statement statement2 = connection.createStatement();
            Statement statement3 = connection.createStatement();

            String selectQuery1 = "SELECT * FROM loan_options WHERE loan_option_ID = 1";
            String selectQuery2 = "SELECT * FROM loan_options WHERE loan_option_ID = 2";
            String selectQuery3 = "SELECT * FROM loan_options WHERE loan_option_ID = 3";

            ResultSet results1 = statement1.executeQuery(selectQuery1);
            ResultSet results2 = statement2.executeQuery(selectQuery2);
            ResultSet results3 = statement3.executeQuery(selectQuery3);

            if (!results1.next()){
                String personal_loan = "INSERT INTO loan_options "
                        + "(loan_option_ID, loan_option_type, interest_rate, loan_duration_month, max_loan_amt, min_loan_amt) "
                        + "VALUES ('1', 'Personal Loan', '0.07', '24', '1000000', '20000')";
                statement1.executeUpdate(personal_loan);
            }
            if (!results2.next()){
                String auto_loan = "INSERT INTO loan_options "
                        + "(loan_option_ID, loan_option_type, interest_rate, loan_duration_month, max_loan_amt, min_loan_amt) "
                        + "VALUES ('2', 'Auto Loan', '0.10', '36', '3000000', '250000')";
                statement2.executeUpdate(auto_loan);
            }

            if (!results3.next()) {
                String emergency_loan = "INSERT INTO loan_options "
                        + "(loan_option_ID, loan_option_type, interest_rate, loan_duration_month, max_loan_amt, min_loan_amt) "
                        + "VALUES ('3', 'Emergency Loan', '0.05', '24', '750000', '20000')";
                statement3.executeUpdate(emergency_loan);

            }

            System.out.println("Insert Complete!");

            results1.close();
            results2.close();
            results3.close();
            statement1.close();
            statement2.close();
            statement3.close();
            connection.close();

        } catch(SQLException e){
            e.printStackTrace();
        }

    }

    public static void addLoanOption(String loan_option_type, double interest_rate, int loan_duration_month, double max_loan_amount, double min_loan_amount) {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/dbapp_bankdb",
                    "root",
                    "1234"
            );

            String selectQuery = "SELECT COUNT(loan_option_type) FROM loan_options WHERE loan_option_type = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, loan_option_type);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    System.out.println("Record already exists. Exiting.");
                } else {
                    int id = 0;

                    System.out.println("Record does not exist. Proceeding.");
                    String newLoanTypeQuery = "INSERT INTO loan_options "
                            + "(loan_option_id, loan_option_type, interest_rate, loan_duration_month, max_loan_amt, min_loan_amt) "
                            + "VALUES (?,?,?,?,?,?)";

                    Statement statementInt = connection.createStatement();
                    ResultSet resultInt = statementInt.executeQuery("SELECT loan_option_id FROM loan_options ORDER BY loan_option_id DESC LIMIT 1");

                    if (resultInt.next()) {
                        id = resultInt.getInt("loan_option_id") + 1;
                    }

                    PreparedStatement preped = connection.prepareStatement(newLoanTypeQuery);
                    preped.setInt(1, id);
                    preped.setString(2, loan_option_type);
                    preped.setDouble(3, interest_rate);
                    preped.setInt(4, loan_duration_month);
                    preped.setDouble(5, max_loan_amount);
                    preped.setDouble(6, min_loan_amount);

                    int rowsInserted = preped.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("New loan option inserted successfully.");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
