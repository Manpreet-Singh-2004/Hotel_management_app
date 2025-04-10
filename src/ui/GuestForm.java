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
    private final JTable table;
    private final User currentUser;
    private final MainMenu mainMenu;

    public GuestForm(User user, MainMenu menu) {
        this.currentUser = user;
        this.mainMenu = menu;

        setTitle("👤 Guest Management");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(panel);

        // Form panel
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
        JButton updateBtn = new JButton("✏️ Update");
        JButton deleteBtn = new JButton("🗑️ Delete");
        JButton backBtn = new JButton("⬅ Back");

        formPanel.add(addBtn);
        formPanel.add(updateBtn);

        JPanel bottomButtons = new JPanel(new GridLayout(1, 2, 10, 10));
        bottomButtons.add(deleteBtn);
        bottomButtons.add(backBtn);

        panel.add(formPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone", "Address"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomButtons, BorderLayout.SOUTH);

        loadGuests();

        // Events
        addBtn.addActionListener(e -> addGuest());
        updateBtn.addActionListener(e -> updateGuest());
        deleteBtn.addActionListener(e -> deleteGuest());

        backBtn.addActionListener(e -> {
            mainMenu.setVisible(true);
            dispose();
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                nameField.setText(tableModel.getValueAt(row, 1).toString());
                emailField.setText(tableModel.getValueAt(row, 2).toString());
                phoneField.setText(tableModel.getValueAt(row, 3).toString());
                addressField.setText(tableModel.getValueAt(row, 4).toString());
            }
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
            clearFields();
            loadGuests();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Failed to add guest.");
        }
    }

    private void updateGuest() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Select a guest to update.");
            return;
        }

        int guestId = (int) tableModel.getValueAt(selectedRow, 0);
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ All fields are required.");
            return;
        }

        Guest guest = new Guest(guestId, name, email, phone, address);
        boolean success = new GuestDAO().updateGuest(guest);

        if (success) {
            JOptionPane.showMessageDialog(this, "✅ Guest updated!");
            clearFields();
            loadGuests();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Update failed.");
        }
    }

    private void deleteGuest() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Select a guest to delete.");
            return;
        }

        int guestId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this guest?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = new GuestDAO().deleteGuest(guestId);
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Guest deleted!");
                clearFields();
                loadGuests();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Deletion failed.");
            }
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

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        table.clearSelection();
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
