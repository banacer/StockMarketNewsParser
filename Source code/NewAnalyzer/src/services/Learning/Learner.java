/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package services.Learning;

import data.dal.*;
import data.dto.*;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.FailedLoginException;
import multithreading.WikiThread;
import org.wikipedia.Wiki;

/**
 *
 * @author banacer
 */
public class Learner {
    
    public Vector<Keyword> kList;
    public Vector<String> wList;
    public int last;
    private Wiki wiki;
    private static String login = "Newanalyzer";
    private static char[] password = "mynews".toCharArray();
    public Learner() throws IOException, FailedLoginException
    {
        wList = new Vector<String>(200);
        kList = new Vector<Keyword>(200);
        last = 0;
        //wiki = new Wiki("en.wikipedia.org");
        //wiki.setThrottle(5000); // set the edit throttle to 0.2 Hz
        //wiki.login(login, password); // log in as user
    }
    /**
     * Decomposes the text into a list of words stored in the vector wList
     * @param text
     * @return whether operation was done successfully or not
     */
    public boolean tokenText(Text text)
    {
        //Clear the data structures in case they are already filled
        wList.clear();
        kList.clear();
        Boolean ignore = false;        
        StringTokenizer token = new StringTokenizer(text.getText(), " ,.;:/()<>{}?!-_%");
        while(token.hasMoreTokens())
        {         
            ignore = false;
            try{
                String w = token.nextToken();
                if(w.length() == 1 && !w.equals("I") && !w.equalsIgnoreCase("a") && !isNumber(w))
                    w += ".";
                if(containsApostrophy(w) > 0)
                {                    
                    String[] res = doSpecialTokenizing(w);
                    
                    wList.add(res[0]);
                    String str = res[1];
                    if(str.equalsIgnoreCase("nt"))
                        w = "not";
                    else
                        ignore = true;
                    
                }
                //THIS PART SERVES TO REMOVE NUMBER AS THEY ARE INSIGNIFICANT IN THE ANALYSIS
                boolean parsed = true;
                try{
                    Integer.parseInt(w);
                }
                catch(NumberFormatException e)
                {
                    parsed = false;
                }
                if(!parsed && !ignore && w.length() > 0)
                    wList.add(w);
            }
            catch(Exception ex)
            {                
                ex.printStackTrace();                
            }            
        }
        return true;
    }
    /**
     * Assigns a grade to the keyword
     * @param keyword
     * @param change
     * @return 
     */
    public boolean gradeKeyword(Keyword keyword,double change)
    {
        String sql = null;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            connection = MySQLConnection.getConnection();
            statement = connection.createStatement();
            double num = keyword.getNum() + 1;
            double grade = ((keyword.getGrade() + change)* keyword.getNum())/num;
            double sd = (keyword.getSd() * keyword.getNum() + Math.abs(grade - change)) / num;
            sql = "update keyword set key_grade = '"+grade+"',key_num = '"+num+"',key_sd = '"+sd+"' where key_ID = "+keyword.getId();
            resultSet = statement.executeQuery(sql);
            return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    /**
     * Assigns a grade to the group of keywords
     * @param group
     * @param change
     * @return 
     */
    public boolean gradeGroup(Group group,double change)
    {
        String sql = null;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            connection = MySQLConnection.getConnection();
            statement = connection.createStatement();
            double num = group.getNum() + 1;
            double grade = ((group.getGrade() * group.getNum()) + change) / num;
            double sd = ((group.getSd() * group.getNum()) + Math.abs(grade - change)) / num;
            sql = "update keygroup set key_grade = '"+grade+"',key_num = '"+num+"',key_sd = '"+sd+"' where gr_ID = "+group.getId();
            resultSet = statement.executeQuery(sql);
            return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Checks the closest keyword to it in the database and returns a list of keywords
     * @param content
     * @return 
    **/
    public DBData[] isMeaningfulWord(String content)
    {
        String sql = "select en_name,key_ID from entity where lower(en_name) LIKE lower('%"+content+"%')";
                
        try{
            Connection connection = MySQLConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            DBData[] data = null;
            Vector<DBData> v = new Vector<DBData>();
            if(resultSet.next())
            {
                    String name = resultSet.getString(1);
                    DBData d = new DBData(resultSet.getInt(2), name, false);
                    v.add(d);
                    while(resultSet.next())
                    {
                        d = new DBData(resultSet.getInt(2), resultSet.getString(1), false);
                        v.add(d);
                    }                    
            }                
            
            sql = "select b.key_ID,b.en_name,a.content from alias a join entity b on a.key_id = b.key_ID where lower(a.content) LIKE lower('"+content+"')";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if(resultSet.next())
            {
                DBData d = new DBData(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3), false);
                v.add(d);
                while(resultSet.next())
                {
                    d = new DBData(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3), false);
                    v.add(d);
                }                
            }      
            resultSet.close();
            sql = "select * from word where lower(content) LIKE lower('"+content+"')";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
                
            if(resultSet.next())
            {
                DBData d = new DBData(resultSet.getInt(1), resultSet.getString(2), true); 
                v.add(d);                
                while(resultSet.next())
                {
                    d = new DBData(resultSet.getInt(1), resultSet.getString(2), true); 
                    v.add(d);
                }
            }
            
            data = new DBData[v.size()];
            for(int i = 0; i < v.size(); i++)
            {
                data[i] = v.elementAt(i);
            }
            return data;            
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get's the candidate keyword and grades it based on the location of the word in the text. This is the grading function to choose
     * the best and most suitable word to it.
     * @param Index
     * @param word
     * @return 
     */
    public double gradeByPoint(int Index,String word)
    {
        try
        {
            
        double points = 0;
        int k = 0,l = 0;
        int j = 0;
        int myIndex = 0;
        int wrong = 0;
        StringTokenizer token = new StringTokenizer(word," ,-_");
        Vector<String> list = new Vector<String>();
        
        while(token.hasMoreTokens())
            list.add(token.nextToken());
        myIndex = Index;
        j = FindWordInExternalList(wList.elementAt(Index), list);
        if(j == -1)
            return 0;
        int ind = 0;
        //This condition is in the case the sentence begins with eg: "bush" while in the db it's
        //geoges walker bush and going to the beggining does not exist
        if(myIndex - j < 0)
        {
            myIndex += j;
            l = j;
        }
        for(int i = l; i < list.size() && (myIndex - j) < wList.size(); i++)
        {
            if(wrong == 3)
            {
                myIndex++;
                i -= wrong;
                wrong = 0;
            }
            ind = myIndex - j;            
            if(SameOrEqual((wList.elementAt(ind)),list.elementAt(i)))
            {
                
                points++;
                myIndex++;
            }
            else
                wrong++;
        }
        last = ind;
        return points/( double ) list.size();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return 0;
        }
    }
    
    public double gradePoint(int index, String word) throws Exception
    {
        try
        {
            StringTokenizer token = new StringTokenizer(word," ,-_");
            Vector<String> list = new Vector<String>();
        
            while(token.hasMoreTokens())
                list.add(token.nextToken());
            Vector<String> ls = new Vector<String>();
            
            for(int i = 0; i < list.size(); i++)
                ls.add(this.wList.elementAt(index+i));
            double count = 0;
            for(int i = 0; i < ls.size(); i++)
            {
                if(contains(ls.elementAt(i), list))
                    count++;
            }            
            return (count/ ls.size());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return 0;
        }
    }
    
    public boolean contains(String word, Vector<String> list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            if(SameOrEqual(word, list.elementAt(i)))
                return true;
        }
        return false;
    }
    /**
     * Function to equate between words
     * @param word1
     * @param word2
     * @return 
     */
    public boolean SameOrEqual(String word1,String word2)
    {
        //System.out.println(word1+" = "+word2 );
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        if(word1.equalsIgnoreCase(word2))
            return true;
        else
        {
            if(word1.length() < 2 || word2.length() < 2)
                return false;
            if(word1.charAt(1) == '.' || word2.charAt(1) == '.')
            {                
                if(word1.charAt(0) == word2.charAt(0))
                    return true;
            }
            
            return false;
        }
    }
    /**
     * 
     * @param word
     * @return 
     */
    public int findWordInList(String word)
    {
        for(int i = 0; i < wList.size(); i++)
        {
            if(wList.elementAt(i).equalsIgnoreCase(word))
                return i;
        }
        return -1;
    }
    
    public int FindWordInExternalList(String word,Vector<String> list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            //System.out.println(list.elementAt(i)+"  "+word);
            if(list.elementAt(i).equalsIgnoreCase(word))
            {
                //System.out.println("ddd  "+i);
                return i;
            }
        }
        return -1;
    }
    /**
     * Adds a the keyword to the important and final list of words
     * @param id
     * @param word 
     */
    public void addToList(int id,boolean word)
    {
        if(word)
        {
            Word w = new Word(id);
            kList.add(w);
        }
        else
        {
            Entity e = new Entity(id);
            kList.add(e);                    
        }
    }
    /**
     * Takes the id in the wList and takes the word at that id, searches for its equivalent in the db and stores it in the final list kList 
     * @param last 
     */
    public void AnalyzeText(int last) throws IOException, FailedLoginException, InterruptedException
    {
        WordServices s = new WordServices();
        DBData[] ls = isMeaningfulWord(this.wList.elementAt(last));
        if(ls == null || ls.length == 0)
        {
            System.out.println("Hello 0: "+this.wList.elementAt(last));
            WikiThread wikiThread = new WikiThread(this.wList.elementAt(last));
            wikiThread.start();
            Thread.sleep(1000);
            if(!wikiThread.isFinished())
            {
                System.out.println("Killed: "+this.wList.elementAt(last));
                wikiThread.interrupt();
            }
            /*else
            {
                if(wikiThread.found)
                    this.last--;
            }*/
            return;
        }
        double grade = 0,tempGrade = 0;
        int index = 0;
        for(int i = 0; i < ls.length; i++)
        {            
            if(!ls[i].isWord())
            {
                try {
                    //System.out.println(ls[i].getContent());
                     if(ls[i].getAlias() != null)
                        tempGrade = gradePoint(last,ls[i].getAlias());
                     else       
                        tempGrade = gradePoint(last,ls[i].getContent());
                     
                     if(tempGrade > grade)
                     {
                         grade = tempGrade;
                         index = i;
                     }
                    //System.out.println("hello: "+index);
                    //System.out.println("hello: "+index);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }            
        }
        addToList(ls[index].getId(), ls[index].isWord());
    }
    /**
     * Function to analyze the whole article
     */
    public void analyzeArticle() throws Exception
    {
        
        while(this.last < wList.size()) 
        {
            if(last == 15)
                last = 20;
            System.out.println(last+": "+wList.elementAt(last));
            AnalyzeText(last);
            last++;
        }
    }
    
    public String[] searchForEntity(String entity)
    {
        try {            
            String[][] result = wiki.search(entity, 0);
            String[] toReturn = new String[result.length];
            
            for(int i = 0; i < result.length;i++)
            {                
                    //System.out.println(result[i][0]);
                    toReturn[i] = result[i][0];
            }                                    
            return toReturn;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public String getWikiArticle(String title)
    {
        try {
            return wiki.getPageText(title);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    /**
     * Extract info and store in the DB
     * @param text
     * @param title
     * @return 
     */
    public boolean extractInfo(String text,String title)
    {
        WordServices service = new WordServices();
        if(text.contains("birth_date"))
        {
            //It's a person
            service.addEntity(title, "", 1);
        }
        else
        {
            if(text.contains("logo") || text.contains("founded") || text.contains("foundation"))
            {
                //Its an organization
                service.addEntity(title, "", 0);
            }
            else
            {
                
                if(text.contains("manufacturer") || text.contains("released"))
                {
                    service.addEntity(title, "", 2);
                }
                else
                    service.addEntity(title, "", 0);
            }
        }
        return true;
    }
    /**
     * Extract possible entity (human or organization) names from the text 
     * @return 
     */
    public String[] extractFullNames()
    {
        WordServices w = new WordServices();
        Vector<String> names = new Vector<String>();
        try
        {
            int count = 0;
            boolean yes = true;
            for(int i = 0; i < wList.size(); i++)
            {                
                yes = true;
                if(w.isOnlyNoun(wList.elementAt(i)))
                {   
                    if(!w.isBlocked(wList.elementAt(i)))
                    {
                        yes = false;
                        count++;
                    }
                }
                if(yes)
                {
                    if(count >= 2)
                    {                        
                        System.out.println("count = "+count);
                        String temp = "";
                        for(int j = i -count; j < i; j++)
                        {
                            temp += wList.elementAt(j)+" ";
                            System.out.print(wList.elementAt(j)+" ");
                        }
                        System.out.println();
                        names.add(temp);
                    }
                    
                    count = 0;
                }
            }
            //REMOVE ENTITIES ALREADY IN DB
            int size = names.size();
            for(int i = 0; i < size; i++)
            {
                if(w.EntityInDB(names.elementAt(i)) || w.AliasInDB(names.elementAt(i)))
                {
                    names.remove(i);
                    i--;
                    size--;
                }
            }
            //Conversion from vector to array
            String[] list = new String[names.size()];
            for(int i = 0; i < names.size(); i++)
                list[i] = names.elementAt(i);
            names = null;
            System.gc();
            
            return list;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    
    public void fullTextDecomposition(Text text) throws Exception
    {
        long seconds = 5;
        int threadNum = 5;
        this.last = 0;
        //Decomposes the text file
        this.tokenText(text);
        
        
        //Extract possible entity names
        //This part uses multithreading to speed up the work
        //CAUTION!!!!: This part can take several minutes to execute
        String names[] = this.extractFullNames(); //ONLY NAMES NOT IN DB
        WikiThread[] threadList = new WikiThread[threadNum];
        for(int i = 0; i < (names.length / threadNum); i++)
        {
            for(int j = 0; j < threadNum; j++)
            {
                try
                {                
                    if(names.length >= (i*threadNum + j) )
                    {
                        threadList[j] = new WikiThread(names[i*threadNum + j]);
                        threadList[j].start();
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }                
            }
            Thread.sleep( 1000* seconds);
            
            for(int j = 0; j < threadNum; j++)
            {
                if(!threadList[j].isFinished())
                {
                    System.out.println("Killed:  "+names[i*threadNum + j]);
                    threadList[j].interrupt();
                }
            }
        }
        //Identification of words and
        analyzeArticle();      
        /*System.out.println("Here it comeessss:::");
        for(int i = 0; i < this.kList.size(); i++)
        {
            if(kList.elementAt(i).isIsWord())
                System.out.println(((Word)kList.elementAt(i)).getContent());
            else
                System.out.println(((Entity)kList.elementAt(i)).getName());
        }
         * 
         */
    }
    
    public boolean isNumber(String str)
    {
        try{
            Integer.parseInt(str);
            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    }
    private int containsApostrophy(String str)
    {
        int count = 0;
        for(int i = 0; i < str.length(); i++)
        {
            if((int) str.charAt(i) == 65533)
                count++;
        }
        return count;
    }
    
    private String[] doSpecialTokenizing(String str)
    {
        int count = 0;
        int tokens = containsApostrophy(str) + 1;
        String[] list = new String[tokens];
        String temp = new String();
        for(int i = 0; i < str.length(); i++)
        {
            
            if((int) str.charAt(i) != 65533)
                temp += str.charAt(i);
            else
            {
                list[count] = temp;
                i++;
                count++;
                temp = new String();
            }
        }
        list[count] = temp;
        return list;        
    }
    public String[] searchList(String content)
    {
        StringTokenizer tok = new StringTokenizer(content);
        String[] l = new String[tok.countTokens()];
        int j = 0;
        while(tok.hasMoreTokens())
        {
            l[j] = tok.nextToken();
            j++;
        }
        String query = "";
        for(int i = 0; i < j; i++)
        {
            query += l[i];
            if(i  != (j-1))
                query += "%20";
        }
        
        String link = "http://en.wikipedia.org/w/api.php?action=opensearch&search="+query;
        String data = download(link);
        String[] result = parseResults(data);
        return result;
        
    }
    
    public String download(String link)
    {
        URL url;
        InputStream is = null;
        DataInputStream dis;
        String line;
        String data = "";

        try 
        {
            url = new URL(link);
            is = url.openStream();  // throws an IOException
            dis = new DataInputStream(new BufferedInputStream(is));            
            while ((line = dis.readLine()) != null) {
                data += line+"\n";
            }
            return data;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public String[] parseResults(String data)
    {
        data = data.substring(1);
        StringTokenizer t1 = new StringTokenizer(data,"[");
        String search = t1.nextToken();
        String results = t1.nextToken();
        StringTokenizer t2 = new StringTokenizer(results,"\"");
        Vector<String> vlist = new Vector<String>();
        while(t2.hasMoreTokens())
        {
            String str = t2.nextToken();
            if(!str.equalsIgnoreCase(","))
                vlist.addElement(str);
        }
        String[] list = new String[vlist.size() - 1];
        for(int i = 0; i < list.length; i++)
            list[i] = vlist.elementAt(i);
        return list;            
    }
}
