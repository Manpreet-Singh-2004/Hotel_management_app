package ui;

import com.toedter.calendar.JDateChooser;
import db.DBConnection;
import db.HousekeepingDAO;
import db.ReservationDAO;
import db.RoomDAO;
import models.HousekeepingTask;
import models.Reservation;
import models.Room;
import models.User;
import main.MainMenu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class LateCheckoutForm extends JFrame {
    private final User currentUser;
    private final MainMenu mainMenu;
    private final Connection conn;
    private final JComboBox<ReservationItem> reservationCombo;
    private final JDateChooser newCheckoutDate;
    private final JTextField chargesField;
    private final DefaultTableModel housekeepingModel;

    public LateCheckoutForm(User user, MainMenu menu, Connection conn) {
        this.currentUser = user;
        this.mainMenu = menu;
        this.conn = conn;

        setTitle("⏰ Late Check-Out");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top panel
        JPanel topPanel = new JPanel();
        reservationCombo = new JComboBox<>();
        loadReservations();
        newCheckoutDate = new JDateChooser();
        chargesField = new JTextField(10);
        chargesField.setEditable(false);
        JButton calculateBtn = new JButton("🧮 Calculate Charges");
        JButton applyBtn = new JButton("✅ Apply Extension");
        JButton backBtn = new JButton("⬅ Back");

        topPanel.add(new JLabel("Reservation:"));
        topPanel.add(reservationCombo);
        topPanel.add(new JLabel("New Check-Out Date:"));
        topPanel.add(newCheckoutDate);
        topPanel.add(calculateBtn);
        topPanel.add(new JLabel("Extra Charges: $"));
        topPanel.add(chargesField);
        topPanel.add(applyBtn);
        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        // Housekeeping task table
        housekeepingModel = new DefaultTableModel(new String[]{"Room ID", "Status", "Deep Clean", "Maintenance"}, 0);
        JTable table = new JTable(housekeepingModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        calculateBtn.addActionListener(e -> calculateCharges());
        applyBtn.addActionListener(e -> applyLateCheckout());
        backBtn.addActionListener(e -> {
            mainMenu.setVisible(true);
            dispose();
        });

        loadHousekeepingTasks();
    }

    private void loadReservations() {
        reservationCombo.removeAllItems();
        for (Reservation r : new ReservationDAO().getAllReservations()) {
            reservationCombo.addItem(new ReservationItem(r));
        }
    }

    private void calculateCharges() {
        ReservationItem item = (ReservationItem) reservationCombo.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "❌ Please select a reservation first.");
            return;
        }

        Date selectedDate = newCheckoutDate.getDate();
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "❌ Please pick a new check-out date.");
            return;
        }

        try {
            LocalDate currentCheckout = LocalDate.parse(item.reservation.getCheckOut());
            LocalDate newCheckout = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (!newCheckout.isAfter(currentCheckout)) {
                JOptionPane.showMessageDialog(this, "⚠️ New check-out must be after current check-out.");
                return;
            }

            long extraDays = ChronoUnit.DAYS.between(currentCheckout, newCheckout);
            Room room = new RoomDAO().getRoomById(item.reservation.getRoomId());

            if (room == null) {
                JOptionPane.showMessageDialog(this, "❌ Failed to retrieve room info.");
                return;
            }

            double charge = extraDays * room.getPrice();
            chargesField.setText(String.format("%.2f", charge));
            JOptionPane.showMessageDialog(this, "💵 Extra charge for " + extraDays + " day(s): $" + String.format("%.2f", charge));

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Something went wrong during calculation.");
        }
    }

    private void applyLateCheckout() {
        ReservationItem item = (ReservationItem) reservationCombo.getSelectedItem();
        if (item == null || newCheckoutDate.getDate() == null) return;

        try {
            String newDateStr = new SimpleDateFormat("yyyy-MM-dd").format(newCheckoutDate.getDate());
            Reservation r = item.reservation;
            r.setCheckOut(newDateStr);

            boolean updated = new ReservationDAO().updateReservation(r);
            if (updated) {
                HousekeepingDAO housekeepingDAO = new HousekeepingDAO(conn);
                housekeepingDAO.addTask(new HousekeepingTask(0, r.getRoomId(), "Scheduled (Late Check-Out)", false, false));
                JOptionPane.showMessageDialog(this, "✅ Late check-out applied.\n🧹 Housekeeping task added.");
                loadHousekeepingTasks();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to update reservation.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error applying update.");
        }
    }

    private void loadHousekeepingTasks() {
        housekeepingModel.setRowCount(0);
        try {
            for (HousekeepingTask task : new HousekeepingDAO(conn).getAllTasks()) {
                if (task.getStatus().toLowerCase().contains("late")) {
                    housekeepingModel.addRow(new Object[]{
                            task.getRoomId(),
                            task.getStatus(),
                            task.isDeepClean(),
                            task.isMaintenance()
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ReservationItem {
        Reservation reservation;

        ReservationItem(Reservation r) {
            this.reservation = r;
        }

        @Override
        public String toString() {
            return "Reservation #" + reservation.getReservationId() +
                    " | Room " + reservation.getRoomId() +
                    " | Current Check-Out: " + reservation.getCheckOut();
        }
    }
}
