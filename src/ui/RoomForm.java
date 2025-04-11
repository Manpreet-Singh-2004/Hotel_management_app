package ui;

import db.RoomDAO;
import models.Room;
import models.User;
import main.MainMenu;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomForm extends JFrame {
    private JTextField numberField, typeField, priceField;
    private JComboBox<String> statusBox;
    private DefaultTableModel tableModel;
    private final User currentUser;
    private final MainMenu mainMenu;
    private JTable table;

    public RoomForm(User user, MainMenu menu) {
        this.currentUser = user;
        this.mainMenu = menu;

        setTitle("🛏️ Room Management");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(main);

        // ==== FORM PANEL ====
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Add / Update Room"));

        numberField = new JTextField();
        typeField = new JTextField();
        priceField = new JTextField();
        statusBox = new JComboBox<>(new String[]{"Available", "Occupied", "Maintenance"});

        form.add(new JLabel("Room Number:"));
        form.add(numberField);
        form.add(new JLabel("Room Type:"));
        form.add(typeField);
        form.add(new JLabel("Price:"));
        form.add(priceField);
        form.add(new JLabel("Status:"));
        form.add(statusBox);

        JButton addBtn = new JButton("➕ Add Room");
        JButton updateBtn = new JButton("✏️ Update");
        JButton backBtn = new JButton("⬅ Back");
        form.add(addBtn);
        form.add(updateBtn);

        main.add(form, BorderLayout.NORTH);

        // ==== TABLE ====
        tableModel = new DefaultTableModel(new String[]{"Room #", "Type", "Price", "Status"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(30);

        // Color the status column
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                String status = val.toString();
                if ("Available".equalsIgnoreCase(status)) {
                    c.setForeground(new Color(0, 128, 0));
                } else if ("Occupied".equalsIgnoreCase(status)) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(new Color(255, 140, 0));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        main.add(scrollPane, BorderLayout.CENTER);

        // ==== BOTTOM PANEL ====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteBtn = new JButton("🗑 Delete");
        bottom.add(deleteBtn);
        bottom.add(backBtn);
        main.add(bottom, BorderLayout.SOUTH);

        loadRooms();

        // ==== EVENT HANDLERS ====
        addBtn.addActionListener(e -> {
            try {
                String number = numberField.getText().trim();
                String type = typeField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                String status = (String) statusBox.getSelectedItem();

                if (number.isEmpty() || type.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "❌ All fields are required.");
                    return;
                }

                Room room = new Room(number, type, price, status);
                boolean success = new RoomDAO().addRoom(room);

                if (success) {
                    JOptionPane.showMessageDialog(this, "✅ Room added!");
                    clearForm();
                    loadRooms();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed to add room.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "❌ Invalid price entered.");
            }
        });

        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "❌ Select a room to update.");
                return;
            }

            try {
                String number = numberField.getText().trim();
                String type = typeField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                String status = (String) statusBox.getSelectedItem();

                Room existing = new RoomDAO().getRoomByNumber(tableModel.getValueAt(row, 0).toString());
                existing.setRoomNumber(number);
                existing.setType(type);
                existing.setPrice(price);
                existing.setStatus(status);

                boolean updated = new RoomDAO().updateRoom(existing);
                if (updated) {
                    JOptionPane.showMessageDialog(this, "✏️ Room updated!");
                    clearForm();
                    loadRooms();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Update failed.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Invalid input.");
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "❌ Select a room to delete.");
                return;
            }
            String number = tableModel.getValueAt(row, 0).toString();
            Room r = new RoomDAO().getRoomByNumber(number);
            if (r == null) {
                JOptionPane.showMessageDialog(this, "❌ Room not found.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Delete Room " + number + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = new RoomDAO().deleteRoom(r.getRoomId());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "🗑 Room deleted.");
                    loadRooms();
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                numberField.setText(tableModel.getValueAt(row, 0).toString());
                typeField.setText(tableModel.getValueAt(row, 1).toString());
                priceField.setText(tableModel.getValueAt(row, 2).toString().replace("$", ""));
                statusBox.setSelectedItem(tableModel.getValueAt(row, 3).toString());
            }
        });

        backBtn.addActionListener(e -> {
            mainMenu.setVisible(true);
            dispose();
        });
    }

    private void loadRooms() {
        tableModel.setRowCount(0);
        for (Room r : new RoomDAO().getAllRooms()) {
            tableModel.addRow(new Object[]{r.getRoomNumber(), r.getType(), "$" + r.getPrice(), r.getStatus()});
        }
    }

    private void clearForm() {
        numberField.setText("");
        typeField.setText("");
        priceField.setText("");
        statusBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        User dummy = new User("admin", "123", "Admin");
        MainMenu menu = new MainMenu(dummy);
        menu.setVisible(false);
        new RoomForm(dummy, menu).setVisible(true);
    }
}
