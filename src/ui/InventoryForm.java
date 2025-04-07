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
    private final User currentUser;
    private final MainMenu mainMenu;

    public InventoryForm(User user, MainMenu menu) {
        this.currentUser = user;
        this.mainMenu = menu;

        setTitle("📦 Inventory Management");
        setSize(550, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(main);

        // Form
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
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

        JButton addBtn = new JButton("Add Item");
        JButton backBtn = new JButton("Back");
        form.add(addBtn);
        form.add(backBtn);

        main.add(form, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Item", "Qty", "Category"}, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        main.add(scrollPane, BorderLayout.CENTER);

        loadItems();

        addBtn.addActionListener(e -> addItem());
        backBtn.addActionListener(e -> {
            mainMenu.setVisible(true);
            dispose();
        });
    }

    private void addItem() {
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
                nameField.setText("");
                categoryField.setText("");
                quantityField.setText("");
                thresholdField.setText("");
                loadItems();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to add item.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Quantity and threshold must be numbers.");
        }
    }

    private void loadItems() {
        tableModel.setRowCount(0);
        List<InventoryItem> items = new InventoryDAO().getAllItems();
        for (InventoryItem i : items) {
            tableModel.addRow(new Object[]{
                    i.getItemName(),
                    i.getQuantity() + (i.getQuantity() <= i.getThreshold() ? " ⚠️ LOW" : ""),
                    i.getCategory()
            });
        }
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
