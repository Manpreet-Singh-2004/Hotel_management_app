package ui;

import db.HousekeepingDAO;
import models.HousekeepingTask;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class HousekeepingForm extends JFrame {
    private final HousekeepingDAO dao;
    private final JTable taskTable;
    private final DefaultTableModel tableModel;
    private final JFrame parent;

    public HousekeepingForm(Connection conn, JFrame parent) {
        this.dao = new HousekeepingDAO(conn);
        this.parent = parent;

        setTitle("Housekeeping Management");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Task ID", "Room ID", "Status", "Deep Clean", "Maintenance"}, 0);
        taskTable = new JTable(tableModel);
        add(new JScrollPane(taskTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton refreshBtn = new JButton("🔄 Refresh");
        JButton addBtn = new JButton("➕ Add Task");
        JButton updateBtn = new JButton("✏️ Update Task");
        JButton deleteBtn = new JButton("❌ Delete Task");
        JButton backBtn = new JButton("🔙 Back");

        refreshBtn.addActionListener(this::loadTasks);
        addBtn.addActionListener(this::addTask);
        updateBtn.addActionListener(this::updateTask);
        deleteBtn.addActionListener(this::deleteTask);
        backBtn.addActionListener(e -> {
            this.setVisible(false);
            if (parent != null) parent.setVisible(true);
        });

        btnPanel.add(refreshBtn);
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(backBtn);

        add(btnPanel, BorderLayout.SOUTH);

        loadTasks(null);
        setVisible(true);
    }

    private void loadTasks(ActionEvent e) {
        try {
            tableModel.setRowCount(0);
            List<HousekeepingTask> tasks = dao.getAllTasks();
            for (HousekeepingTask t : tasks) {
                tableModel.addRow(new Object[]{
                        t.getTaskId(),
                        t.getRoomId(),
                        t.getStatus(),
                        t.isDeepClean(),
                        t.isMaintenance()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load tasks: " + ex.getMessage());
        }
    }

    private void addTask(ActionEvent e) {
        try {
            String roomNumber = JOptionPane.showInputDialog(this, "Enter Room Number:");
            if (roomNumber == null || roomNumber.trim().isEmpty()) return;

            // ✅ Fetch room_id using room_number
            String fetchIdSql = "SELECT room_id FROM Rooms WHERE room_number = ?";
            int roomId = -1;
            try (PreparedStatement stmt = dao.getConnection().prepareStatement(fetchIdSql)) {
                stmt.setString(1, roomNumber.trim());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    roomId = rs.getInt("room_id");
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Room number '" + roomNumber + "' does not exist.");
                    return;
                }
            }

            String status = JOptionPane.showInputDialog(this, "Enter Status (Pending/In Progress/Completed):");
            if (status == null || status.trim().isEmpty()) return;

            boolean isDeepClean = JOptionPane.showConfirmDialog(this, "Is Deep Clean?") == JOptionPane.YES_OPTION;
            boolean isMaintenance = JOptionPane.showConfirmDialog(this, "Requires Maintenance?") == JOptionPane.YES_OPTION;

            HousekeepingTask task = new HousekeepingTask(0, roomId, status, isDeepClean, isMaintenance);
            dao.addTask(task);
            loadTasks(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to add task: " + ex.getMessage());
        }
    }

    private void updateTask(ActionEvent e) {
        int row = taskTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to update.");
            return;
        }
        try {
            int taskId = (int) tableModel.getValueAt(row, 0);
            int roomId = (int) tableModel.getValueAt(row, 1);

            String status = JOptionPane.showInputDialog(this, "Enter new Status:", tableModel.getValueAt(row, 2));
            if (status == null || status.trim().isEmpty()) return;

            boolean isDeepClean = JOptionPane.showConfirmDialog(this, "Is Deep Clean?") == JOptionPane.YES_OPTION;
            boolean isMaintenance = JOptionPane.showConfirmDialog(this, "Requires Maintenance?") == JOptionPane.YES_OPTION;

            HousekeepingTask task = new HousekeepingTask(taskId, roomId, status, isDeepClean, isMaintenance);
            dao.updateTask(task);
            loadTasks(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to update task: " + ex.getMessage());
        }
    }

    private void deleteTask(ActionEvent e) {
        int row = taskTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.");
            return;
        }
        try {
            int taskId = (int) tableModel.getValueAt(row, 0);
            dao.deleteTask(taskId);
            loadTasks(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete task: " + ex.getMessage());
        }
    }
}
