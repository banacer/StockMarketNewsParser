package data.dal;

import java.sql.*;

/**
 *
 * @author banacer
 */
public class MySQLConnection {

    private static Connection conn = null;    
    
    public static synchronized Connection getConnection() throws SQLException
    {
        if(!(conn == null || conn.isClosed()))
            return conn;
        
        String url = "jdbc:mysql://localhost:3306/";
        String dbName = "trading";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "root"; 
        String password = "";        
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url+dbName,userName,password);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {           
            return conn;
        }
    }        
}
