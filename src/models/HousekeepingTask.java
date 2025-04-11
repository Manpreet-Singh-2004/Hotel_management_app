package models;

public class HousekeepingTask {
    private int taskId;
    private int roomId;
    private String status; // e.g., "Pending", "In Progress", "Completed"
    private boolean isDeepClean;
    private boolean isMaintenance;

    public HousekeepingTask(int taskId, int roomId, String status, boolean isDeepClean, boolean isMaintenance) {
        this.taskId = taskId;
        this.roomId = roomId;
        this.status = status;
        this.isDeepClean = isDeepClean;
        this.isMaintenance = isMaintenance;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDeepClean() {
        return isDeepClean;
    }

    public void setDeepClean(boolean deepClean) {
        isDeepClean = deepClean;
    }

    public boolean isMaintenance() {
        return isMaintenance;
    }

    public void setMaintenance(boolean maintenance) {
        isMaintenance = maintenance;
    }
}
