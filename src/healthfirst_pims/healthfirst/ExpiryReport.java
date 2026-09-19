package healthfirst_pims;
import javax.swing.*; 
import javax.swing.table.DefaultTableModel; 
import java.awt.*; 
import java.sql.*;

public class ExpiryReport extends JPanel {
    
    // Table for expiry report - expiry tracking feature
    JTable table; 
    DefaultTableModel model;
    
    public ExpiryReport(){ 
        setLayout(new BorderLayout()); 
        
        // Columns for expiry report - shows days left
        model = new DefaultTableModel(new Object[]{"ID","Name","Expiry Date","Stock","Days Left"},0); 
        table = new JTable(model); 
        add(new JScrollPane(table), BorderLayout.CENTER); 
        
        JButton b = new JButton("Load Expiry Next 30 Days"); 
        add(b, BorderLayout.SOUTH); 
        
        b.addActionListener(e -> load()); // Load expiry list
        
        load(); // Load on startup for report screenshot
    }
    
    // Load medicines expiring in next 30 days - expiry management requirement
    void load(){ 
        try{ 
            model.setRowCount(0); 
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(
                "SELECT medicine_id,name,expiry_date,quantity_in_stock,DATEDIFF(expiry_date,CURDATE()) " +
                "FROM medicines WHERE expiry_date <= DATE_ADD(CURDATE(), INTERVAL 30 DAY) ORDER BY expiry_date"
            ); 
            while(rs.next()) 
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getInt(4), rs.getInt(5)+" days"}); 
        } catch(Exception e){} 
    }
}