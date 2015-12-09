/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.dal;

import data.dto.Stock;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author banacer
 */
public class WordServices {
    private Connection connection;
    private Vector<String> suffix;
    private int group = 0;
    private boolean used = true;
    
    public WordServices()
    {
        try {
            this.connection = MySQLConnection.getConnection();
            suffix = new Vector<String>();
        } catch (SQLException ex) {
            System.out.println();
            ex.printStackTrace();
        }
    }
    
    public boolean addWord(String word)
    {
        String sql1 = "insert into keyword (key_grade,key_num,key_sd) values (0,0,0)";
        String sql2 = "select max(key_id) from keyword";
        String sql3 = null;
        
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            
            connection = MySQLConnection.getConnection();
            
            statement = connection.createStatement();
            statement.executeUpdate(sql1);
            
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql2);
            if(resultSet.next())
            {
                int id = resultSet.getInt(1);
                sql3 = "insert into word values ('"+id+"','"+word+"')";
            }
            
           statement = connection.createStatement();
           statement.executeUpdate(sql3);
             
           return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean addEntity(String name,String description, int human)
    {
        String sql1 = "insert into keyword (key_grade,key_num,key_sd) values (0,0,0)";
        String sql2 = "select max(key_id) from keyword";
        String sql3 = null;
        
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            
            connection = MySQLConnection.getConnection();
            
            statement = connection.createStatement();
            statement.executeUpdate(sql1);
            
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql2);
            if(resultSet.next())
            {
                int id = resultSet.getInt(1);
                sql3 = "insert into entity values ('"+id+"','"+name+"','"+description+"','"+human+"','0')";
            }
            
           statement = connection.createStatement();
           statement.executeUpdate(sql3);
             
           return true;
        }
        catch(Exception ex)
        {
            //ex.printStackTrace();
            return false;
        }
    }
    public boolean addStock(String name,String description)
    {
        String sql1 = "insert into keyword (key_grade,key_num,key_sd) values (0,0,0)";
        String sql2 = "select max(key_id) from keyword";
        String sql3 = null;
        
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            
            connection = MySQLConnection.getConnection();
            
            statement = connection.createStatement();
            statement.executeUpdate(sql1);
            
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql2);
            if(resultSet.next())
            {
                int id = resultSet.getInt(1);
                sql3 = "insert into entity values ('"+id+"','"+name+"','"+description+"','0','1')";
            }
            
           statement = connection.createStatement();
           statement.executeUpdate(sql3);
             
           return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    public int createGroup(String word)
    {
        String myWord = transformWord(word);        
        String sql1 = "select key_id,content from word where content like '"+myWord+"%'  order by key_ID";
        String sql2 = null;
        Vector<Integer> kList = new Vector<Integer>();
        Vector<String> cList = new Vector<String>();
        
        try{
            connection = MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = null;
            if(used)
            {
                this.group = newGroup();
                this.used = false;
            }
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql1);
            
            while(resultSet.next())
            {
                if(!used)
                    used = true;
                kList.addElement(resultSet.getInt(1));
                cList.addElement(resultSet.getString(2));
            }            
            resultSet.close();
            boolean done = false;
            for(int i = 0; i < kList.size(); i++)
            {
                boolean composition = isWordComposition(myWord, cList.elementAt(i));
                
                if(composition)
                {                    
                    statement = connection.createStatement();                                    
                    sql2 = "update keyword set gr_ID = '"+this.group+"' where key_ID = '"+kList.elementAt(i) +"'";                    
                    System.out.println(sql2);
                    statement.executeUpdate(sql2);                    
                }
                if(!done && composition)
                {
                    
                    sql2 = "update keygroup set source = '"+kList.elementAt(i) +"' where gr_ID = '"+this.group+"'";
                    statement = connection.createStatement();
                    statement.executeUpdate(sql2);
                    this.used = true;
                    done = true;
                }
                
            }
            System.out.println(sql1);
            System.out.println(kList.elementAt(kList.size() - 1));
            return kList.elementAt(kList.size() - 1);
            
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return -1;
        }        
    }
    
    public String transformWord(String word)
    {
        String result = null;
        try {
            if(suffix.size() == 0)
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("suffix.txt")));
                String suff = null;
                while((suff = br.readLine()) != null)
                {
                    suffix.addElement(suff);
                }
            }
            boolean done = false;
            for(int i = 0; i < suffix.size(); i++)
            {
                if(word.length() > suffix.elementAt(i).length())
                {
                    String print = word.substring(word.length() - suffix.elementAt(i).length(), word.length());
                    //System.out.println("print: "+print+" suffix: "+suffix.elementAt(i));
                    if(print.equalsIgnoreCase(suffix.elementAt(i)) )
                    {
                        result = word.substring(0, word.length() - suffix.elementAt(i).length());
                        done = true;
                    }
                }
            }
            if(!done)
                return word;
            if(result.charAt(result.length() - 1) == 'a' ||result.charAt(result.length() - 1) == 'e' || result.charAt(result.length() - 1) == 'o' || result.charAt(result.length() - 1) == 'i' || result.charAt(result.length() - 1) == 'u')
                return result.substring(0, result.length() - 1);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }        
    }
    
    public String getWord(int ID)
    {
        try{
            String sql = "select content from word where key_ID = "+ID;
            connection = MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resulSet = statement.executeQuery(sql);
            if(resulSet.next())
                return resulSet.getString(1);
            return null;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    
    public int newGroup()
    {
        String sql1 = "insert into keygroup (gr_grade,gr_num,gr_sd) values (0,0,0)";
        try{
            connection = MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = null;   
            int rows = statement.executeUpdate(sql1);
            boolean full = false;
            if(rows > 0)
            {
                sql1 = "select max(gr_ID) from keygroup";
                statement = connection.createStatement();
                resultSet = statement.executeQuery(sql1);
                resultSet.next();
                return resultSet.getInt(1);
            }
            else
                return 0;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return 0;
        }
    }
    
    public boolean isWordComposition(String root,String word)
    {
        String part1 = word.substring(0, root.length());
        if(part1.equalsIgnoreCase(root))
        {
            String part2 = word.substring(root.length(), word.length());
            if(part2.length() == 0)
                return true;
            else
            {
                boolean found = true;
                while(part2.length() > 0 && found == true)
                {
                    found = false;
                    for(int i = 0; i < suffix.size(); i++)
                    {
                        if(part2.length() > suffix.elementAt(i).length())
                        {
                            String temp = part2.substring(0, suffix.elementAt(i).length());
                            if(temp.equalsIgnoreCase(suffix.elementAt(i)))
                            {
                                found = true;
                                part2 = part2.substring(suffix.elementAt(i).length(), part2.length());
                                break;
                            }                            
                        }
                        else
                        {
                            if(part2.length() == suffix.elementAt(i).length())
                            {
                                if(part2.equalsIgnoreCase(suffix.elementAt(i)))
                                {
                                    found = true;
                                    part2 = part2.substring(suffix.elementAt(i).length(), part2.length());
                                    break;
                                }
                            }
                        }
                    }
                }
                return found;
            }
        }
        else
            return false;
    }
    
    public int insertWord(String word,String pos,int groupId)
    {
        String sql2 = null;
        try{
            String sql1 = "insert into keyword (key_grade,key_num,key_sd,gr_ID) values (0,0,0,'"+groupId+"')";
            sql2 = "select max(key_id) from keyword";
            connection = MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = null;
            statement.executeUpdate(sql1);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql2);
            if(resultSet.next())
            {
                int wordId = resultSet.getInt(1);
                sql2 = "insert into word values ('"+wordId+"','"+word+"','"+pos+"',1)";
                statement = connection.createStatement();
                statement.executeUpdate(sql2);
                return wordId;
            }
            return -1;
        }
        catch(Exception ex)
        {
            System.err.println(sql2);
            ex.printStackTrace();
            return -1;
        }
    }
    
    public int insertGroup()
    {
        try
        {
            String sql = "insert into keygroup (gr_grade,gr_num,gr_sd) values (0,0,0)";
            connection = MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            sql = "select max(gr_ID) from keygroup";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
                return resultSet.getInt(1);
            else 
                return -1;
        }   
        catch(Exception ex)
        {
            ex.printStackTrace();
            return -1;
        }
    }
    
    public boolean addSourcetoGroup(int groupId,int sourceId)
    {
        try
        {
            String sql = "update keygroup set source = '"+sourceId+"' where gr_ID = '"+groupId+"'";
            connection = MySQLConnection.getConnection();
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
    
    public boolean isNoun(String word)
    {
        String sql = null;
        try
        {
            sql = "select count(*),canUse from word where content LIKE '"+word.toLowerCase()+"' and pos = 'N'";
            if(connection == null)
                connection = MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
            {
                int count = resultSet.getInt(1);
                if(count > 0)
                {
                    int canUse = resultSet.getInt(2);
                    if(canUse == 1)
                        return true;                    
                }
                return false;
            }                
            return false;
        }
        catch(Exception ex)
        {
            System.err.println(sql);
            ex.printStackTrace();
            return false;
        }
    }
    public boolean isOnlyNoun(String word)
    {
        String sql = null;
        try
        {
            sql = "select * from word where content LIKE '"+word+"' and ( pos = 'V' OR pos = 'A')";
            if(connection == null)
                connection = MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
                return false;
            return true;
        }
        catch(Exception ex)
        {            
            return true;
        }
    }
    
    
    public boolean blockWord(String word)
    {
        String sql = null;
        try
        {
            sql = "update word set canUse = 0 where lower(content) LIKE lower('"+word+"')";
            System.out.println(sql);
            if(connection == null)
                connection = MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            return true;
        }
        catch(Exception ex)
        {
            System.err.println(sql);
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean isBlocked(String word)
    {
        String sql = null;
        try
        {
            sql = "select distinct canUse from word where lower(content) like '"+word.toLowerCase()+"'";
            //System.out.println(sql);
            if(connection == null)
                connection = MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
            {
                int canUse = resultSet.getInt(1);
                if(canUse == 0)
                    return true;
                return false;
            }
            return false;
        }
        catch(Exception ex)
        {           
            return false;
        }
    }
    
    public boolean EntityInDB(String content)
    {
        String sql = null;
        try
        {
            if(connection == null)
                connection = MySQLConnection.getConnection();
            sql = "select * from entity where lower(en_name ) like lower('"+content+"')";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
                return true;
            return false;
        }
        catch(Exception ex)
        {
            
            return false;
        }
    }
    
    public boolean addAlias(String entity,String alias)
    {        
        String sql = null;
        try
        {
            if(connection == null)
                connection = MySQLConnection.getConnection();
            sql = "select key_ID from entity where lower(en_name) like lower('"+entity+"')";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
            {
                int keyId = resultSet.getInt(1);
                sql = "insert into alias values ('"+keyId+"','"+alias+"')";
                statement = connection.createStatement();
                int rows = statement.executeUpdate(sql);
                if(rows > 0)
                    return true;
                return false;
            }
            else
                return false;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean AliasInDB(String content)
    {
        String sql = null;
        try
        {
            if(connection == null)
                connection = MySQLConnection.getConnection();
            sql = "select * from alias where lower(content) like lower('"+content+"')";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
                return true;
            return false;
        }
        catch(Exception ex)
        {
            
            return false;
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
}