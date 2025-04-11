package ui;

import db.PromotionDAO;
import db.ReservationDAO;
import models.Promotion;
import models.Reservation;
import models.User;
import main.MainMenu;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class PromotionForm extends JFrame {
    private final User currentUser;
    private final MainMenu mainMenu;

    private JTextField codeField, valueField;
    private JComboBox<String> typeCombo;
    private JComboBox<Reservation> reservationCombo;
    private JCheckBox globalBox;
    private JDateChooser startChooser, endChooser;
    private JTable promoTable;
    private DefaultTableModel model;

    public PromotionForm(User user, MainMenu menu) {
        this.currentUser = user;
        this.mainMenu = menu;

        setTitle("🎁 Manage Promotions");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Form Panel ---
        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Promotion Entry"));

        codeField = new JTextField();
        valueField = new JTextField();
        typeCombo = new JComboBox<>(new String[]{"Percentage", "Flat"});
        reservationCombo = new JComboBox<>();
        globalBox = new JCheckBox("Global");
        startChooser = new JDateChooser();
        endChooser = new JDateChooser();

        form.add(new JLabel("Code:"));
        form.add(codeField);
        form.add(new JLabel("Discount Type:"));
        form.add(typeCombo);
        form.add(new JLabel("Value:"));
        form.add(valueField);
        form.add(new JLabel("Reservation:"));
        form.add(reservationCombo);
        form.add(new JLabel("Start Date:"));
        form.add(startChooser);
        form.add(new JLabel("End Date:"));
        form.add(endChooser);
        form.add(globalBox);
        form.add(new JLabel());

        add(form, BorderLayout.NORTH);

        // --- Buttons ---
        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("💾 Save");
        JButton updateBtn = new JButton("✏️ Update");
        JButton deleteBtn = new JButton("🗑 Delete");
        JButton backBtn = new JButton("⬅ Back");
        btnPanel.add(saveBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(backBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // --- Table ---
        model = new DefaultTableModel(new String[]{"ID", "Code", "Type", "Value", "ResID", "Global", "Start", "End"}, 0);
        promoTable = new JTable(model);
        add(new JScrollPane(promoTable), BorderLayout.CENTER);

        loadReservations();
        loadPromotions();

        // --- Listeners ---
        globalBox.addActionListener(e -> reservationCombo.setEnabled(!globalBox.isSelected()));
        promoTable.getSelectionModel().addListSelectionListener(e -> loadSelectedPromotion());
        saveBtn.addActionListener(e -> savePromotion());
        updateBtn.addActionListener(e -> updatePromotion());
        deleteBtn.addActionListener(e -> deletePromotion());
        backBtn.addActionListener(e -> {
            dispose();
            mainMenu.setVisible(true);
        });
    }

    private void loadReservations() {
        reservationCombo.removeAllItems();
        for (Reservation r : new ReservationDAO().getAllReservations()) {
            reservationCombo.addItem(r);
        }
    }

    private void loadPromotions() {
        model.setRowCount(0);
        for (Promotion p : new PromotionDAO().getAllPromotions()) {
            model.addRow(new Object[]{
                    p.getId(), p.getCode(), p.getDiscountType(), p.getDiscountValue(),
                    p.getReservationId(), p.isGlobal(), p.getStartDate(), p.getEndDate()
            });
        }
    }

    private void loadSelectedPromotion() {
        int row = promoTable.getSelectedRow();
        if (row == -1) return;

        codeField.setText(model.getValueAt(row, 1).toString());
        typeCombo.setSelectedItem(model.getValueAt(row, 2).toString());
        valueField.setText(model.getValueAt(row, 3).toString());
        globalBox.setSelected((boolean) model.getValueAt(row, 5));

        if (!globalBox.isSelected()) {
            int resId = Integer.parseInt(model.getValueAt(row, 4).toString());
            for (int i = 0; i < reservationCombo.getItemCount(); i++) {
                if (reservationCombo.getItemAt(i).getReservationId() == resId) {
                    reservationCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        try {
            startChooser.setDate(new SimpleDateFormat("yyyy-MM-dd").parse((String) model.getValueAt(row, 6)));
            endChooser.setDate(new SimpleDateFormat("yyyy-MM-dd").parse((String) model.getValueAt(row, 7)));
        } catch (Exception ignored) {}
    }

    private void savePromotion() {
        try {
            Promotion p = extractFormValues(-1);
            boolean ok = new PromotionDAO().addPromotion(p);
            if (ok) {
                JOptionPane.showMessageDialog(this, "✅ Added");
                loadPromotions();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to add");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Invalid input");
        }
    }

    private void updatePromotion() {
        int row = promoTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "❌ Select a promotion to update");
            return;
        }

        try {
            int id = (int) model.getValueAt(row, 0);
            Promotion p = extractFormValues(id);
            boolean ok = new PromotionDAO().updatePromotion(p);
            if (ok) {
                JOptionPane.showMessageDialog(this, "✅ Updated");
                loadPromotions();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to update");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Invalid input");
        }
    }

    private void deletePromotion() {
        int row = promoTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "❌ Select a promotion to delete");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this promotion?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = new PromotionDAO().deletePromotion(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "🗑 Deleted");
                loadPromotions();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to delete");
            }
        }
    }

    private Promotion extractFormValues(int id) {
        String code = codeField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        double value = Double.parseDouble(valueField.getText().trim());
        int resId = globalBox.isSelected() ? -1 : ((Reservation) reservationCombo.getSelectedItem()).getReservationId();
        String start = new SimpleDateFormat("yyyy-MM-dd").format(startChooser.getDate());
        String end = new SimpleDateFormat("yyyy-MM-dd").format(endChooser.getDate());

        Promotion p = new Promotion();
        p.setId(id);
        p.setCode(code);
        p.setDiscountType(type);
        p.setDiscountValue(value);
        p.setReservationId(resId);
        p.setStartDate(start);
        p.setEndDate(end);
        p.setGlobal(globalBox.isSelected());
        return p;
    }

    private void clearForm() {
        codeField.setText("");
        valueField.setText("");
        startChooser.setDate(null);
        endChooser.setDate(null);
        reservationCombo.setSelectedIndex(0);
        globalBox.setSelected(false);
    }
}
