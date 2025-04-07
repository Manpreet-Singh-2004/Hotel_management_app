package models;

public class InventoryItem {
    private int itemId;
    private String itemName;
    private String category;
    private int quantity;
    private int threshold;

    public InventoryItem() {}

    public InventoryItem(String itemName, String category, int quantity, int threshold) {
        this.itemName = itemName;
        this.category = category;
        this.quantity = quantity;
        this.threshold = threshold;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
