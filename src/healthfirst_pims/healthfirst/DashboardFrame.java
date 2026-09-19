package healthfirst_pims;
import javax.swing.*;

public class DashboardFrame extends JFrame {
    
    public DashboardFrame(){
        // Setup Admin Dashboard window - main window after login
        setTitle("Admin Dashboard - HealthFirst"); 
        setSize(1000,600); 
        setLocationRelativeTo(null); // Center screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // JTabbedPane used to show all modules in one window - PROG732 requirement
        JTabbedPane tabs = new JTabbedPane();
        
        // Adding all 8 tabs for Admin role
        tabs.addTab("Manage Medicines", new ManageMedicines()); // CRUD for medicines table
        tabs.addTab("Manage Suppliers", new ManageSuppliers()); // CRUD for suppliers table
        tabs.addTab("Manage Users", new ManageUsers());          // CRUD for users table
        tabs.addTab("POS / Billing", new POS(1));                // POS module - user_id 1 is Admin
        tabs.addTab("Sales Report", new SalesReport());           // Shows all sales
        tabs.addTab("ItemWise Report", new ItemWiseReport());     // Group by medicine sales
        tabs.addTab("Low Stock Report", new LowStockReport());    // Stock <= reorder_level
        tabs.addTab("Expiry Report", new ExpiryReport());         // Medicines expiring in 30 days
        
        add(tabs); // Add tabbed pane to frame
        
        setVisible(true); // Show dashboard
    }
}