package ui;

import db.DBInitializer;
import db.InvoiceDAO;
import db.ReservationDAO;
import db.RoomDAO;
import db.PromotionDAO;
import models.Invoice;
import models.Reservation;
import models.Room;
import models.Promotion;
import models.User;
import main.MainMenu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BillingScreen extends JFrame {
    private JComboBox<ReservationItem> reservationCombo;
    private JTextField guestIdField, dateField, totalField, roomInfoField, stayField;
    private DefaultTableModel tableModel;
    private JLabel promoLabel;
    private final User currentUser;
    private final MainMenu mainMenu;

    public BillingScreen(User user, MainMenu menu) {
        this.currentUser = user;
        this.mainMenu = menu;

        setTitle("💳 Invoice / Billing System");
        setSize(700, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        DBInitializer.initialize();

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(main);

        // Form
        JPanel form = new JPanel(new GridLayout(8, 2, 10, 10));
        reservationCombo = new JComboBox<>();
        guestIdField = new JTextField();
        dateField = new JTextField(LocalDate.now().toString());
        roomInfoField = new JTextField();
        stayField = new JTextField();
        totalField = new JTextField();
        promoLabel = new JLabel("🎁 No promotion applied");

        guestIdField.setEditable(false);
        roomInfoField.setEditable(false);
        stayField.setEditable(false);
        totalField.setEditable(false);

        form.add(new JLabel("Reservation:"));
        form.add(reservationCombo);
        form.add(new JLabel("Guest ID:"));
        form.add(guestIdField);
        form.add(new JLabel("Room Info:"));
        form.add(roomInfoField);
        form.add(new JLabel("Stay Duration:"));
        form.add(stayField);
        form.add(new JLabel("Issue Date:"));
        form.add(dateField);
        form.add(new JLabel("Promotion:"));
        form.add(promoLabel);
        form.add(new JLabel("Total Amount ($):"));
        form.add(totalField);

        JButton generateBtn = new JButton("🧾 Generate Invoice");
        JButton backBtn = new JButton("Back");
        form.add(generateBtn);
        form.add(backBtn);

        main.add(form, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Invoice ID", "Reservation ID", "Guest ID", "Date", "Total"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        main.add(scrollPane, BorderLayout.CENTER);

        loadReservations();
        loadInvoices();

        reservationCombo.addActionListener(e -> updateSummary());

        generateBtn.addActionListener(e -> generateInvoice());
        backBtn.addActionListener(e -> {
            dispose();
            mainMenu.setVisible(true);
        });
    }

    private void loadReservations() {
        reservationCombo.removeAllItems();
        List<Reservation> resList = new ReservationDAO().getAllReservations();
        for (Reservation r : resList) {
            reservationCombo.addItem(new ReservationItem(r));
        }
        updateSummary();
    }

    private void updateSummary() {
        ReservationItem item = (ReservationItem) reservationCombo.getSelectedItem();
        if (item == null) return;

        Reservation r = item.reservation;
        Room room = new RoomDAO().getRoomById(r.getRoomId());
        if (room == null) return;

        long days = ChronoUnit.DAYS.between(LocalDate.parse(r.getCheckIn()), LocalDate.parse(r.getCheckOut()));
        double baseAmount = room.getPrice() * days;

        guestIdField.setText(String.valueOf(r.getGuestId()));
        roomInfoField.setText(room.getRoomNumber() + " - " + room.getType());
        stayField.setText(days + " nights");

        Promotion promo = new PromotionDAO().getValidPromotionForReservation(r.getReservationId());
        double discount = 0;

        if (promo != null) {
            if (promo.getDiscountType().equalsIgnoreCase("Percentage")) {
                discount = baseAmount * (promo.getDiscountValue() / 100.0);
            } else {
                discount = promo.getDiscountValue();
            }
            promoLabel.setText("🎉 " + promo.getCode() + " (" + promo.getDiscountType() + " off)");
        } else {
            promoLabel.setText("🎁 No promotion applied");
        }

        double finalTotal = baseAmount - discount;
        totalField.setText(String.format("%.2f", finalTotal));
    }

    private void generateInvoice() {
        try {
            ReservationItem item = (ReservationItem) reservationCombo.getSelectedItem();
            if (item == null) return;

            int reservationId = item.reservation.getReservationId();
            int guestId = item.reservation.getGuestId();
            String date = dateField.getText().trim();
            double total = Double.parseDouble(totalField.getText().trim());

            Invoice invoice = new Invoice(reservationId, guestId, date, total);
            boolean success = new InvoiceDAO().addInvoice(invoice);

            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Invoice generated!");
                loadInvoices();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to generate invoice.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Invalid input!");
        }
    }

    private void loadInvoices() {
        tableModel.setRowCount(0);
        for (Invoice i : new InvoiceDAO().getAllInvoices()) {
            tableModel.addRow(new Object[]{
                    i.getInvoiceId(),
                    i.getReservationId(),
                    i.getGuestId(),
                    i.getIssueDate(),
                    "$" + i.getTotalAmount()
            });
        }
    }

    private static class ReservationItem {
        Reservation reservation;

        ReservationItem(Reservation r) {
            this.reservation = r;
        }

        @Override
        public String toString() {
            return "Reservation #" + reservation.getReservationId() + " | Guest ID: " + reservation.getGuestId();
        }
    }

    public static void main(String[] args) {
        User dummy = new User("admin", "123", "Admin");
        MainMenu menu = new MainMenu(dummy);
        menu.setVisible(false);

        SwingUtilities.invokeLater(() -> new BillingScreen(dummy, menu).setVisible(true));
    }
}
