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
public class Group {
    private int id;
    private Word source;
    private double grade;
    private double num;
    private double sd;
    
    public Group()
    {
        id = 0;
        source = null;
        grade = 0;
        num = 0;
        sd = 0;
    }
    
    public Group(int id)
    {
        this.id = id;
        String sql = "select * from keygroup where gr_ID = "+id;
        try{
            Connection connection = MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
            {
                this.source = new Word(resultSet.getInt(2));
                this.grade = resultSet.getDouble(3);
                this.num = resultSet.getDouble(4);
                this.sd = resultSet.getDouble(5);
            }
            else
                throw new Exception("Group with id:"+id+"  does not exist");
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
     * @return the source
     */
    public Word getSource() {
        return source;
    }

    /**
     * @return the grade
     */
    public double getGrade() {
        return grade;
    }

    /**
     * @return the num
     */
    public double getNum() {
        return num;
    }

    /**
     * @return the sd
     */
    public double getSd() {
        return sd;
    }
}
