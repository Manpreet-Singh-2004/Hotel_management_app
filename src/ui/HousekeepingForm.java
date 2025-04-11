// src/ui/HousekeepingForm.java
package ui;

import db.HousekeepingDAO;
import models.HousekeepingTask;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.util.List;

public class HousekeepingForm extends JFrame {
    private HousekeepingDAO dao;
    private JTable taskTable;
    private DefaultTableModel tableModel;

    public HousekeepingForm(Connection conn) {
        this.dao = new HousekeepingDAO(conn);
        setTitle("Housekeeping Management");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Task ID", "Room ID", "Status", "Deep Clean", "Maintenance"}, 0);
        taskTable = new JTable(tableModel);
        add(new JScrollPane(taskTable), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(this::loadTasks);
        add(refreshBtn, BorderLayout.SOUTH);

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
}
