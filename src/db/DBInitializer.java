package db;

import java.sql.Connection;
import java.sql.Statement;

public class DBInitializer {
    public static void initialize() {
        String[] tables = {
                // Drop the outdated Promotions table (temporary fix during dev)
//                "DROP TABLE IF EXISTS Promotions;",


                "CREATE TABLE housekeeping (" +
                        "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "    room_id INTEGER NOT NULL," +
                        "    status TEXT NOT NULL," +
                        "    is_deep_clean BOOLEAN," +
                        "    is_maintenance BOOLEAN" +
                        ");",

                "CREATE TABLE IF NOT EXISTS Guests (" +
                        "guest_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "full_name TEXT," +
                        "email TEXT," +
                        "phone TEXT," +
                        "address TEXT);",

                "CREATE TABLE IF NOT EXISTS Rooms (" +
                        "room_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "room_number TEXT NOT NULL UNIQUE," +
                        "type TEXT," +
                        "price REAL," +
                        "status TEXT);",

                "CREATE TABLE IF NOT EXISTS Reservations (" +
                        "reservation_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "guest_id INTEGER," +
                        "room_id INTEGER," +
                        "check_in TEXT," +
                        "check_out TEXT," +
                        "payment_status TEXT," +
                        "special_requests TEXT," +
                        "FOREIGN KEY (guest_id) REFERENCES Guests(guest_id)," +
                        "FOREIGN KEY (room_id) REFERENCES Rooms(room_id));",

                "CREATE TABLE IF NOT EXISTS Inventory (" +
                        "item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "item_name TEXT," +
                        "category TEXT," +
                        "quantity INTEGER," +
                        "threshold INTEGER);",

                "CREATE TABLE IF NOT EXISTS Users (" +
                        "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT UNIQUE," +
                        "password TEXT," +
                        "role TEXT);",

                "CREATE TABLE IF NOT EXISTS Invoices (" +
                        "invoice_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "reservation_id INTEGER," +
                        "guest_id INTEGER," +
                        "issue_date TEXT," +
                        "total_amount REAL," +
                        "FOREIGN KEY (reservation_id) REFERENCES Reservations(reservation_id));",

                "CREATE TABLE IF NOT EXISTS GroupReservations (" +
                        "group_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "group_name TEXT NOT NULL," +
                        "contact_name TEXT," +
                        "contact_email TEXT," +
                        "contact_phone TEXT" +
                        ");",

                "CREATE TABLE IF NOT EXISTS GroupReservationRooms (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "group_id INTEGER," +
                        "room_id INTEGER," +
                        "check_in TEXT," +
                        "check_out TEXT," +
                        "FOREIGN KEY(group_id) REFERENCES GroupReservations(group_id)," +
                        "FOREIGN KEY(room_id) REFERENCES Rooms(room_id)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS Promotions (" +
                        "promo_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "code TEXT," +
                        "discount_type TEXT," +     // 'Percentage' or 'Flat'
                        "discount_value REAL," +
                        "reservation_id INTEGER," +
                        "start_date TEXT," +
                        "end_date TEXT," +
                        "is_global INTEGER DEFAULT 0," +
                        "FOREIGN KEY (reservation_id) REFERENCES Reservations(reservation_id));"
        };

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            for (String sql : tables) {
                stmt.execute(sql);
            }
            System.out.println("✅ Database tables created successfully!");
        } catch (Exception e) {
            System.out.println("❌ Error creating database tables");
            e.printStackTrace();
        }
    }
}
