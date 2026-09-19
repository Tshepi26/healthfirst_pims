package healthfirst_pims;
import javax.swing.*; 
import javax.swing.table.DefaultTableModel; 
import java.awt.*; 
import java.sql.*;

public class ItemWiseReport extends JPanel {
    
    // Table for item wise sales report - for analysis
    JTable table; 
    DefaultTableModel model;
    
    public ItemWiseReport(){ 
        setLayout(new BorderLayout()); 
        
        // Columns for item wise report - medicine name, qty sold, revenue
        model = new DefaultTableModel(new Object[]{"Medicine","Total Qty Sold","Total Revenue"},0); 
        table = new JTable(model); 
        add(new JScrollPane(table), BorderLayout.CENTER); 
        
        JButton b = new JButton("Load Item-Wise Sales"); 
        add(b, BorderLayout.SOUTH); 
        
        b.addActionListener(e -> load()); // Load data on button click
        
        load(); // Load on startup for report
    }
    
    // Load item wise sales - GROUP BY query for report requirement
    void load(){ 
        try{ 
            model.setRowCount(0); 
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(
                "SELECT m.name, SUM(si.quantity_sold), SUM(si.quantity_sold*si.price_at_sale) " +
                "FROM sale_items si JOIN medicines m ON si.medicine_id=m.medicine_id GROUP BY m.name"
            ); 
            while(rs.next()) 
                model.addRow(new Object[]{rs.getString(1), rs.getInt(2), rs.getDouble(3)}); 
        } catch(Exception e){} 
    }
}