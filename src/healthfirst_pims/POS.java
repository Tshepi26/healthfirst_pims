package healthfirst_pims;
import javax.swing.*; 
import javax.swing.table.DefaultTableModel;
import java.awt.*; 
import java.sql.*;

public class POS extends JPanel {
    
    // Tables for medicines and cart - POS requirement
    JTable medTable, cartTable; 
    DefaultTableModel medModel, cartModel;
    JTextField searchF, qtyF; 
    JLabel totalL; 
    double grand = 0; 
    int userId; // logged in user id for sales record
    
    public POS(int userId){ 
        this.userId = userId; 
        setLayout(new BorderLayout(10,10));
        ui(); // build POS UI
        loadMeds(""); // load all medicines
    }
    
    void ui(){
        // Top panel - search medicine (Stock Check feature)
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
        searchF = new JTextField(20); 
        JButton sBtn = new JButton("Search / Stock Check");
        top.add(new JLabel("Search Medicine:")); 
        top.add(searchF); 
        top.add(sBtn); 
        add(top, BorderLayout.NORTH);
        
        // Medicine list table and cart table
        medModel = new DefaultTableModel(new Object[]{"ID","Name","Price","Stock"},0); 
        medTable = new JTable(medModel);
        cartModel = new DefaultTableModel(new Object[]{"ID","Name","Price","Qty","Total"},0); 
        cartTable = new JTable(cartModel);
        
        JPanel center = new JPanel(new GridLayout(1,2,10,10)); 
        center.add(new JScrollPane(medTable));
        
        // Cart panel
        JPanel cartP = new JPanel(new BorderLayout()); 
        cartP.add(new JLabel("CART"), BorderLayout.NORTH); 
        cartP.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        
        // Cart bottom - qty and buttons
        JPanel cb = new JPanel(new FlowLayout()); 
        qtyF = new JTextField("1",4); 
        JButton addB = new JButton("Add to Cart >>"); 
        JButton remB = new JButton("Remove"); 
        totalL = new JLabel("Total: R 0.00"); 
        totalL.setFont(new Font("Arial", Font.BOLD,16));
        cb.add(new JLabel("Qty:")); 
        cb.add(qtyF); 
        cb.add(addB); 
        cb.add(remB); 
        cb.add(totalL); 
        cartP.add(cb, BorderLayout.SOUTH); 
        center.add(cartP); 
        add(center, BorderLayout.CENTER);
        
        // Bottom panel - checkout button
        JPanel bot = new JPanel(); 
        JButton check = new JButton("CHECKOUT & GENERATE BILL"); 
        check.setBackground(new Color(0,150,0)); 
        check.setForeground(Color.WHITE); 
        JButton clear = new JButton("Clear Cart");
        bot.add(clear); 
        bot.add(check); 
        add(bot, BorderLayout.SOUTH);
        
        // Action listeners
        sBtn.addActionListener(e -> loadMeds(searchF.getText())); 
        addB.addActionListener(e -> addToCart()); 
        remB.addActionListener(e -> removeCart()); 
        clear.addActionListener(e -> clearCart()); 
        check.addActionListener(e -> checkout());
    }
    
    // Load medicines with search filter - also stock check feature
    void loadMeds(String k){ 
        try{ 
            medModel.setRowCount(0); 
            PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT medicine_id,name,price,quantity_in_stock FROM medicines WHERE name LIKE ?"); 
            ps.setString(1, "%"+k+"%"); 
            ResultSet rs = ps.executeQuery(); 
            while(rs.next()) 
                medModel.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getInt(4)}); 
        } catch(Exception e){} 
    }
    
    // Add selected medicine to cart - with stock validation
    void addToCart(){ 
        int r = medTable.getSelectedRow(); 
        if(r == -1){ 
            JOptionPane.showMessageDialog(this, "Select medicine"); 
            return;
        } 
        try{ 
            int id = (int)medModel.getValueAt(r,0); 
            String name = medModel.getValueAt(r,1).toString(); 
            double price = Double.parseDouble(medModel.getValueAt(r,2).toString()); 
            int stock = Integer.parseInt(medModel.getValueAt(r,3).toString()); 
            int qty = Integer.parseInt(qtyF.getText()); 
            
            // Validate qty against stock
            if(qty <= 0 || qty > stock){
                JOptionPane.showMessageDialog(this, "Invalid qty. Stock: "+stock); 
                return;
            } 
            double tot = price * qty; 
            cartModel.addRow(new Object[]{id, name, price, qty, tot}); 
            grand += tot; 
            totalL.setText(String.format("Total: R %.2f", grand)); 
        } catch(Exception e){
            JOptionPane.showMessageDialog(this, "Invalid qty");
        } 
    }
    
    // Remove item from cart
    void removeCart(){ 
        int r = cartTable.getSelectedRow(); 
        if(r != -1){ 
            grand -= (double)cartModel.getValueAt(r,4); 
            cartModel.removeRow(r); 
            totalL.setText(String.format("Total: R %.2f", grand)); 
        } 
    }
    
    // Clear all cart items
    void clearCart(){ 
        cartModel.setRowCount(0); 
        grand = 0; 
        totalL.setText("Total: R 0.00"); 
    }
    
    // Checkout - insert into sales and sale_items, update stock, transaction handling
    void checkout(){ 
        if(cartModel.getRowCount() == 0){
            JOptionPane.showMessageDialog(this, "Cart empty"); 
            return;
        } 
        try{ 
            Connection con = DBConnection.getConnection(); 
            con.setAutoCommit(false); // Start transaction - PROG requirement
            
            // Insert into sales table
            PreparedStatement psSale = con.prepareStatement("INSERT INTO sales(total_amount,user_id) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS); 
            psSale.setDouble(1, grand); 
            psSale.setInt(2, userId); 
            psSale.executeUpdate(); 
            ResultSet rs = psSale.getGeneratedKeys(); 
            rs.next(); 
            int saleId = rs.getInt(1); 
            
            // Insert each cart item into sale_items and deduct stock
            for(int i = 0; i < cartModel.getRowCount(); i++){ 
                int medId = (int)cartModel.getValueAt(i,0); 
                int qty = (int)cartModel.getValueAt(i,3); 
                double price = (double)cartModel.getValueAt(i,2); 
                
                PreparedStatement psItem = con.prepareStatement("INSERT INTO sale_items(sale_id,medicine_id,quantity_sold,price_at_sale) VALUES(?,?,?,?)"); 
                psItem.setInt(1, saleId); 
                psItem.setInt(2, medId); 
                psItem.setInt(3, qty); 
                psItem.setDouble(4, price); 
                psItem.executeUpdate(); 
                
                // Update stock quantity
                PreparedStatement psSt = con.prepareStatement("UPDATE medicines SET quantity_in_stock=quantity_in_stock-? WHERE medicine_id=?"); 
                psSt.setInt(1, qty); 
                psSt.setInt(2, medId); 
                psSt.executeUpdate(); 
            } 
            con.commit(); // Commit transaction
            con.setAutoCommit(true); 
            
            // Generate bill
            new Bill(saleId, cartModel, grand); 
            clearCart(); 
            loadMeds(searchF.getText()); // refresh stock display
            
        } catch(Exception e){ 
            JOptionPane.showMessageDialog(this, "Checkout failed: "+e.getMessage()); 
        } 
    }
}