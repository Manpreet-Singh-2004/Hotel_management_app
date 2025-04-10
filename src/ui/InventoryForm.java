package ui;

import db.InventoryDAO;
import models.InventoryItem;
import models.User;
import main.MainMenu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryForm extends JFrame {
    private JTextField nameField, categoryField, quantityField, thresholdField;
    private DefaultTableModel tableModel;
    private JTable table;
    private final User currentUser;
    private final MainMenu mainMenu;

    public InventoryForm(User user, MainMenu menu) {
        this.currentUser = user;
        this.mainMenu = menu;

        setTitle("📦 Inventory Management");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(main);

        // Form Panel
        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        nameField = new JTextField();
        categoryField = new JTextField();
        quantityField = new JTextField();
        thresholdField = new JTextField();

        form.add(new JLabel("Item Name:"));
        form.add(nameField);
        form.add(new JLabel("Category:"));
        form.add(categoryField);
        form.add(new JLabel("Quantity:"));
        form.add(quantityField);
        form.add(new JLabel("Threshold:"));
        form.add(thresholdField);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton backBtn = new JButton("Back");

        form.add(addBtn);
        form.add(updateBtn);
        form.add(deleteBtn);
        form.add(backBtn);

        main.add(form, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"ID", "Item", "Qty", "Category"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        main.add(scrollPane, BorderLayout.CENTER);

        loadItems();

        // Fill form on row select
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                nameField.setText(table.getValueAt(row, 1).toString());
                String qtyStr = table.getValueAt(row, 2).toString().split(" ")[0]; // remove LOW warning
                quantityField.setText(qtyStr);
                categoryField.setText(table.getValueAt(row, 3).toString());
                thresholdField.setText(""); // optional to manually refill
            }
        });

        addBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String category = categoryField.getText().trim();
                int qty = Integer.parseInt(quantityField.getText().trim());
                int threshold = Integer.parseInt(thresholdField.getText().trim());

                if (name.isEmpty() || category.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "❌ All fields are required.");
                    return;
                }

                InventoryItem item = new InventoryItem(name, category, qty, threshold);
                boolean success = new InventoryDAO().addItem(item);

                if (success) {
                    JOptionPane.showMessageDialog(this, "✅ Item added!");
                    clearFields();
                    loadItems();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed to add item.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Invalid input.");
            }
        });

        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "❌ Select a row to update.");
                return;
            }

            try {
                int id = (int) table.getValueAt(row, 0);
                String name = nameField.getText().trim();
                String category = categoryField.getText().trim();
                int qty = Integer.parseInt(quantityField.getText().trim());
                int threshold = Integer.parseInt(thresholdField.getText().trim());

                InventoryItem item = new InventoryItem(name, category, qty, threshold);
                item.setItemId(id);

                boolean updated = new InventoryDAO().updateItem(item);
                if (updated) {
                    JOptionPane.showMessageDialog(this, "✅ Item updated!");
                    clearFields();
                    loadItems();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Update failed.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Invalid input.");
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "❌ Select a row to delete.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = (int) table.getValueAt(row, 0);
                boolean deleted = new InventoryDAO().deleteItem(id);
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "✅ Item deleted!");
                    clearFields();
                    loadItems();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Deletion failed.");
                }
            }
        });

        backBtn.addActionListener(e -> {
            mainMenu.setVisible(true);
            dispose();
        });
    }

    private void loadItems() {
        tableModel.setRowCount(0);
        List<InventoryItem> items = new InventoryDAO().getAllItems();
        for (InventoryItem i : items) {
            tableModel.addRow(new Object[]{
                    i.getItemId(),
                    i.getItemName(),
                    i.getQuantity() + (i.getQuantity() <= i.getThreshold() ? " ⚠️ LOW" : ""),
                    i.getCategory()
            });
        }
    }

    private void clearFields() {
        nameField.setText("");
        categoryField.setText("");
        quantityField.setText("");
        thresholdField.setText("");
    }

    public static void main(String[] args) {
        User dummy = new User();
        dummy.setUsername("admin");
        dummy.setRole("Admin");

        MainMenu menu = new MainMenu(dummy);
        menu.setVisible(false);
        SwingUtilities.invokeLater(() -> new InventoryForm(dummy, menu).setVisible(true));
    }
}
