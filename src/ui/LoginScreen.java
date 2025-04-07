package ui;

import db.UserDAO;
import main.MainMenu;
import models.User;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {

    public LoginScreen() {
        setTitle("🔐 Login");
        setSize(350, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin", "Receptionist"});

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);
        panel.add(loginBtn);
        panel.add(registerBtn);

        add(panel);

        loginBtn.addActionListener(e -> {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            User loggedIn = new UserDAO().login(user, pass);

            if (loggedIn != null) {
                JOptionPane.showMessageDialog(this, "✅ Login successful!");
                dispose();
                new MainMenu(loggedIn).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid credentials");
            }
        });

        registerBtn.addActionListener(e -> {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();
            String role = roleCombo.getSelectedItem().toString();

            boolean registered = new UserDAO().register(new User(user, pass, role));
            JOptionPane.showMessageDialog(this, registered ? "✅ Registered!" : "❌ Username taken or error");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
