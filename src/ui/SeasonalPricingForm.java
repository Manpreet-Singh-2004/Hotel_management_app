// src/ui/SeasonalPricingForm.java
package ui;

import db.SeasonalPricingDAO;
import models.SeasonalPricing;
import models.User;
import db.DBConnection;
import main.MainMenu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.util.List;

public class SeasonalPricingForm extends JFrame {
    private final SeasonalPricingDAO dao;
    private final DefaultTableModel tableModel;
    private final JTable pricingTable;
    private final MainMenu mainMenu;

    public SeasonalPricingForm(Connection conn, MainMenu mainMenu) {
        this.dao = new SeasonalPricingDAO(conn);
        this.mainMenu = mainMenu;

        setTitle("📅 Seasonal Pricing Management");
        setSize(800, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"ID", "Start Date", "End Date", "Multiplier", "Description"}, 0);
        pricingTable = new JTable(tableModel);
        add(new JScrollPane(pricingTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("➕ Add Rule");
        JButton updateBtn = new JButton("✏️ Update Rule");
        JButton deleteBtn = new JButton("❌ Delete Rule");
        JButton refreshBtn = new JButton("🔄 Refresh");
        JButton backBtn = new JButton("🔙 Back");

        addBtn.addActionListener(this::addRule);
        updateBtn.addActionListener(this::updateRule);
        deleteBtn.addActionListener(this::deleteRule);
        refreshBtn.addActionListener(e -> loadData());
        backBtn.addActionListener(e -> {
            this.dispose();
            mainMenu.setVisible(true);
        });

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(backBtn);

        add(btnPanel, BorderLayout.SOUTH);
        loadData();
        setVisible(true);
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            List<SeasonalPricing> list = dao.getAll();
            for (SeasonalPricing s : list) {
                tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getStartDate(),
                        s.getEndDate(),
                        s.getPriceMultiplier(),
                        s.getDescription()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + e.getMessage());
        }
    }

    private void addRule(ActionEvent e) {
        try {
            String start = JOptionPane.showInputDialog(this, "Start Date (YYYY-MM-DD):");
            String end = JOptionPane.showInputDialog(this, "End Date (YYYY-MM-DD):");
            double multiplier = Double.parseDouble(JOptionPane.showInputDialog(this, "Multiplier (e.g. 1.2):"));
            String desc = JOptionPane.showInputDialog(this, "Description:");

            dao.addSeasonalPricing(new SeasonalPricing(0, start, end, multiplier, desc));
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateRule(ActionEvent e) {
        int row = pricingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a rule to update.");
            return;
        }
        try {
            int id = (int) tableModel.getValueAt(row, 0);
            String start = JOptionPane.showInputDialog(this, "New Start Date:", tableModel.getValueAt(row, 1));
            String end = JOptionPane.showInputDialog(this, "New End Date:", tableModel.getValueAt(row, 2));
            double multiplier = Double.parseDouble(JOptionPane.showInputDialog(this, "New Multiplier:", tableModel.getValueAt(row, 3)));
            String desc = JOptionPane.showInputDialog(this, "New Description:", tableModel.getValueAt(row, 4));

            dao.updateSeasonalPricing(new SeasonalPricing(id, start, end, multiplier, desc));
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
        }
    }

    private void deleteRule(ActionEvent e) {
        int row = pricingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a rule to delete.");
            return;
        }
        try {
            int id = (int) tableModel.getValueAt(row, 0);
            dao.deleteSeasonalPricing(id);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        MainMenu dummy = new MainMenu(new User("admin", "123", "Admin"));
        dummy.setVisible(false);
        SwingUtilities.invokeLater(() -> new SeasonalPricingForm(DBConnection.getInstance().getConnection(), dummy));
    }
}
