package main;

import db.DBInitializer;

public class HotelManagementApp {
    public static void main(String[] args) {
        DBInitializer.initialize();
        System.out.println("🏨 Hotel Management System is ready to go!");
    }
}
