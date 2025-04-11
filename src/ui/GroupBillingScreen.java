package ui;

import db.GroupReservationDAO;
import db.GroupReservationBillingDAO;
import models.GroupReservation;
import models.GroupReservationRoom;
import models.Room;
import models.Guest;
import models.User;
import db.RoomDAO;
import db.GuestDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class GroupBillingScreen extends JFrame {
    private final User currentUser;
    private final JFrame parentFrame;
    private final int groupId;
    
    private JTextField groupNameField;
    private JTextField totalAmountField;
    private JTextField issueDateField;
    private JRadioButton sharedBillingRadio;
    private JRadioButton individualBillingRadio;
    private DefaultTableModel roomsTableModel;
    
    private final GroupReservationDAO groupDAO;
    private final GroupReservationBillingDAO billingDAO;
    private final RoomDAO roomDAO;
    private final GuestDAO guestDAO;

    public GroupBillingScreen(User user, JFrame parent, int groupId) {
        this.currentUser = user;
        this.parentFrame = parent;
        this.groupId = groupId;
        this.groupDAO = new GroupReservationDAO();
        this.billingDAO = new GroupReservationBillingDAO();
        this.roomDAO = new RoomDAO();
        this.guestDAO = new GuestDAO();
        
        setTitle("💳 Group Billing");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top panel for billing info
        JPanel topPanel = createBillingInfoPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel for room list
        JPanel centerPanel = createRoomsPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel for buttons
        JPanel bottomPanel = createButtonPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Load data
        loadGroupData();
        loadRoomsData();
    }
    
    private JPanel createBillingInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Billing Information"));
        
        groupNameField = new JTextField();
        groupNameField.setEditable(false);
        
        totalAmountField = new JTextField();
        totalAmountField.setEditable(false);
        
        issueDateField = new JTextField(LocalDate.now().toString());
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sharedBillingRadio = new JRadioButton("Shared Billing (One Invoice)");
        individualBillingRadio = new JRadioButton("Individual Billing (Per Room)");
        
        ButtonGroup billingGroup = new ButtonGroup();
        billingGroup.add(sharedBillingRadio);
        billingGroup.add(individualBillingRadio);
        
        radioPanel.add(sharedBillingRadio);
        radioPanel.add(individualBillingRadio);
        
        panel.add(new JLabel("Group Name:"));
        panel.add(groupNameField);
        panel.add(new JLabel("Total Amount:"));
        panel.add(totalAmountField);
        panel.add(new JLabel("Issue Date:"));
        panel.add(issueDateField);
        panel.add(new JLabel("Billing Type:"));
        panel.add(radioPanel);
        
        return panel;
    }
    
    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Rooms in Group"));
        
        roomsTableModel = new DefaultTableModel(
            new String[]{"Room", "Guest", "Check-in", "Check-out", "Nights", "Room Rate", "Total"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable roomsTable = new JTable(roomsTableModel);
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton generateBtn = new JButton("Generate Invoice(s)");
        JButton cancelBtn = new JButton("Cancel");
        
        generateBtn.addActionListener(e -> generateInvoices());
        cancelBtn.addActionListener(e -> dispose());
        
        panel.add(generateBtn);
        panel.add(cancelBtn);
        
        return panel;
    }
    
    private void loadGroupData() {
        GroupReservation group = groupDAO.getGroupReservationById(groupId);
        if (group != null) {
            groupNameField.setText(group.getGroupName());
            
            // Set default billing option based on group preference
            if (group.isSharedBilling()) {
                sharedBillingRadio.setSelected(true);
            } else {
                individualBillingRadio.setSelected(true);
            }
            
            // Calculate total
            double total = groupDAO.calculateGroupTotal(groupId);
            totalAmountField.setText(String.format("$%.2f", total));
        }
    }
    
    private void loadRoomsData() {
        roomsTableModel.setRowCount(0);
        List<GroupReservationRoom> rooms = groupDAO.getRoomsForGroup(groupId);
        
        for (GroupReservationRoom roomEntry : rooms) {
            Room room = roomDAO.getRoomById(roomEntry.getRoomId());
            Guest guest = guestDAO.getGuestById(roomEntry.getGuestId());
            
            if (room != null) {
                // Calculate days
                LocalDate checkIn = LocalDate.parse(roomEntry.getCheckIn());
                LocalDate checkOut = LocalDate.parse(roomEntry.getCheckOut());
                long days = ChronoUnit.DAYS.between(checkIn, checkOut);
                
                // Calculate room total
                double roomTotal = room.getPrice() * days;
                
                roomsTableModel.addRow(new Object[]{
                    room.getRoomNumber() + " - " + room.getType(),
                    guest != null ? guest.getFullName() : "Unknown",
                    roomEntry.getCheckIn(),
                    roomEntry.getCheckOut(),
                    days,
                    String.format("$%.2f", room.getPrice()),
                    String.format("$%.2f", roomTotal)
                });
            }
        }
    }
    
    private void generateInvoices() {
        if (issueDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an issue date", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String issueDate = issueDateField.getText().trim();
        boolean isSharedBilling = sharedBillingRadio.isSelected();
        boolean success;
        
        if (isSharedBilling) {
            // Generate one invoice for the entire group
            String totalAmountStr = totalAmountField.getText().replace("$", "");
            double totalAmount = Double.parseDouble(totalAmountStr);
            success = billingDAO.generateGroupInvoice(groupId, issueDate, totalAmount);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Group invoice generated successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to generate group invoice", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Generate individual invoices for each room
            success = billingDAO.generateIndividualInvoices(groupId, issueDate);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Individual invoices generated successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to generate individual invoices", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        if (success) {
            dispose();
        }
    }
}