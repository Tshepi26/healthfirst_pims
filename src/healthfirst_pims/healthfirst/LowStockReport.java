package healthfirst_pims;
import javax.swing.*; 
import javax.swing.table.DefaultTableModel; 
import java.awt.*; 
import java.sql.*;

public class LowStockReport extends JPanel {
    
    // Table for low stock report - stock alert feature
    JTable table; 
    DefaultTableModel model;
    
    public LowStockReport(){ 
        setLayout(new BorderLayout()); 
        
        // Low stock columns - shows status LOW STOCK!
        model = new DefaultTableModel(new Object[]{"ID","Name","Stock","Reorder Level","Status"},0); 
        table = new JTable(model); 
        add(new JScrollPane(table), BorderLayout.CENTER); 
        
        JButton b = new JButton("Load Low Stock (< Reorder Level)"); 
        add(b, BorderLayout.SOUTH); 
        
        b.addActionListener(e -> load()); // Refresh low stock list
        
        load(); // Load on startup
    }
    
    // Load medicines where stock <= reorder_level - stock management requirement
    void load(){ 
        try{ 
            model.setRowCount(0); 
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(
                "SELECT medicine_id,name,quantity_in_stock,reorder_level FROM medicines WHERE quantity_in_stock <= reorder_level"
            ); 
            while(rs.next()) 
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4), "LOW STOCK!"}); 
        } catch(Exception e){} 
    }
}