// src/main/MainMenu.java
package main;

import models.User;
import ui.*;
import db.DBConnection;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    private final User currentUser;

    public MainMenu(User currentUser) {
        this.currentUser = currentUser;

        setTitle("🏨 Hotel Management Dashboard - Logged in as " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(11, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton guestBtn = new JButton("👤 Manage Guests");
        JButton roomBtn = new JButton("🏨 Manage Rooms");
        JButton reserveBtn = new JButton("📅 Make Reservation");
        JButton groupBtn = new JButton("👪 Group Reservations");
        JButton inventoryBtn = new JButton("📦 Inventory");
        JButton billingBtn = new JButton("💳 Billing");
        JButton reportBtn = new JButton("📊 Reports");
        JButton searchBtn = new JButton("🔍 Search / Filter");
        JButton promoBtn = new JButton("🎁 Promotions");
        JButton logoutBtn = new JButton("🔐 Logout");
        JButton exitBtn = new JButton("🚪 Exit");
        JButton housekeepingBtn = new JButton("🧹 Manage Housekeeping");
        JButton seasonalBtn = new JButton("📅 Seasonal Pricing");

        // Access control
        if (currentUser.isAdmin()) {
            panel.add(guestBtn);
            panel.add(roomBtn);
            panel.add(reserveBtn);
            panel.add(groupBtn);
            panel.add(inventoryBtn);
            panel.add(billingBtn);
            panel.add(reportBtn);
            panel.add(searchBtn);
            panel.add(promoBtn);
            panel.add(housekeepingBtn);
            panel.add(seasonalBtn);
        } else if (currentUser.isReceptionist()) {
            panel.add(guestBtn);
            panel.add(reserveBtn);
            panel.add(groupBtn);
            panel.add(billingBtn);
            panel.add(searchBtn);
        }

        panel.add(logoutBtn);
        panel.add(exitBtn);
        add(panel);

        // Actions – hide MainMenu and open forms

        housekeepingBtn.addActionListener(e -> {
            setVisible(false);
            new HousekeepingForm(DBConnection.getInstance().getConnection(), this).setVisible(true);
        });

        seasonalBtn.addActionListener(e -> {
            setVisible(false);
            new SeasonalPricingForm(DBConnection.getInstance().getConnection(), this).setVisible(true);
        });

        guestBtn.addActionListener(e -> {
            setVisible(false);
            new GuestForm(currentUser, this).setVisible(true);
        });

        roomBtn.addActionListener(e -> {
            setVisible(false);
            new RoomForm(currentUser, this).setVisible(true);
        });

        reserveBtn.addActionListener(e -> {
            setVisible(false);
            new ReservationForm(currentUser, this).setVisible(true);
        });
        
        groupBtn.addActionListener(e -> {
            setVisible(false);
            new GroupReservationForm(currentUser, this).setVisible(true);
        });

        inventoryBtn.addActionListener(e -> {
            setVisible(false);
            new InventoryForm(currentUser, this).setVisible(true);
        });

        billingBtn.addActionListener(e -> {
            setVisible(false);
            new BillingScreen(currentUser, this).setVisible(true);
        });

        reportBtn.addActionListener(e -> {
            setVisible(false);
            new ReportScreen(currentUser, this).setVisible(true);
        });

        searchBtn.addActionListener(e -> {
            setVisible(false);
            new SearchFilterScreen(currentUser, this).setVisible(true);
        });

        promoBtn.addActionListener(e -> {
            setVisible(false);
            new PromotionForm(currentUser, this).setVisible(true);
        });

<<<<<<< Updated upstream
        historyBtn.addActionListener(e -> {
            dispose();
            new GuestHistoryScreen(currentUser, this).setVisible(true);
        });

=======
>>>>>>> Stashed changes
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        exitBtn.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}