/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multithreading;

import data.dal.WordServices;
import java.io.IOException;
import java.io.*;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.security.auth.login.FailedLoginException;
import org.wikipedia.Wiki;

/**
 *
 * @author banacer
 */
public class WikiThread extends Thread{
    private Wiki wiki;
    private static String login = "Newanalyzer";
    private static char[] password = "mynews".toCharArray();
    private String title;
    private boolean finished;
    public boolean found = false;
    
    public WikiThread(String title) throws IOException, FailedLoginException, InterruptedException
    {        
        this.title = title;
        finished = false;        
    }
    
    public void run()
    {
        int tentative = 0;
        wiki = WikiStaticCollection.getWiki();
        while(wiki == null && tentative < 5)
        {
            tentative++;
            try 
            {
                //WAIT FOR A WIKI OBJECT TO BE RELEASED
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            wiki = WikiStaticCollection.getWiki();
        }
        if(tentative == 5)
            return;
        String[] results = searchList(title);
        int index = -1; // The index of the most appropriate title
        //CHOOSE THE MOST APPROPRIATE TITLE HERE
        double grade = 0;
        double temp = -1;
        for(int i = 0; i < results.length; i++)
        {
            temp = compare(this.title, results[i]);       
            if(temp > grade)                              
            {
                temp = grade;
                index = i;
            }
        }
        index = 0; //TO ACTIVATE COMPARISON REMOVE THIS LINE OF CODE
        //
        if(results.length > 0)
        {
            this.found = true;
            String article =  getWikiArticle(results[index]);
            boolean store = false;
            if(article != null)
                store = extractInfo(article, results[0],title);
            
        }
        //RETURN THE WIKI OBJECT TO THE COLLECTION
        WikiStaticCollection.returnWiki(wiki);
        this.finished = true;
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
     * @return: true means extracted and placed into the database. False means ignored
     */
    public boolean extractInfo(String text,String title,String alias)
    {
        WordServices service = new WordServices();
        if(text.contains("birth_date"))
        {
            //It's a person
            service.addEntity(title, "", 1);
            service.addAlias(title, alias);
        }
        else
        {
            if(text.contains("logo") || text.contains("founded") || text.contains("foundation"))
            {
                //Its an organization
                service.addEntity(title, "", 0);
                service.addAlias(title, alias);
            }
            else
            {
                service.addEntity(title, "", 0);
                //----- IGNORE
                //return false;
            }
        }
        return true;
    }

    /**
     * @return the finished
     */
    public boolean isFinished() {
        return finished;
    }
    
    public double compare(String word1,String word2)
    {
        Vector<String> w1 = new Vector<String>();
        Vector<String> w2 = new Vector<String>();
        
        StringTokenizer StrTok = new StringTokenizer(word1," ,-_");
        while(StrTok.hasMoreTokens())
            w1.addElement(StrTok.nextToken());
        
        StrTok = new StringTokenizer(word2," ,-_");
        while(StrTok.hasMoreTokens())
            w2.addElement(StrTok.nextToken());
        double grade = 0;
        if(w1.size() > w2.size())
        {
            for(int i = 0; i < w2.size(); i++)
            {
                if(contains(w2.elementAt(i), w1))
                    grade++;
            }
            grade /= w1.size();
        }
        else
        {
            for(int i = 0; i < w1.size(); i++)
            {
                if(contains(w1.elementAt(i), w2))
                    grade++;
            }
            grade /= w2.size();
        }
        return grade;
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
    
    public boolean contains(String word, Vector<String> list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            if(SameOrEqual(word, list.elementAt(i)))
                return true;
        }
        return false;
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
