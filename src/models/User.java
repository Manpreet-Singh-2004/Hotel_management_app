package models;

public class User {
    private int userId;
    private String username;
    private String password;
    private String role;  // "Admin" or "Receptionist"

    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Role helpers
    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(role);
    }

    public boolean isReceptionist() {
        return "Receptionist".equalsIgnoreCase(role);
    }
}
