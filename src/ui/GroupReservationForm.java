package ui;

import com.toedter.calendar.JDateChooser;
import db.GuestDAO;
import db.GroupReservationDAO;
import db.RoomDAO;
import models.Guest;
import models.GroupReservation;
import models.GroupReservationRoom;
import models.Room;
import models.User;
import main.MainMenu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class GroupReservationForm extends JFrame {
    private final User currentUser;
    private final MainMenu mainMenu;
    
    // Group details panel components
    private JTextField groupNameField;
    private JTextField contactNameField;
    private JTextField contactEmailField;
    private JTextField contactPhoneField;
    private JCheckBox sharedBillingCheckbox;
    
    // Room adding panel components
    private JComboBox<Room> roomCombo;
    private JComboBox<Guest> guestCombo;
    private JDateChooser checkInChooser;
    private JDateChooser checkOutChooser;
    
    // Tables
    private DefaultTableModel groupsTableModel;
    private JTable groupsTable;
    private DefaultTableModel roomsTableModel;
    private JTable roomsTable;
    
    // Current selected group
    private int selectedGroupId = -1;
    
    private final GroupReservationDAO groupDAO;
    private final RoomDAO roomDAO;
    private final GuestDAO guestDAO;

    public GroupReservationForm(User user, MainMenu menu) {
        this.currentUser = user;
        this.mainMenu = menu;
        this.groupDAO = new GroupReservationDAO();
        this.roomDAO = new RoomDAO();
        this.guestDAO = new GuestDAO();
        
        setTitle("👥 Group Reservations");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Create top panel with group management
        JPanel topPanel = createGroupManagementPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Create center panel with tables
        JPanel centerPanel = createTablesPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Create bottom panel with room management
        JPanel bottomPanel = createRoomManagementPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Load initial data
        loadGroups();
    }
    
    private JPanel createGroupManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Group Details"));
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 5));
        
        groupNameField = new JTextField();
        contactNameField = new JTextField();
        contactEmailField = new JTextField();
        contactPhoneField = new JTextField();
        sharedBillingCheckbox = new JCheckBox("Use shared billing (single invoice for group)");
        
        formPanel.add(new JLabel("Group Name:"));
        formPanel.add(groupNameField);
        formPanel.add(new JLabel("Contact Name:"));
        formPanel.add(contactNameField);
        formPanel.add(new JLabel("Contact Email:"));
        formPanel.add(contactEmailField);
        formPanel.add(new JLabel("Contact Phone:"));
        formPanel.add(contactPhoneField);
        formPanel.add(new JLabel("")); // Empty for spacing
        formPanel.add(sharedBillingCheckbox);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveGroupBtn = new JButton("💾 Save Group");
        JButton clearGroupBtn = new JButton("🔄 Clear");
        JButton deleteGroupBtn = new JButton("🗑️ Delete Group");
        JButton billingBtn = new JButton("💳 Generate Invoices");
        JButton backBtn = new JButton("⬅️ Back");
        
        buttonPanel.add(saveGroupBtn);
        buttonPanel.add(clearGroupBtn);
        buttonPanel.add(deleteGroupBtn);
        buttonPanel.add(billingBtn);
        buttonPanel.add(backBtn);
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        saveGroupBtn.addActionListener(e -> saveGroup());
        clearGroupBtn.addActionListener(e -> clearGroupForm());
        deleteGroupBtn.addActionListener(e -> deleteGroup());
        billingBtn.addActionListener(e -> openGroupBilling());
        backBtn.addActionListener(e -> {
            dispose();
            mainMenu.setVisible(true);
        });
        
        return panel;
    }
    
    private JPanel createTablesPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Groups table
        groupsTableModel = new DefaultTableModel(
            new String[]{"ID", "Group Name", "Contact", "Email", "Phone", "Billing Type"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        groupsTable = new JTable(groupsTableModel);
        JScrollPane groupsScrollPane = new JScrollPane(groupsTable);
        groupsScrollPane.setBorder(BorderFactory.createTitledBorder("Group Reservations"));
        
        // Rooms table
        roomsTableModel = new DefaultTableModel(
            new String[]{"ID", "Room", "Guest", "Check-in", "Check-out"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        roomsTable = new JTable(roomsTableModel);
        JScrollPane roomsScrollPane = new JScrollPane(roomsTable);
        roomsScrollPane.setBorder(BorderFactory.createTitledBorder("Rooms in Selected Group"));
        
        panel.add(groupsScrollPane);
        panel.add(roomsScrollPane);
        
        // Add selection listener to groups table
        groupsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = groupsTable.getSelectedRow();
                if (row >= 0) {
                    selectedGroupId = (int) groupsTableModel.getValueAt(row, 0);
                    loadGroupDetails(selectedGroupId);
                    loadRoomsForGroup(selectedGroupId);
                }
            }
        });
        
        return panel;
    }
    
    private JPanel createRoomManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Add Room to Group"));
        
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        
        roomCombo = new JComboBox<>();
        guestCombo = new JComboBox<>();
        checkInChooser = new JDateChooser();
        checkOutChooser = new JDateChooser();
        
        formPanel.add(new JLabel("Room:"));
        formPanel.add(roomCombo);
        formPanel.add(new JLabel("Guest:"));
        formPanel.add(guestCombo);
        formPanel.add(new JLabel("Check-in:"));
        formPanel.add(checkInChooser);
        formPanel.add(new JLabel("Check-out:"));
        formPanel.add(checkOutChooser);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addRoomBtn = new JButton("➕ Add Room");
        JButton removeRoomBtn = new JButton("➖ Remove Room");
        
        buttonPanel.add(addRoomBtn);
        buttonPanel.add(removeRoomBtn);
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        // Add action listeners
        addRoomBtn.addActionListener(e -> addRoomToGroup());
        removeRoomBtn.addActionListener(e -> removeRoomFromGroup());
        
        // Load rooms and guests
        loadRoomsAndGuests();
        
        return panel;
    }
    
    private void loadGroups() {
        groupsTableModel.setRowCount(0);
        List<GroupReservation> groups = groupDAO.getAllGroupReservations();
        
        for (GroupReservation group : groups) {
            groupsTableModel.addRow(new Object[]{
                group.getGroupId(),
                group.getGroupName(),
                group.getContactName(),
                group.getContactEmail(),
                group.getContactPhone(),
                group.isSharedBilling() ? "Shared" : "Individual"
            });
        }
    }
    
    private void loadGroupDetails(int groupId) {
        GroupReservation group = groupDAO.getGroupReservationById(groupId);
        if (group != null) {
            groupNameField.setText(group.getGroupName());
            contactNameField.setText(group.getContactName());
            contactEmailField.setText(group.getContactEmail());
            contactPhoneField.setText(group.getContactPhone());
            sharedBillingCheckbox.setSelected(group.isSharedBilling());
        }
    }
    
    private void loadRoomsForGroup(int groupId) {
        roomsTableModel.setRowCount(0);
        List<GroupReservationRoom> rooms = groupDAO.getRoomsForGroup(groupId);
        
        for (GroupReservationRoom roomEntry : rooms) {
            Room room = roomDAO.getRoomById(roomEntry.getRoomId());
            Guest guest = guestDAO.getGuestById(roomEntry.getGuestId());
            
            roomsTableModel.addRow(new Object[]{
                roomEntry.getId(),
                room != null ? room.getRoomNumber() + " - " + room.getType() : "Unknown",
                guest != null ? guest.getFullName() : "Unknown",
                roomEntry.getCheckIn(),
                roomEntry.getCheckOut()
            });
        }
    }
    
    private void loadRoomsAndGuests() {
        // Load rooms
        roomCombo.removeAllItems();
        for (Room room : roomDAO.getAllRooms()) {
            if ("Available".equals(room.getStatus())) {
                roomCombo.addItem(room);
            }
        }
        
        // Load guests
        guestCombo.removeAllItems();
        for (Guest guest : guestDAO.getAllGuests()) {
            guestCombo.addItem(guest);
        }
    }
    
    private void saveGroup() {
        String groupName = groupNameField.getText().trim();
        String contactName = contactNameField.getText().trim();
        String contactEmail = contactEmailField.getText().trim();
        String contactPhone = contactPhoneField.getText().trim();
        boolean sharedBilling = sharedBillingCheckbox.isSelected();
        
        if (groupName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Group name is required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        GroupReservation group = new GroupReservation(groupName, contactName, contactEmail, contactPhone, sharedBilling);
        
        boolean success;
        if (selectedGroupId != -1) {
            // Update existing group
            group.setGroupId(selectedGroupId);
            success = groupDAO.updateGroupReservation(group);
            if (success) {
                JOptionPane.showMessageDialog(this, "Group updated successfully");
            }
        } else {
            // Create new group
            int newId = groupDAO.addGroupReservation(group);
            success = newId != -1;
            if (success) {
                selectedGroupId = newId;
                JOptionPane.showMessageDialog(this, "Group created successfully");
            }
        }
        
        if (success) {
            loadGroups();
            // Select the saved group in the table
            for (int i = 0; i < groupsTableModel.getRowCount(); i++) {
                if ((int) groupsTableModel.getValueAt(i, 0) == selectedGroupId) {
                    groupsTable.setRowSelectionInterval(i, i);
                    break;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save group", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearGroupForm() {
        selectedGroupId = -1;
        groupNameField.setText("");
        contactNameField.setText("");
        contactEmailField.setText("");
        contactPhoneField.setText("");
        sharedBillingCheckbox.setSelected(false);
        roomsTableModel.setRowCount(0);
        groupsTable.clearSelection();
    }
    
    private void deleteGroup() {
        if (selectedGroupId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a group to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this group reservation? This will remove all associated rooms.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = groupDAO.deleteGroupReservation(selectedGroupId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Group reservation deleted successfully");
                clearGroupForm();
                loadGroups();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete group reservation", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void addRoomToGroup() {
        if (selectedGroupId == -1) {
            JOptionPane.showMessageDialog(this, "Please select or create a group first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Room selectedRoom = (Room) roomCombo.getSelectedItem();
        Guest selectedGuest = (Guest) guestCombo.getSelectedItem();
        
        if (selectedRoom == null || selectedGuest == null) {
            JOptionPane.showMessageDialog(this, "Please select a room and guest", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (checkInChooser.getDate() == null || checkOutChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please select check-in and check-out dates", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (checkOutChooser.getDate().before(checkInChooser.getDate())) {
            JOptionPane.showMessageDialog(this, "Check-out date must be after check-in date", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Format dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String checkIn = dateFormat.format(checkInChooser.getDate());
        String checkOut = dateFormat.format(checkOutChooser.getDate());
        
        // Create and save the group reservation room
        GroupReservationRoom roomEntry = new GroupReservationRoom(
            selectedGroupId,
            selectedRoom.getRoomId(),
            selectedGuest.getGuestId(),
            checkIn,
            checkOut
        );
        
        boolean success = groupDAO.addRoomToGroup(roomEntry);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Room added to group successfully");
            loadRoomsForGroup(selectedGroupId);
            // Update room status to Occupied
            selectedRoom.setStatus("Occupied");
            roomDAO.updateRoom(selectedRoom);
            loadRoomsAndGuests();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add room to group", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeRoomFromGroup() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to remove", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int roomEntryId = (int) roomsTableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to remove this room from the group?",
            "Confirm Removal",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = groupDAO.removeRoomFromGroup(roomEntryId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Room removed from group successfully");
                loadRoomsForGroup(selectedGroupId);
                loadRoomsAndGuests();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove room from group", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void openGroupBilling() {
        if (selectedGroupId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a group first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<GroupReservationRoom> rooms = groupDAO.getRoomsForGroup(selectedGroupId);
        if (rooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "This group has no rooms to bill", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Open the group billing screen
        GroupBillingScreen billingScreen = new GroupBillingScreen(currentUser, this, selectedGroupId);
        billingScreen.setVisible(true);
    }
    
    public static void main(String[] args) {
        User dummyUser = new User("admin", "admin", "Admin");
        MainMenu dummyMenu = new MainMenu(dummyUser);
        
        SwingUtilities.invokeLater(() -> {
            GroupReservationForm form = new GroupReservationForm(dummyUser, dummyMenu);
            form.setVisible(true);
        });
    }
}