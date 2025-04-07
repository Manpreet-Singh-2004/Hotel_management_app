package main;

import db.DBInitializer;
import db.ReservationDAO;
import models.Reservation;

public class ReservationTest {
    public static void main(String[] args) {
        DBInitializer.initialize();

        ReservationDAO dao = new ReservationDAO();

        Reservation r = new Reservation(1, 1, "2025-04-10", "2025-04-13", "Paid", "Window seat");
        dao.addReservation(r);

        System.out.println("📋 Reservations:");
        for (Reservation res : dao.getAllReservations()) {
            System.out.println(res);
        }
    }
}
