package ui;

import db.GuestDAO;
import db.RoomDAO;
import models.Guest;
import models.Room;
import models.User;
import main.MainMenu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SearchFilterScreen extends JFrame {
    private JTextField guestSearchField;
    private JTable guestTable;

    private JComboBox<String> roomTypeCombo, roomStatusCombo;
    private JTable roomTable;

    private final User currentUser;
    private final MainMenu mainMenu;

    public SearchFilterScreen(User user, MainMenu menu) {
        this.currentUser = user;
        this.mainMenu = menu;

        setTitle("🔍 Search & Filter");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Guests", guestSearchPanel());
        tabs.add("Rooms", roomFilterPanel());

        add(tabs, BorderLayout.CENTER);

        // Back button
        JButton backBtn = new JButton("⬅ Back");
        backBtn.addActionListener(e -> {
            dispose();
            mainMenu.setVisible(true);
        });
        add(backBtn, BorderLayout.SOUTH);
    }

    // 🔍 Guest Search Tab
    private JPanel guestSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        guestSearchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");

        JPanel top = new JPanel();
        top.add(new JLabel("Name / Email / Phone:"));
        top.add(guestSearchField);
        top.add(searchBtn);
        panel.add(top, BorderLayout.NORTH);

        guestTable = new JTable(new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone"}, 0));
        panel.add(new JScrollPane(guestTable), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> loadGuestResults());

        return panel;
    }

    private void loadGuestResults() {
        String query = guestSearchField.getText().trim().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) guestTable.getModel();
        model.setRowCount(0);

        List<Guest> guests = new GuestDAO().getAllGuests();
        for (Guest g : guests) {
            if (g.getFullName().toLowerCase().contains(query) ||
                    g.getEmail().toLowerCase().contains(query) ||
                    g.getPhone().toLowerCase().contains(query)) {
                model.addRow(new Object[]{g.getGuestId(), g.getFullName(), g.getEmail(), g.getPhone()});
            }
        }
    }

    // 🧹 Room Filter Tab
    private JPanel roomFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        roomTypeCombo = new JComboBox<>(new String[]{"All", "Single", "Double", "Suite"});
        roomStatusCombo = new JComboBox<>(new String[]{"All", "Available", "Occupied", "Maintenance"});

        JButton filterBtn = new JButton("Filter");

        JPanel top = new JPanel();
        top.add(new JLabel("Type:"));
        top.add(roomTypeCombo);
        top.add(new JLabel("Status:"));
        top.add(roomStatusCombo);
        top.add(filterBtn);
        panel.add(top, BorderLayout.NORTH);

        roomTable = new JTable(new DefaultTableModel(new String[]{"ID", "Number", "Type", "Price", "Status"}, 0));
        panel.add(new JScrollPane(roomTable), BorderLayout.CENTER);

        filterBtn.addActionListener(e -> loadRoomResults());

        return panel;
    }

    private void loadRoomResults() {
        String selectedType = roomTypeCombo.getSelectedItem().toString();
        String selectedStatus = roomStatusCombo.getSelectedItem().toString();
        DefaultTableModel model = (DefaultTableModel) roomTable.getModel();
        model.setRowCount(0);

        List<Room> rooms = new RoomDAO().getAllRooms();
        for (Room r : rooms) {
            boolean typeMatch = selectedType.equals("All") || r.getType().equalsIgnoreCase(selectedType);
            boolean statusMatch = selectedStatus.equals("All") || r.getStatus().equalsIgnoreCase(selectedStatus);
            if (typeMatch && statusMatch) {
                model.addRow(new Object[]{r.getRoomId(), r.getRoomNumber(), r.getType(), r.getPrice(), r.getStatus()});
            }
        }
    }

    public static void main(String[] args) {
        User dummy = new User("demo", "123", "Admin");
        MainMenu dummyMenu = new MainMenu(dummy);
        dummyMenu.setVisible(false);

        SwingUtilities.invokeLater(() -> new SearchFilterScreen(dummy, dummyMenu).setVisible(true));
    }
}
