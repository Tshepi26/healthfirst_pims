package healthfirst_pims;
import java.sql.*;

public class DBConnection {
    public static Connection getConnection(){
        try{
            //  password is Admin123
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/healthfirst_pims","root","Admin123");
        }catch(Exception e){
            System.out.println("DB Error: " + e.getMessage());
            return null;
        }
    }
}
