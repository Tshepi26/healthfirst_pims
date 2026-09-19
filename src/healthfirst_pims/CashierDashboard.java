package healthfirst_pims;
import javax.swing.*;

public class CashierDashboard extends JFrame {
    
    public CashierDashboard(int userId){
        // Setup Cashier Dashboard - limited access for cashier role
        setTitle("Cashier Dashboard"); 
        setSize(900,600); 
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Tabbed pane for cashier - only 2 tabs as per requirements
        JTabbedPane tabs = new JTabbedPane();
        
        // Cashier can only access POS and Sales Report - role based access
        tabs.addTab("POS / Billing", new POS(userId)); // Billing module with logged in user id
        tabs.addTab("Sales Report", new SalesReport()); // View sales report only
        
        add(tabs); // Add tabs to frame
        
        setVisible(true); // Show cashier dashboard
    }
}