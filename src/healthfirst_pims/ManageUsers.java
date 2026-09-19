package healthfirst_pims;
import javax.swing.*; 
import javax.swing.table.DefaultTableModel;
import java.awt.*; 
import java.sql.*;

public class ManageUsers extends JPanel {
    
    // Table and fields for user management - Admin only
    JTable table; 
    DefaultTableModel model; 
    JTextField userF, fullF; 
    JPasswordField passF; 
    JComboBox<String> roleBox;
    
    public ManageUsers(){ 
        setLayout(new BorderLayout());
        ui(); // setup UI
        load(); // load users
    }
    
    void ui(){
        // Table shows user_id, username, role, full_name - no password shown for security
        model = new DefaultTableModel(new Object[]{"ID","Username","Role","Full Name"},0);
        table = new JTable(model); 
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Form for new user
        JPanel f = new JPanel(new GridLayout(2,4,5,5));
        userF = new JTextField(); 
        passF = new JPasswordField(); 
        fullF = new JTextField(); 
        roleBox = new JComboBox<>(new String[]{"Admin","Cashier"}); // Role based access control
        
        f.add(new JLabel("Username:")); f.add(userF); 
        f.add(new JLabel("Password:")); f.add(passF);
        f.add(new JLabel("Full Name:")); f.add(fullF); 
        f.add(new JLabel("Role:")); f.add(roleBox);
        
        // Buttons - Add, Delete, Refresh
        JPanel b = new JPanel(); 
        JButton add = new JButton("Add User"), del = new JButton("Delete"), ref = new JButton("Refresh");
        b.add(add); b.add(del); b.add(ref);
        
        JPanel s = new JPanel(new BorderLayout()); 
        s.add(f, BorderLayout.CENTER); 
        s.add(b, BorderLayout.SOUTH); 
        add(s, BorderLayout.SOUTH);
        
        // Button actions
        add.addActionListener(e -> addU()); 
        del.addActionListener(e -> deleteU()); 
        ref.addActionListener(e -> load());
    }
    
    // Load all users - READ
    void load(){ 
        try { 
            model.setRowCount(0); 
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery("SELECT user_id,username,role,full_name FROM users"); 
            while(rs.next()) 
                model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4)}); 
        } catch(Exception ex){} 
    }
    
    // Add new user - CREATE, for RBAC requirement
    void addU(){
        try { 
            PreparedStatement ps = DBConnection.getConnection().prepareStatement("INSERT INTO users(username,password,role,full_name) VALUES(?,?,?,?)"); 
            ps.setString(1, userF.getText()); 
            ps.setString(2, new String(passF.getPassword())); // Get password from JPasswordField
            ps.setString(3, roleBox.getSelectedItem().toString()); // Admin or Cashier role
            ps.setString(4, fullF.getText()); 
            ps.executeUpdate(); 
            load(); // refresh table
        } catch(Exception e){ 
            JOptionPane.showMessageDialog(this, e.getMessage()); 
        } 
    }
    
    // Delete user - DELETE
    void deleteU(){ 
        int r = table.getSelectedRow();
        if(r == -1) return; 
        try{ 
            DBConnection.getConnection().createStatement().executeUpdate("DELETE FROM users WHERE user_id="+(int)model.getValueAt(r,0)); 
            load(); 
        } catch(Exception e){ 
            JOptionPane.showMessageDialog(this, e.getMessage()); 
        } 
    }
}