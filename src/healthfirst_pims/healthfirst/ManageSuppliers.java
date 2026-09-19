package healthfirst_pims;
import javax.swing.*; 
import javax.swing.table.DefaultTableModel;
import java.awt.*; 
import java.sql.*;

public class ManageSuppliers extends JPanel {
    
    // Table and fields for supplier CRUD
    JTable table; 
    DefaultTableModel model; 
    JTextField nameF, contactF, phoneF, emailF, addrF;
    
    public ManageSuppliers(){ 
        setLayout(new BorderLayout()); 
        ui(); // build UI
        load(); // load suppliers from DB
    }
    
    void ui(){
        // Table model for suppliers
        model = new DefaultTableModel(new Object[]{"ID","Name","Contact Person","Phone","Email","Address"},0);
        table = new JTable(model); 
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Form panel for supplier details
        JPanel f = new JPanel(new GridLayout(3,4,5,5));
        nameF = new JTextField(); 
        contactF = new JTextField(); 
        phoneF = new JTextField(); 
        emailF = new JTextField(); 
        addrF = new JTextField();
        
        f.add(new JLabel("Name:")); f.add(nameF); 
        f.add(new JLabel("Contact Person:")); f.add(contactF);
        f.add(new JLabel("Phone:")); f.add(phoneF); 
        f.add(new JLabel("Email:")); f.add(emailF);
        f.add(new JLabel("Address:")); f.add(addrF);
        
        // Buttons for CRUD operations
        JPanel b = new JPanel(); 
        JButton add = new JButton("Add"), upd = new JButton("Update"), del = new JButton("Delete"), ref = new JButton("Refresh");
        b.add(add); b.add(upd); b.add(del); b.add(ref);
        
        JPanel s = new JPanel(new BorderLayout()); 
        s.add(f, BorderLayout.CENTER); 
        s.add(b, BorderLayout.SOUTH); 
        add(s, BorderLayout.SOUTH);
        
        // Button listeners
        add.addActionListener(e -> addS()); 
        upd.addActionListener(e -> updateS()); 
        del.addActionListener(e -> deleteS()); 
        ref.addActionListener(e -> load());
        
        // Fill form when row selected
        table.getSelectionModel().addListSelectionListener(e->{ 
            int r = table.getSelectedRow(); 
            if(r != -1){ 
                nameF.setText(model.getValueAt(r,1).toString()); 
                contactF.setText(model.getValueAt(r,2).toString()); 
                phoneF.setText(model.getValueAt(r,3).toString()); 
                emailF.setText(model.getValueAt(r,4).toString()); 
                addrF.setText(model.getValueAt(r,5).toString()); 
            }
        });
    }
    
    // Load all suppliers from database - READ
    void load(){ 
        try{ 
            model.setRowCount(0); 
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery("SELECT * FROM suppliers"); 
            while(rs.next()) 
                model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6)}); 
        } catch(Exception ex){} 
    }
    
    // Add new supplier - CREATE
    void addS(){ 
        try{ 
            PreparedStatement ps = DBConnection.getConnection().prepareStatement("INSERT INTO suppliers(name,contact_person,phone,email,address) VALUES(?,?,?,?,?)"); 
            ps.setString(1, nameF.getText()); 
            ps.setString(2, contactF.getText()); 
            ps.setString(3, phoneF.getText()); 
            ps.setString(4, emailF.getText()); 
            ps.setString(5, addrF.getText()); 
            ps.executeUpdate(); 
            load(); 
        } catch(Exception e){ 
            JOptionPane.showMessageDialog(this, e.getMessage()); 
        } 
    }
    
    // Update supplier - UPDATE
    void updateS(){ 
        int r = table.getSelectedRow(); 
        if(r == -1) return;
        try{ 
            PreparedStatement ps = DBConnection.getConnection().prepareStatement("UPDATE suppliers SET name=?,contact_person=?,phone=?,email=?,address=? WHERE supplier_id=?"); 
            ps.setString(1, nameF.getText()); 
            ps.setString(2, contactF.getText()); 
            ps.setString(3, phoneF.getText()); 
            ps.setString(4, emailF.getText()); 
            ps.setString(5, addrF.getText()); 
            ps.setInt(6, (int)model.getValueAt(r,0)); 
            ps.executeUpdate(); 
            load(); 
        } catch(Exception e){ 
            JOptionPane.showMessageDialog(this, e.getMessage()); 
        } 
    }
    
    // Delete supplier - DELETE, handle foreign key error
    void deleteS(){ 
        int r = table.getSelectedRow(); 
        if(r == -1) return; 
        try{ 
            DBConnection.getConnection().createStatement().executeUpdate("DELETE FROM suppliers WHERE supplier_id="+(int)model.getValueAt(r,0)); 
            load(); 
        } catch(Exception e){ 
            JOptionPane.showMessageDialog(this, "Cannot delete: Supplier linked to medicine\n"+e.getMessage()); 
        } 
    }
}