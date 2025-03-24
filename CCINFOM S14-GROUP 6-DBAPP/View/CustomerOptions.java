package View;

import Controller.CustomerGUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerOptions {
    public static void showOptions() {
        JFrame frame = new JFrame("Customer Options");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));

        JLabel label = new JLabel("Customer Options", SwingConstants.CENTER);
        JButton signUpButton = new JButton("Sign Up");
        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        signUpButton.addActionListener(e -> CustomerGUI.signUpGUI());
        loginButton.addActionListener(e -> CustomerGUI.loginGUI());
        backButton.addActionListener(e -> frame.dispose());

        panel.add(signUpButton);
        panel.add(loginButton);
        panel.add(backButton);

        frame.add(label, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}