package models;

public class Promotion {
    private int id;
    private String code;
    private String discountType; // "Percentage" or "Flat"
    private double discountValue;
    private int reservationId; // Nullable for global
    private String startDate;  // Optional (format: "yyyy-MM-dd")
    private String endDate;    // Optional (format: "yyyy-MM-dd")
    private boolean isGlobal;

    public Promotion() {}

    public Promotion(String code, String discountType, double discountValue, int reservationId) {
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.reservationId = reservationId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDiscountType() {
        return discountType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    @Override
    public String toString() {
        return code + " (" + discountType + " - " + discountValue + ")" + (isGlobal ? " 🌐" : "");
    }
}
