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
public class Entity extends Keyword{
    
    private String name;
    private String description;
    private boolean human;
    
    public Entity()
    {
        super();
        this.isWord = false;
        this.name = "";
        this.description = "";
        this.human = false;
    }
    
    public Entity(int id,String name,String description,boolean human)
    {
        super(id);
        this.isWord = false;
        this.name = name;
        this.description = description;
        this.human = human;
    }            
    
    public Entity(int id,double grade,int num,double sd,String name,String description,boolean human)
    {
        super(id,grade,num,sd);
        this.isWord = false;
        this.name = name;
        this.description = description;
        this.human = human;
    }        
    public Entity(int id)
    {
        super(id);
        this.isWord = false;
        
        String sql = "select * from keyword a join entity b on a.key_ID = b.key_ID where a.key_ID = "+id;
        try{
            Connection connection = MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
            {
                this.grade = resultSet.getDouble(2);
                this.num = resultSet.getDouble(3);
                this.sd = resultSet.getDouble(4);
                this.group = resultSet.getInt(5);                
                this.name = resultSet.getString(7);
                this.description = resultSet.getString(8);
                if(resultSet.getInt(9) == 1)
                    this.human = true;
                else
                    this.human = false;
            }
            else
                throw new Exception("Keyword with id:"+id+"  does not exist");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public String getName()
    {
        return this.name;
    } 
    
    public String getDescription()
    {
        return this.description;
    }
    
    public boolean isHuman()
    {
        return this.human;
    }
}
