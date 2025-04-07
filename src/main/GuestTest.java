package main;

import db.DBInitializer;
import db.GuestDAO;
import models.Guest;

public class GuestTest {
    public static void main(String[] args) {
        DBInitializer.initialize();

        GuestDAO dao = new GuestDAO();

        Guest g = new Guest("Sahibjeet Singh", "sahib@example.com", "9876543210", "Chandigarh");
        dao.addGuest(g);

        System.out.println("📋 Guest List:");
        for (Guest guest : dao.getAllGuests()) {
            System.out.println(guest);
        }
    }
}
