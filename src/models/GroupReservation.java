package models;

public class GroupReservation {
    private int groupId;
    private String groupName;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private boolean sharedBilling;  // New field to indicate billing preference

    public GroupReservation() {}

    public GroupReservation(String groupName, String contactName, String contactEmail, 
                           String contactPhone, boolean sharedBilling) {
        this.groupName = groupName;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.sharedBilling = sharedBilling;
    }

    public GroupReservation(int groupId, String groupName, String contactName, 
                           String contactEmail, String contactPhone, boolean sharedBilling) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.sharedBilling = sharedBilling;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public boolean isSharedBilling() {
        return sharedBilling;
    }

    public void setSharedBilling(boolean sharedBilling) {
        this.sharedBilling = sharedBilling;
    }

    @Override
    public String toString() {
        return groupName + " (Contact: " + contactName + ")";
    }
}