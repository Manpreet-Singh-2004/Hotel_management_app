package models;

public class GroupReservationRoom {
    private int id;
    private int groupId;
    private int roomId;
    private int guestId;  // Guest assigned to this room
    private String checkIn;
    private String checkOut;

    public GroupReservationRoom() {}

    public GroupReservationRoom(int groupId, int roomId, int guestId, String checkIn, String checkOut) {
        this.groupId = groupId;
        this.roomId = roomId;
        this.guestId = guestId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public GroupReservationRoom(int id, int groupId, int roomId, int guestId, String checkIn, String checkOut) {
        this.id = id;
        this.groupId = groupId;
        this.roomId = roomId;
        this.guestId = guestId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
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
}