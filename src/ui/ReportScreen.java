package ui;

import db.GuestDAO;
import db.RoomDAO;
import db.InvoiceDAO;
import models.User;
import main.MainMenu;

import javax.swing.*;
import java.awt.*;

public class ReportScreen extends JFrame {
    private final User currentUser;
    private final MainMenu mainMenu;

    public ReportScreen(User user, MainMenu menu) {
        this.currentUser = user;
        this.mainMenu = menu;

        setTitle("📊 Hotel Report Dashboard");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        int guestCount = new GuestDAO().getAllGuests().size();
        int roomCount = new RoomDAO().getAllRooms().size();
        double totalIncome = new InvoiceDAO().getAllInvoices()
                .stream().mapToDouble(i -> i.getTotalAmount()).sum();

        JLabel guestsLabel = new JLabel("👤 Total Guests: " + guestCount);
        JLabel roomsLabel = new JLabel("🛏️ Total Rooms: " + roomCount);
        JLabel incomeLabel = new JLabel("💰 Total Income: $" + String.format("%.2f", totalIncome));

        JButton backBtn = new JButton("⬅ Back");

        panel.add(guestsLabel);
        panel.add(roomsLabel);
        panel.add(incomeLabel);
        panel.add(backBtn);
        add(panel);

        backBtn.addActionListener(e -> {
            dispose();
            mainMenu.setVisible(true);
        });
    }

    public static void main(String[] args) {
        User dummy = new User("demo", "123", "Admin");
        MainMenu dummyMenu = new MainMenu(dummy);
        dummyMenu.setVisible(false);

        SwingUtilities.invokeLater(() -> new ReportScreen(dummy, dummyMenu).setVisible(true));
    }
}
