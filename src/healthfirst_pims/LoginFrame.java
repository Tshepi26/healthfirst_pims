package healthfirst_pims;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    
    // Declare textfields for login - student
    JTextField userF; 
    JPasswordField passF;
    
    public LoginFrame(){
        // Setting up the login window properties
        setTitle("HealthFirst - Login"); 
        setSize(350,250); 
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4,2,10,10));
        
        // Adding username label and field
        add(new JLabel("Username:")); 
        userF = new JTextField(); 
        add(userF);
        
        // Adding password label and field
        add(new JLabel("Password:")); 
        passF = new JPasswordField(); 
        add(passF);
        
        // Creating buttons for login and clear
        JButton loginBtn = new JButton("Login"); 
        JButton clearBtn = new JButton("Clear");
        add(loginBtn); 
        add(clearBtn);
        
        // Action listener for login button - calls doLogin method
        loginBtn.addActionListener(e -> doLogin());
        
        // Action listener for clear button - clears the fields
        clearBtn.addActionListener(e -> {
            userF.setText(""); 
            passF.setText("");
        });
        
        setVisible(true); // Make the frame visible
    }
    
    // This method checks login from database - PROG732 requirement
    private void doLogin(){
        try{
            // Get connection from DBConnection class
            Connection con = DBConnection.getConnection();
            
            // Using prepared statement to avoid SQL injection
            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, userF.getText()); 
            ps.setString(2, new String(passF.getPassword()));
            
            ResultSet rs = ps.executeQuery(); // Execute query
            
            if(rs.next()){
                // If user found, get role and user_id
                String role = rs.getString("role"); 
                int id = rs.getInt("user_id");
                
                JOptionPane.showMessageDialog(this, "Welcome " + role);
                
                dispose(); // Close login window
                
                // Role based redirection - Admin vs Cashier
                if(role.equals("Admin")) 
                    new DashboardFrame();
                else 
                    new CashierDashboard(id);
            } else {
                // If no user found
                JOptionPane.showMessageDialog(this, "Invalid login");
            }
        } catch(Exception e){ 
            // Show error if database connection fails
            JOptionPane.showMessageDialog(this, e.getMessage()); 
        }
    }
    
    // Main method to run the login screen
    public static void main(String[] args){ 
        new LoginFrame(); 
    }
}