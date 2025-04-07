package models;

public class Reservation {
    private int reservationId;
    private int guestId;
    private int roomId;
    private String checkIn;
    private String checkOut;
    private String paymentStatus;
    private String specialRequests;

    public Reservation() {}

    public Reservation(int reservationId, int guestId, int roomId, String checkIn, String checkOut, String paymentStatus, String specialRequests) {
        this.reservationId = reservationId;
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.paymentStatus = paymentStatus;
        this.specialRequests = specialRequests;
    }


    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public Reservation(int guestId, int roomId, String checkIn, String checkOut, String paymentStatus, String specialRequests) {
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.paymentStatus = paymentStatus;
        this.specialRequests = specialRequests;
    }


    @Override
    public String toString() {
        return "Reservation #" + reservationId +
                " | Guest ID: " + guestId +
                " | Room ID: " + roomId +
                " | From: " + checkIn +
                " To: " + checkOut +
                " | Status: " + paymentStatus;
    }
}
