package healthfirst_pims;
import javax.swing.*; 
import javax.swing.table.DefaultTableModel;
import java.awt.*; 
import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter;

public class Bill extends JDialog {
    
    public Bill(int saleId, DefaultTableModel cartModel, double grandTotal){
        // Bill dialog - modal so user must close it
        super((Frame)null, "HealthFirst Pharmacy - BILL No "+saleId, true);
        setSize(400,500); 
        setLocationRelativeTo(null); // Center on screen
        
        // Text area to display bill - monospaced for alignment
        JTextArea area = new JTextArea(); 
        area.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
        area.setEditable(false); // User cannot edit bill
        
        // Build bill content
        StringBuilder sb = new StringBuilder();
        sb.append("=== HealthFirst Pharmacy ===\nRoodepoort, Gauteng\n");
        sb.append("Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("Bill No: ").append(saleId).append("\n---------------------\n");
        sb.append(String.format("%-20s %3s %7s\n","Item","Qty","Total")); 
        sb.append("--------------\n");
        
        // Loop through cart items to add to bill - POS requirement
        for(int i = 0; i < cartModel.getRowCount(); i++) 
            sb.append(String.format("%-20s %3d R%6.2f\n", cartModel.getValueAt(i,1), cartModel.getValueAt(i,3), cartModel.getValueAt(i,4)));
        
        sb.append("--------------\nTOTAL: R ").append(String.format("%.2f", grandTotal)).append("\nThank you! Get well soon!\n");
        
        area.setText(sb.toString()); 
        add(new JScrollPane(area), BorderLayout.CENTER);
        
        // Close button - for screenshot requirement
        JButton close = new JButton("Close - Take Screenshot!"); 
        close.addActionListener(e -> dispose()); 
        add(close, BorderLayout.SOUTH);
        
        setVisible(true); // Show bill - take screenshot here for report
    }
}
