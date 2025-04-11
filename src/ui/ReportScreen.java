package ui;

import db.GuestDAO;
import db.RoomDAO;
import db.InvoiceDAO;
import db.ReservationDAO;
import models.User;
import main.MainMenu;
import models.Invoice;
import models.Reservation;
import models.Room;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ReportScreen extends JFrame {
    private final User currentUser;
    private final MainMenu mainMenu;

    public ReportScreen(User user, MainMenu menu) {
        this.currentUser = user;
        this.mainMenu = menu;

        setTitle("📊 Detailed Hotel Report");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GuestDAO guestDAO = new GuestDAO();
        RoomDAO roomDAO = new RoomDAO();
        InvoiceDAO invoiceDAO = new InvoiceDAO();
        ReservationDAO reservationDAO = new ReservationDAO();

        List<Invoice> invoices = invoiceDAO.getAllInvoices();
        List<Reservation> reservations = reservationDAO.getAllReservations();
        List<Room> rooms = roomDAO.getAllRooms();

        int guestCount = guestDAO.getAllGuests().size();
        int roomCount = rooms.size();
        int reservationCount = reservations.size();

        // Avoid duplicate income by summing only unique reservation_id entries
        Set<Integer> seenReservationIds = new HashSet<>();
        double totalIncome = 0;
        for (Invoice i : invoices) {
            if (!seenReservationIds.contains(i.getReservationId())) {
                totalIncome += i.getTotalAmount();
                seenReservationIds.add(i.getReservationId());
            }
        }

        // Most popular room type
        Map<String, Integer> roomTypeCount = new HashMap<>();
        for (Reservation r : reservations) {
            Room room = roomDAO.getRoomById(r.getRoomId());
            if (room != null) {
                roomTypeCount.put(room.getType(), roomTypeCount.getOrDefault(room.getType(), 0) + 1);
            }
        }

        String popularRoomType = roomTypeCount.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + " (" + e.getValue() + " bookings)")
                .orElse("N/A");

        // UI Labels
        panel.add(new JLabel("👤 Total Guests: " + guestCount));
        panel.add(new JLabel("🛏️ Total Rooms: " + roomCount));
        panel.add(new JLabel("📆 Total Reservations: " + reservationCount));
        panel.add(new JLabel("💰 Total Income: $" + String.format("%.2f", totalIncome)));
        panel.add(new JLabel("🔥 Most Popular Room Type: " + popularRoomType));

        JButton backBtn = new JButton("⬅ Back");
        panel.add(backBtn);

        backBtn.addActionListener(e -> {
            dispose();
            mainMenu.setVisible(true);
        });

        add(panel);
    }

    public static void main(String[] args) {
        User dummy = new User("demo", "123", "Admin");
        MainMenu dummyMenu = new MainMenu(dummy);
        dummyMenu.setVisible(false);

        SwingUtilities.invokeLater(() -> new ReportScreen(dummy, dummyMenu).setVisible(true));
    }
}
