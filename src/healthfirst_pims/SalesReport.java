package healthfirst_pims;
import javax.swing.*; 
import javax.swing.table.DefaultTableModel; 
import java.awt.*; 
import java.sql.*;

public class SalesReport extends JPanel {
    
    // Table for sales report - for report analysis
    JTable table; 
    DefaultTableModel model;
    
    public SalesReport(){ 
        setLayout(new BorderLayout()); 
        
        // Sales report columns
        model = new DefaultTableModel(new Object[]{"Sale ID","Date","Total","Cashier"},0); 
        table = new JTable(model); 
        add(new JScrollPane(table), BorderLayout.CENTER); 
        
        JButton b = new JButton("Load Sales Report"); 
        add(b, BorderLayout.SOUTH); 
        
        b.addActionListener(e -> load()); // Refresh report
        
        load(); // Load on startup
    }
    
    // Load sales with cashier name using JOIN - for report screenshots
    void load(){ 
        try{ 
            model.setRowCount(0); 
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(
                "SELECT s.sale_id,s.sale_date,s.total_amount,u.username FROM sales s JOIN users u ON s.user_id=u.user_id ORDER BY s.sale_id DESC"
            ); 
            while(rs.next()) 
                model.addRow(new Object[]{rs.getInt(1), rs.getTimestamp(2), rs.getDouble(3), rs.getString(4)}); 
        } catch(Exception e){} 
    }
}