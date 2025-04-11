package models;

public class SeasonalPricing {
    private int id;
    private String startDate;
    private String endDate;
    private double priceMultiplier;
    private String description;

    public SeasonalPricing(int id, String startDate, String endDate, double priceMultiplier, String description) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priceMultiplier = priceMultiplier;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getPriceMultiplier() {
        return priceMultiplier;
    }

    public void setPriceMultiplier(double priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
