# 🏨 Hotel Management System - Java Swing + SQLite

This is a complete Hotel Management System developed using Java Swing for the GUI and SQLite as the backend database. It includes role-based UI access for **Admin** and **Receptionist**, along with various modules for managing hotel operations.

---

## 📁 Project Structure

```
Hotel_management_app/
├── lib/                     # External libraries (JCalendar, SQLite JDBC)
├── out/                     # Build outputs
├── src/
│   ├── dao/                 # Data Access Objects
│   ├── db/                  # DB Initialization
│   ├── model/               # Model classes (Reservation, Guest, Room, etc.)
│   └── ui/                  # UI screens (Login, MainMenu, ReservationForm, etc.)
├── database.db              # SQLite database
├── README.md
```

---

## 🚀 Features

- Login with role-based access (Admin / Receptionist)
- Reservation creation and automatic invoice generation
- Real-time room availability and status (vacant, occupied, under maintenance)
- Housekeeping management
- Invoice generation with auto-calculated pricing and seasonal discount logic
- Reports view (total guests, income, rooms)
- Guest history and stay tracking
- Clean and modular UI using Java Swing

---

## 🧠 System UML Diagrams

These UML diagrams illustrate system architecture and workflows.

### ✅ Use Case Diagram
![Use Case Diagram](/media/UseCase.png)

### ✅ Class Diagram
![Class Diagram](/media/ClassDiagram.png)

### ✅ Sequence Diagram
![Sequence Diagram](/media/SeQuenceDiagram.png)

---

## 🛠 Technologies Used

- **Java** (Swing)
- **SQLite**
- **JCalendar** for date picking
- **JDBC** for DB connectivity
- **IntelliJ IDEA** for development

---

## 📌 Getting Started

1. Clone the repo:
```bash
git clone https://github.com/Manpreet-Singh-2004/Hotel_management_app
```

2. Open in IntelliJ and add the libraries from the `lib/` folder manually.

3. Run `MainMenu.java` after database is initialized.

4. Default credentials can be manually inserted or preconfigured.

---

## 🧩 Key Code Snippets

### 🔐 Login Logic
```java
if (user.getRole().equals("Admin")) {
    new MainMenu(user).setVisible(true);
} else if (user.getRole().equals("Receptionist")) {
    new MainMenu(user).setVisible(true);
} else {
    JOptionPane.showMessageDialog(this, "Invalid credentials");
}
```
> Determines the role after login and navigates to the appropriate UI.

---

### 🛏️ Creating a Reservation
```java
Reservation reservation = new Reservation(guestId, roomId, checkIn, checkOut, "Unpaid", specialRequests);
int reservationId = ReservationDAO.addReservation(reservation);
```
> Creates a new reservation with default payment status and adds it to the database.

---

### 🧾 Invoice Generation Logic
```java
double total = calculateTotal(roomRate, numberOfDays, discount);
Invoice invoice = new Invoice(reservationId, roomRate, discount, total);
InvoiceDAO.addInvoice(invoice);
```
> Calculates total cost considering discounts and generates the invoice for the stay.

---

### 🧹 Scheduling Housekeeping
```java
HousekeepingTask task = new HousekeepingTask(roomId, "Scheduled after check-out", "Pending");
HousekeepingDAO.addTask(task);
```
> Automatically schedules a cleaning task after guest check-out or late check-out extension.

---

## 📬 Contributors

- Manpreet Singh  
- Sahibjeet Singh  
- Shreyas Dutt

---
