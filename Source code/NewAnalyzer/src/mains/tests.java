/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mains;

import data.dto.DBData;
import java.io.*;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import services.Learning.Learner;

/**
 *
 * @author banacer
 */
public class tests {
    public static void main(String[] args)
    {
        try {
            Learner l = new Learner();
            DBData[] d = l.isMeaningfulWord("mobile");
            for(int i = 0; i < d.length; i++)
                System.out.println(d[i].getContent());
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(tests.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void searchList(String content)
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
        
        for(int i = 0; i < result.length; i++)
            System.out.println(result[i]);
        
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
