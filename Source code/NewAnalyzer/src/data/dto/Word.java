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
public class Word extends Keyword{
    
    private String content;
    
    public Word()
    {
        super();
        this.content = "";
        this.isWord = true;
    }
    
    public Word(int id,String content)
    {
        super(id);
        this.content = content;
        this.isWord = true;
    }
    
    public Word(int id,String content,double grade,int num,double sd,int group)
    {
        super(id,grade,num,sd,group);
        this.content = content;        
        this.isWord = true;
    }
    /**
     * Give it the word and it gets all the information
     * @param content 
     */
    public Word(String content)
    {
        super();
        this.isWord = true;
        String sql = "select * from keyword a join word b on a.key_ID = b.key_ID where b.content = '"+content+"'";
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
                this.content = resultSet.getString(7);                
            }
            else
                throw new Exception("Keyword with id:"+getId()+"  does not exist");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    /**
     * Give it the id and gets all the information
     * @param id 
     */
    public Word(int id)
    {
        super(id);
        this.isWord = true;
        
        String sql = "select * from keyword a join word b on a.key_ID = b.key_ID where a.key_ID = "+id;
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
                this.content = resultSet.getString(7);                
            }
            else
                throw new Exception("Keyword with id:"+id+"  does not exist");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }
}