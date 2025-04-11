package main;

import db.DBInitializer;
import ui.LoginScreen;

import javax.swing.*;

public class HotelManagementApp {
    public static void main(String[] args) {
        DBInitializer.initialize();

        // Launch login screen on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}