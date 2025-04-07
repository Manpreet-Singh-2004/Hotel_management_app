package main;

import db.DBInitializer;
import db.RoomDAO;
import models.Room;

public class RoomTest {
    public static void main(String[] args) {
        DBInitializer.initialize();

        RoomDAO dao = new RoomDAO();
        dao.addRoom(new Room("101", "Single", 99.99, "Available"));
        dao.addRoom(new Room("102", "Double", 149.99, "Available"));

        System.out.println("📋 All Rooms:");
        for (Room room : dao.getAllRooms()) {
            System.out.println(room);
        }
    }
}
