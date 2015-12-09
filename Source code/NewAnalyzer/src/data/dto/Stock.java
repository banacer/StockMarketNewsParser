/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.dto;

import data.dal.MySQLConnection;
import java.sql.*;

/**
 *
 * @author banacer
 */
public class Stock {
    private int id;
    private String ticker;
    private String name;
    
    public Stock()
    {
        this.id = 0;
        this.ticker = this.name = "";
    }
    
    public Stock(int id,String ticker, String name)
    {
        this.id = id;
        this.ticker = ticker;
        this.name = name;
    }
    
    public Stock(int id)
    {
        this.id = id;
        String sql = null;
        try
        {
            sql = "select b.content,a.en_name from entity a join alias b on a.key_ID = b.key_id where b.key_id  = "+id;
            Connection connection = MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
            {
                this.ticker = resultSet.getString(1);
                this.name = resultSet.getString(2);
            }
            else
                throw new Exception("Stock with Id: "+id+" does not exist");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the ticker
     */
    public String getTicker() {
        return ticker;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
            
    
}
