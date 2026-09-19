package healthfirst_pims;
import javax.swing.*; 
import javax.swing.table.DefaultTableModel;
import java.awt.*; 
import java.sql.*;

public class ManageMedicines extends JPanel {
    
    // Table and fields for medicine management - CRUD
    JTable table; 
    DefaultTableModel model;
    JTextField nameF, compF, typeF, priceF, qtyF, reorderF, expiryF, suppF;
    
    public ManageMedicines(){ 
        setLayout(new BorderLayout());
        ui(); // setup UI
        load(); // load data from DB
    }
    
    void ui(){
        // Table model for medicines table
        model = new DefaultTableModel(new Object[]{"ID","Name","Company","Type","Price","Stock","Reorder","Expiry","SuppID"},0);
        table = new JTable(model); 
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Form panel for inputs
        JPanel f = new JPanel(new GridLayout(4,4,5,5));
        nameF = new JTextField(); 
        compF = new JTextField(); 
        typeF = new JTextField(); 
        priceF = new JTextField();
        qtyF = new JTextField(); 
        reorderF = new JTextField(); 
        expiryF = new JTextField("2027-05-30"); // default expiry date
        suppF = new JTextField("1"); // default supplier id
        
        // Adding labels and fields
        f.add(new JLabel("Name:")); f.add(nameF); 
        f.add(new JLabel("Company:")); f.add(compF);
        f.add(new JLabel("Type:")); f.add(typeF); 
        f.add(new JLabel("Price:")); f.add(priceF);
        f.add(new JLabel("Qty:")); f.add(qtyF); 
        f.add(new JLabel("Reorder:")); f.add(reorderF);
        f.add(new JLabel("Expiry YYYY-MM-DD:")); f.add(expiryF); 
        f.add(new JLabel("Supplier ID:")); f.add(suppF);
        
        // Buttons panel - Add, Update, Delete, Refresh
        JPanel b = new JPanel(); 
        JButton add = new JButton("Add"), upd = new JButton("Update"), del = new JButton("Delete"), ref = new JButton("Refresh");
        b.add(add); b.add(upd); b.add(del); b.add(ref);
        
        // South panel combining form and buttons
        JPanel s = new JPanel(new BorderLayout()); 
        s.add(f, BorderLayout.CENTER); 
        s.add(b, BorderLayout.SOUTH); 
        add(s, BorderLayout.SOUTH);
        
        // Button actions
        add.addActionListener(e -> addM()); 
        upd.addActionListener(e -> updateM()); 
        del.addActionListener(e -> deleteM()); 
        ref.addActionListener(e -> load());
        
        // When row selected, fill fields with row data
        table.getSelectionModel().addListSelectionListener(e->{ 
            int r = table.getSelectedRow(); 
            if(r != -1){ 
                nameF.setText(model.getValueAt(r,1).toString()); 
                compF.setText(model.getValueAt(r,2).toString()); 
                typeF.setText(model.getValueAt(r,3).toString()); 
                priceF.setText(model.getValueAt(r,4).toString()); 
                qtyF.setText(model.getValueAt(r,5).toString()); 
                reorderF.setText(model.getValueAt(r,6).toString()); 
                expiryF.setText(model.getValueAt(r,7).toString()); 
                suppF.setText(model.getValueAt(r,8).toString()); 
            }
        });
    }
    
    // Load all medicines from database
    void load(){ 
        try{ 
            model.setRowCount(0); 
            Connection con = DBConnection.getConnection(); 
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM medicines"); 
            while(rs.next()) 
                model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getDouble(5),rs.getInt(6),rs.getInt(7),rs.getDate(8),rs.getInt(9)}); 
        } catch(Exception ex){} 
    }
    
    // Insert new medicine - CREATE
    void addM(){ 
        try{ 
            Connection con = DBConnection.getConnection(); 
            PreparedStatement ps = con.prepareStatement("INSERT INTO medicines(name,company,medicine_type,price,quantity_in_stock,reorder_level,expiry_date,supplier_id) VALUES(?,?,?,?,?,?,?,?)"); 
            ps.setString(1, nameF.getText()); 
            ps.setString(2, compF.getText()); 
            ps.setString(3, typeF.getText()); 
            ps.setDouble(4, Double.parseDouble(priceF.getText())); 
            ps.setInt(5, Integer.parseInt(qtyF.getText())); 
            ps.setInt(6, Integer.parseInt(reorderF.getText())); 
            ps.setDate(7, Date.valueOf(expiryF.getText())); 
            ps.setInt(8, Integer.parseInt(suppF.getText())); 
            ps.executeUpdate(); 
            load(); // refresh table
        } catch(Exception e){ 
            JOptionPane.showMessageDialog(this, e.getMessage()); 
        } 
    }
    
    // Update selected medicine - UPDATE
    void updateM(){ 
        int r = table.getSelectedRow(); 
        if(r == -1) return; 
        try{ 
            Connection con = DBConnection.getConnection(); 
            PreparedStatement ps = con.prepareStatement("UPDATE medicines SET name=?,company=?,medicine_type=?,price=?,quantity_in_stock=?,reorder_level=?,expiry_date=?,supplier_id=? WHERE medicine_id=?"); 
            ps.setString(1, nameF.getText()); 
            ps.setString(2, compF.getText()); 
            ps.setString(3, typeF.getText()); 
            ps.setDouble(4, Double.parseDouble(priceF.getText())); 
            ps.setInt(5, Integer.parseInt(qtyF.getText())); 
            ps.setInt(6, Integer.parseInt(reorderF.getText())); 
            ps.setDate(7, Date.valueOf(expiryF.getText())); 
            ps.setInt(8, Integer.parseInt(suppF.getText())); 
            ps.setInt(9, (int)model.getValueAt(r,0)); 
            ps.executeUpdate(); 
            load(); 
        } catch(Exception e){ 
            JOptionPane.showMessageDialog(this, e.getMessage()); 
        } 
    }
    
    // Delete medicine - DELETE, also delete sale_items first to avoid FK error
    void deleteM(){ 
        int r = table.getSelectedRow(); 
        if(r == -1) return; 
        try{ 
            Connection con = DBConnection.getConnection(); 
            // Delete from sale_items first because of foreign key
            con.createStatement().executeUpdate("DELETE FROM sale_items WHERE medicine_id="+(int)model.getValueAt(r,0)); 
            con.createStatement().executeUpdate("DELETE FROM medicines WHERE medicine_id="+(int)model.getValueAt(r,0)); 
            load(); 
        } catch(Exception e){ 
            JOptionPane.showMessageDialog(this, e.getMessage()); 
        } 
    }
}