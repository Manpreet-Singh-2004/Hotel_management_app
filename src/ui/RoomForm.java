package ui;

import db.RoomDAO;
import models.Room;
import models.User;
import main.MainMenu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomForm extends JFrame {
    private JTextField numberField, typeField, priceField;
    private JComboBox<String> statusBox;
    private DefaultTableModel tableModel;
    private final User currentUser;
    private final MainMenu mainMenu;

    public RoomForm(User user, MainMenu menu) {
        this.currentUser = user;
        this.mainMenu = menu;

        setTitle("🛏️ Room Management");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(main);

        // Form
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
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

        JButton addBtn = new JButton("Add Room");
        JButton backBtn = new JButton("Back");
        form.add(addBtn);
        form.add(backBtn);

        main.add(form, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Room #", "Type", "Price", "Status"}, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        main.add(scrollPane, BorderLayout.CENTER);

        loadRooms();

        addBtn.addActionListener(e -> addRoom());
        backBtn.addActionListener(e -> {
            mainMenu.setVisible(true);
            dispose();
        });
    }

    private void addRoom() {
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
                numberField.setText("");
                typeField.setText("");
                priceField.setText("");
                loadRooms();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to add room.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Invalid price entered.");
        }
    }

    private void loadRooms() {
        tableModel.setRowCount(0);
        List<Room> rooms = new RoomDAO().getAllRooms();
        for (Room r : rooms) {
            tableModel.addRow(new Object[]{r.getRoomNumber(), r.getType(), "$" + r.getPrice(), r.getStatus()});
        }
    }

    public static void main(String[] args) {
        User dummy = new User();
        dummy.setUsername("admin");
        dummy.setRole("Admin");

        MainMenu menu = new MainMenu(dummy);
        menu.setVisible(false);
        SwingUtilities.invokeLater(() -> new RoomForm(dummy, menu).setVisible(true));
    }
}
