package ui;

import com.toedter.calendar.JDateChooser;
import db.GuestDAO;
import db.ReservationDAO;
import db.RoomDAO;
import models.Guest;
import models.Reservation;
import models.Room;
import models.User;
import main.MainMenu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReservationForm extends JFrame {
    private final JComboBox<Guest> guestCombo;
    private final JComboBox<Room> roomCombo;
    private final JDateChooser checkInChooser;
    private final JDateChooser checkOutChooser;
    private final JComboBox<String> statusCombo;
    private final JTextField requestsField;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final User currentUser;
    private final MainMenu mainMenu;

    public ReservationForm(User user, MainMenu menu) {
        this.currentUser = user;
        this.mainMenu = menu;

        setTitle("📅 Room Reservation");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(mainPanel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Create / Update Reservation"));

        guestCombo = new JComboBox<>();
        roomCombo = new JComboBox<>();
        checkInChooser = new JDateChooser();
        checkOutChooser = new JDateChooser();
        statusCombo = new JComboBox<>(new String[]{"Pending", "Paid"});
        requestsField = new JTextField();

        formPanel.add(new JLabel("Guest:"));
        formPanel.add(guestCombo);
        formPanel.add(new JLabel("Room:"));
        formPanel.add(roomCombo);
        formPanel.add(new JLabel("Check-in:"));
        formPanel.add(checkInChooser);
        formPanel.add(new JLabel("Check-out:"));
        formPanel.add(checkOutChooser);
        formPanel.add(new JLabel("Payment Status:"));
        formPanel.add(statusCombo);
        formPanel.add(new JLabel("Special Requests:"));
        formPanel.add(requestsField);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton addBtn = new JButton("✅ Book");
        JButton updateBtn = new JButton("✏️ Update");
        JButton deleteBtn = new JButton("🗑 Delete");
        JButton backBtn = new JButton("⬅ Back");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(backBtn);

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.add(formPanel, BorderLayout.CENTER);
        top.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(top, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Guest", "Room", "Check-in", "Check-out", "Status", "Requests"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        loadGuests();
        loadRooms();
        loadReservations();

        // Actions
        addBtn.addActionListener(e -> addReservation());
        updateBtn.addActionListener(e -> updateReservation());
        deleteBtn.addActionListener(e -> deleteReservation());

        table.getSelectionModel().addListSelectionListener(e -> populateFormFromSelected());

        backBtn.addActionListener(e -> {
            mainMenu.setVisible(true);
            dispose();
        });
    }

    private void loadGuests() {
        guestCombo.removeAllItems();
        for (Guest g : new GuestDAO().getAllGuests()) guestCombo.addItem(g);
    }

    private void loadRooms() {
        roomCombo.removeAllItems();
        for (Room r : new RoomDAO().getAllRooms()) roomCombo.addItem(r);
    }

    private void loadReservations() {
        tableModel.setRowCount(0);
        for (Reservation r : new ReservationDAO().getAllReservations()) {
            Guest g = new GuestDAO().getGuestById(r.getGuestId());
            Room room = new RoomDAO().getRoomById(r.getRoomId());

            tableModel.addRow(new Object[]{
                    r.getReservationId(),
                    g != null ? g.getFullName() : "Unknown",
                    room != null ? room.getRoomNumber() : "Unknown",
                    r.getCheckIn(),
                    r.getCheckOut(),
                    r.getPaymentStatus(),
                    r.getSpecialRequests()
            });
        }
    }

    private void addReservation() {
        try {
            Guest guest = (Guest) guestCombo.getSelectedItem();
            Room room = (Room) roomCombo.getSelectedItem();
            Date in = checkInChooser.getDate();
            Date out = checkOutChooser.getDate();
            String status = (String) statusCombo.getSelectedItem();
            String notes = requestsField.getText();

            if (guest == null || room == null || in == null || out == null || out.before(in)) {
                throw new Exception("⚠️ Invalid or missing information.");
            }

            String checkIn = new SimpleDateFormat("yyyy-MM-dd").format(in);
            String checkOut = new SimpleDateFormat("yyyy-MM-dd").format(out);

            Reservation res = new Reservation(guest.getGuestId(), room.getRoomId(), checkIn, checkOut, status, notes);
            boolean ok = new ReservationDAO().addReservation(res);

            if (ok) {
                JOptionPane.showMessageDialog(this, "✅ Reservation added!");
                loadReservations();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to add reservation.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void updateReservation() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "❌ Select a reservation to update.");
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(row, 0);
            Guest guest = (Guest) guestCombo.getSelectedItem();
            Room room = (Room) roomCombo.getSelectedItem();
            Date in = checkInChooser.getDate();
            Date out = checkOutChooser.getDate();
            String status = (String) statusCombo.getSelectedItem();
            String notes = requestsField.getText();

            if (guest == null || room == null || in == null || out == null || out.before(in)) {
                throw new Exception("⚠️ Invalid or missing information.");
            }

            String checkIn = new SimpleDateFormat("yyyy-MM-dd").format(in);
            String checkOut = new SimpleDateFormat("yyyy-MM-dd").format(out);

            Reservation updated = new Reservation(id, guest.getGuestId(), room.getRoomId(), checkIn, checkOut, status, notes);
            boolean ok = new ReservationDAO().updateReservation(updated);

            if (ok) {
                JOptionPane.showMessageDialog(this, "✅ Reservation updated!");
                loadReservations();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to update reservation.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void deleteReservation() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "❌ Select a reservation to delete.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this reservation?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = new ReservationDAO().deleteReservation(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "🗑 Reservation deleted!");
                loadReservations();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to delete reservation.");
            }
        }
    }

    private void populateFormFromSelected() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String guestName = tableModel.getValueAt(row, 1).toString();
        String roomNum = tableModel.getValueAt(row, 2).toString();
        String checkIn = tableModel.getValueAt(row, 3).toString();
        String checkOut = tableModel.getValueAt(row, 4).toString();
        String status = tableModel.getValueAt(row, 5).toString();
        String notes = tableModel.getValueAt(row, 6).toString();

        for (int i = 0; i < guestCombo.getItemCount(); i++) {
            if (guestCombo.getItemAt(i).getFullName().equals(guestName)) {
                guestCombo.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < roomCombo.getItemCount(); i++) {
            if (roomCombo.getItemAt(i).getRoomNumber().equals(roomNum)) {
                roomCombo.setSelectedIndex(i);
                break;
            }
        }

        try {
            checkInChooser.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(checkIn));
            checkOutChooser.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(checkOut));
        } catch (Exception ignored) {}

        statusCombo.setSelectedItem(status);
        requestsField.setText(notes);
    }

    public static void main(String[] args) {
        User dummy = new User("admin", "admin123", "Admin");
        SwingUtilities.invokeLater(() -> new ReservationForm(dummy, null).setVisible(true));
    }
}
