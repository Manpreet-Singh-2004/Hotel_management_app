package ui;

import db.GuestDAO;
import models.Guest;
import models.User;
import main.MainMenu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GuestForm extends JFrame {
    private final JTextField nameField, emailField, phoneField, addressField;
    private final DefaultTableModel tableModel;
    private final User currentUser;
    private final MainMenu mainMenu;

    public GuestForm(User user, MainMenu menu) {
        this.currentUser = user;
        this.mainMenu = menu;

        setTitle("👤 Guest Management");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(panel);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextField();

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);

        JButton addBtn = new JButton("➕ Add Guest");
        JButton backBtn = new JButton("⬅ Back");
        formPanel.add(addBtn);
        formPanel.add(backBtn);

        panel.add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone", "Address"}, 0);
        JTable table = new JTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        loadGuests();

        addBtn.addActionListener(e -> addGuest());
        backBtn.addActionListener(e -> {
            mainMenu.setVisible(true); // show main menu again
            dispose(); // close this form
        });
    }

    private void addGuest() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ All fields are required.");
            return;
        }

        Guest guest = new Guest(name, email, phone, address);
        boolean success = new GuestDAO().addGuest(guest);

        if (success) {
            JOptionPane.showMessageDialog(this, "✅ Guest added!");
            loadGuests();
            nameField.setText("");
            emailField.setText("");
            phoneField.setText("");
            addressField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "❌ Failed to add guest.");
        }
    }

    private void loadGuests() {
        tableModel.setRowCount(0);
        List<Guest> guests = new GuestDAO().getAllGuests();
        for (Guest g : guests) {
            tableModel.addRow(new Object[]{
                    g.getGuestId(),
                    g.getFullName(),
                    g.getEmail(),
                    g.getPhone(),
                    g.getAddress()
            });
        }
    }

    public static void main(String[] args) {
        User dummy = new User();
        dummy.setUsername("admin");
        dummy.setRole("Admin");

        SwingUtilities.invokeLater(() -> {
            MainMenu m = new MainMenu(dummy);
            m.setVisible(false);
            new GuestForm(dummy, m).setVisible(true);
        });
    }
}
