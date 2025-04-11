package ui;

import db.GuestDAO;
import db.InvoiceDAO;
import db.ReservationDAO;
import db.RoomDAO;
import models.Guest;
import models.Invoice;
import models.Reservation;
import models.Room;
import models.User;
import main.MainMenu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GuestHistoryScreen extends JFrame {
    private final User currentUser;
    private final MainMenu mainMenu;
    private final JComboBox<Guest> guestCombo;
    private final DefaultTableModel resModel, billModel;

    public GuestHistoryScreen(User user, MainMenu menu) {
        this.currentUser = user;
        this.mainMenu = menu;

        setTitle("📜 Guest History Tracker");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel();

        guestCombo = new JComboBox<>();
        loadGuests();
        JButton viewBtn = new JButton("🔍 View History");
        JButton backBtn = new JButton("⬅ Back");

        topPanel.add(new JLabel("Select Guest:"));
        topPanel.add(guestCombo);
        topPanel.add(viewBtn);
        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        // Reservation table with Room Number column
        resModel = new DefaultTableModel(new String[]{
                "Reservation ID", "Room ID", "Room Number", "Check-In", "Check-Out", "Status", "Requests"
        }, 0);
        JTable resTable = new JTable(resModel);

        // Invoice table
        billModel = new DefaultTableModel(new String[]{"Invoice ID", "Reservation ID", "Issue Date", "Amount ($)"}, 0);
        JTable billTable = new JTable(billModel);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("📅 Reservations", new JScrollPane(resTable));
        tabs.addTab("💳 Billing", new JScrollPane(billTable));
        add(tabs, BorderLayout.CENTER);

        viewBtn.addActionListener(e -> loadGuestHistory());
        backBtn.addActionListener(e -> {
            mainMenu.setVisible(true);
            dispose();
        });
    }

    private void loadGuests() {
        guestCombo.removeAllItems();
        for (Guest g : new GuestDAO().getAllGuests()) {
            guestCombo.addItem(g);
        }
    }

    private void loadGuestHistory() {
        Guest selected = (Guest) guestCombo.getSelectedItem();
        if (selected == null) return;

        int guestId = selected.getGuestId();

        // Load Reservations
        resModel.setRowCount(0);
        RoomDAO roomDAO = new RoomDAO();
        for (Reservation r : new ReservationDAO().getAllReservations()) {
            if (r.getGuestId() == guestId) {
                Room room = roomDAO.getRoomById(r.getRoomId());
                String roomNumber = (room != null) ? room.getRoomNumber() : "N/A";
                resModel.addRow(new Object[]{
                        r.getReservationId(),
                        r.getRoomId(),
                        roomNumber,
                        r.getCheckIn(),
                        r.getCheckOut(),
                        r.getPaymentStatus(),
                        r.getSpecialRequests()
                });
            }
        }

        // Load Invoices
        billModel.setRowCount(0);
        for (Invoice i : new InvoiceDAO().getAllInvoices()) {
            if (i.getGuestId() == guestId) {
                billModel.addRow(new Object[]{
                        i.getInvoiceId(),
                        i.getReservationId(),
                        i.getIssueDate(),
                        String.format("%.2f", i.getTotalAmount())
                });
            }
        }
    }

    public static void main(String[] args) {
        User dummy = new User();
        dummy.setUsername("admin");
        dummy.setRole("Admin");

        MainMenu menu = new MainMenu(dummy);
        menu.setVisible(false);
        new GuestHistoryScreen(dummy, menu).setVisible(true);
    }
}
