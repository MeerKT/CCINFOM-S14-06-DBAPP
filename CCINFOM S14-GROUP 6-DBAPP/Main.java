import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import View.CustomerOptions;
import View.EmployeeOptions;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Banking Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        
        JLabel label = new JLabel("Welcome to the Banking System", SwingConstants.CENTER);
        JButton customerButton = new JButton("Customer Options");
        JButton employeeButton = new JButton("Employee Options");
        JButton exitButton = new JButton("Exit");
        
        customerButton.addActionListener(e -> CustomerOptions.showOptions());
        employeeButton.addActionListener(e -> EmployeeOptions.showOptions());
        exitButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Exiting Application.");
            System.exit(0);
        });
        
        panel.add(customerButton);
        panel.add(employeeButton);
        panel.add(exitButton);
        
        frame.add(label, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}