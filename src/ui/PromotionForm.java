package ui;

import db.PromotionDAO;
import db.ReservationDAO;
import models.Promotion;
import models.Reservation;
import models.User;
import main.MainMenu;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class PromotionForm extends JFrame {
    private JTextField codeField, discountValueField;
    private JComboBox<String> discountTypeCombo;
    private JComboBox<Reservation> reservationCombo;
    private JCheckBox globalCheck;
    private JDateChooser startDateChooser, endDateChooser;
    private final User currentUser;
    private final MainMenu mainMenu;

    public PromotionForm(User user, MainMenu menu) {
        this.currentUser = user;
        this.mainMenu = menu;

        setTitle("🎁 Promotion Entry");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        codeField = new JTextField();
        discountValueField = new JTextField();
        discountTypeCombo = new JComboBox<>(new String[]{"Percentage", "Flat"});
        reservationCombo = new JComboBox<>();
        globalCheck = new JCheckBox("Global Promotion");
        startDateChooser = new JDateChooser();
        endDateChooser = new JDateChooser();

        panel.add(new JLabel("Code:"));
        panel.add(codeField);
        panel.add(new JLabel("Discount Type:"));
        panel.add(discountTypeCombo);
        panel.add(new JLabel("Discount Value:"));
        panel.add(discountValueField);
        panel.add(new JLabel("Apply to Reservation:"));
        panel.add(reservationCombo);
        panel.add(new JLabel("Start Date:"));
        panel.add(startDateChooser);
        panel.add(new JLabel("End Date:"));
        panel.add(endDateChooser);
        panel.add(globalCheck);
        panel.add(new JLabel()); // filler

        JButton saveBtn = new JButton("Save");
        JButton backBtn = new JButton("Back");
        panel.add(saveBtn);
        panel.add(backBtn);

        add(panel);

        loadReservations();

        globalCheck.addActionListener(e -> reservationCombo.setEnabled(!globalCheck.isSelected()));
        saveBtn.addActionListener(e -> savePromotion());
        backBtn.addActionListener(e -> {
            dispose();
            mainMenu.setVisible(true);
        });
    }

    private void loadReservations() {
        reservationCombo.removeAllItems();
        List<Reservation> reservations = new ReservationDAO().getAllReservations();
        for (Reservation r : reservations) {
            reservationCombo.addItem(r);
        }
    }

    private void savePromotion() {
        try {
            String code = codeField.getText().trim();
            String type = discountTypeCombo.getSelectedItem().toString();
            double value = Double.parseDouble(discountValueField.getText().trim());
            int reservationId = globalCheck.isSelected() ? -1 : ((Reservation) reservationCombo.getSelectedItem()).getReservationId();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = (startDateChooser.getDate() != null) ? sdf.format(startDateChooser.getDate()) : null;
            String endDate = (endDateChooser.getDate() != null) ? sdf.format(endDateChooser.getDate()) : null;

            Promotion promo = new Promotion();
            promo.setCode(code);
            promo.setDiscountType(type);
            promo.setDiscountValue(value);
            promo.setReservationId(reservationId);
            promo.setStartDate(startDate);
            promo.setEndDate(endDate);
            promo.setGlobal(globalCheck.isSelected());

            boolean success = new PromotionDAO().addPromotion(promo);
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Promotion saved!");
                codeField.setText("");
                discountValueField.setText("");
                globalCheck.setSelected(false);
                reservationCombo.setEnabled(true);
                startDateChooser.setDate(null);
                endDateChooser.setDate(null);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to save promotion.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Invalid input.");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        User dummy = new User("admin", "123", "Admin");
        MainMenu dummyMenu = new MainMenu(dummy);
        dummyMenu.setVisible(false);
        SwingUtilities.invokeLater(() -> new PromotionForm(dummy, dummyMenu).setVisible(true));
    }
}
