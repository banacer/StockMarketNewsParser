/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import data.dto.Article;
import data.dto.Stock;
import java.sql.*;
import java.util.Vector;
import java.util.Date;

/**
 *
 * @author banacer
 */
public class DBService {
    Connection connection;
    
    public int addArticle(String url,int stockId,java.sql.Timestamp date)
    {
        String sql = null;
        try{
            sql = "insert into article (ar_link,stock_id,date_appeared) values ('"+url+"','"+stockId+"','"+date.toString()+"')";
            connection = data.dal.MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            
            sql = "select max(ar_ID) from article";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
            {
                return resultSet.getInt(1);
            }
            else
            {
                return -1;
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return -1;
        }
    }
    
    public boolean setArticleDownloaded(int arId)
    {
        String sql = null;
        try{
            sql = "update article set downloaded = 1 where ar_ID = '"+arId+"'";
            connection = data.dal.MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean setArticleAnalyzed(int arId)
    {
        String sql = null;
        try{
            sql = "update article set analyzed = 1 where ar_ID = '"+arId+"'";
            connection = data.dal.MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean isDownloaded(int arId)
    {
        String sql = null;
        try{
            sql = "select downloaded from article where ar_ID = '"+arId+"'";
            connection = data.dal.MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
            {
                if(resultSet.getInt(1) == 1)
                    return true;                
            }
            return false;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean isAnalyzed(int arId)
    {
        String sql = null;
        try{
            sql = "select analyzed from article where ar_ID = '"+arId+"'";
            connection = data.dal.MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
            {
                if(resultSet.getInt(1) == 1)
                    return true;                
            }
            return false;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    public boolean exist(String url)
    {
        String sql = null;
        try{
            sql = "select * from article where ar_link LIKE '"+url+"'";
            connection = data.dal.MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
                return true;
            return false;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    
    public Timestamp StringToDate(String str)
    {
        String sql = null;
        try{
            sql = "select str_to_date('"+str+"','%a, %d %M %Y %H:%i:%s')";
            connection = data.dal.MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
                return resultSet.getTimestamp(1);
            return null;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    
    public Stock[] getStockList()
    {
        String sql = null;
        try
        {
            sql = "select a.key_ID,b.content,a.en_name from entity a join alias b on a.key_ID = b.key_id where a.listed = 1";
            if(connection == null)
                connection = data.dal.MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            Vector<Stock> v = new Vector<Stock>();
            while(resultSet.next())
                v.add(new Stock(resultSet.getInt(1),resultSet.getString(2), resultSet.getString(3)));
            Stock[] result = new Stock[v.size()];
            for(int i = 0; i < v.size(); i++)
                result[i] = v.elementAt(i);
            v = null;
            return result;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }               
    }
    
    public Stock getStock(String ticker)
    {
        String sql = null;
        try
        {
            sql = "select a.key_ID,b.content,a.en_name from entity a join alias b on a.key_ID = b.key_id where b.content = '"+ticker+"'";
            if(connection == null)
                connection = data.dal.MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
                return new Stock(resultSet.getInt(1),resultSet.getString(2), resultSet.getString(3));
            return null;                        
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    
    public Article getArticle(int ArticleId)
    {
        String sql = null;
        try
        {
            sql = "select ar_link,stock_id,downloaded,analyzed,date_appeared from article where ar_ID = '"+ArticleId+"'";
            if(connection == null)
                connection = data.dal.MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
            {
                boolean downloaded = false;
                boolean analyzed = false;
                if(resultSet.getInt(3) == 1)
                    downloaded = true;
                if(resultSet.getInt(4) == 4)
                    analyzed = true;
                Article article = new Article(ArticleId, resultSet.getString(1),resultSet.getInt(2), downloaded, analyzed, resultSet.getTimestamp(5));
                return article;
            }
            return null;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    
    public Integer[] getToBeAnalyzedList()
    {
        String sql = null;
        try
        {
            sql = "select ar_ID from article where downloaded = 0 and analyzed = 0";
            if(connection == null)
                connection = data.dal.MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            Vector<Integer> v = new Vector<Integer>();
            while(resultSet.next())
                v.add(resultSet.getInt(1));
            Integer[] result = new Integer[v.size()];
            for(int i = 0; i < v.size(); i++)
                result[i] = v.elementAt(i);
            return result;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
}
