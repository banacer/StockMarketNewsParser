/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.dal;


import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author banacer
 */
public class SchemaTransfer {
    
    private String word;
    private String pos;
    private int group;
    private Vector<String> related;
    
    public boolean isCorrectFormat(String str)
    {
        try
        {
            StringTokenizer s = new StringTokenizer(str, ":");
            String w1 = s.nextToken();
            String w2 = s.nextToken();
            s = new StringTokenizer(w1);
            this.word = s.nextToken();
            System.out.println("holaa:  "+this.word);
            this.pos = s.nextToken();
            if(pos.charAt(pos.length() - 1) == '?')
            pos = pos.substring(0, pos.length() - 1);
            return isInflectedForms(w2);            
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean isInflectedForms(String str)
    {
        StringTokenizer tok = new StringTokenizer(str, "|");
        while(tok.hasMoreTokens())
        {
            String myToken = tok.nextToken();
            if(myToken.charAt(myToken.length() - 1) == ' ')
                myToken = myToken.substring(0, myToken.length()-1);
            if(myToken.charAt(0) == ' ')
                myToken = myToken.substring(1, myToken.length());
            
            isInflectedForm(myToken);
        }
        return true;        
    }
    public boolean isInflectedForm(String str)
    {
        StringTokenizer tok = new StringTokenizer(str,",");
        while(tok.hasMoreTokens())
        {
            String myToken = tok.nextToken();
            if(myToken.charAt(myToken.length() - 1) == ' ')
                myToken = myToken.substring(0, myToken.length()-1);
            if(myToken.charAt(0) == ' ')
                myToken = myToken.substring(1, myToken.length());
            
            isIndividualEntry(myToken);            
        }
        return true;
    }
    public boolean isIndividualEntry(String str)
    {
        try
        {
            StringTokenizer tok = new StringTokenizer(str, "~<!?");
            this.related.add(tok.nextToken());
            return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean MoveToDB()
    {
        try {
            WordServices wservices = new WordServices();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("infl.txt")));
            String word = null;
            while((word = br.readLine()) != null)
            {
                this.word = "";
                this.pos = "";
                this.related = new Vector<String>();
                if(isCorrectFormat(word))
                {
                    this.group = wservices.insertGroup();
                    int source = wservices.insertWord(this.word, pos, group);
                    wservices.addSourcetoGroup(group, source);
                    for(int i = 0; i < this.related.size(); i++)
                    {
                        wservices.insertWord(this.related.elementAt(i), pos, group);
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }        
    }
    
    public boolean BlockWords()
    {
        WordServices s = new WordServices();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream("special_words.txt")));
            String word = null;
            while((word = br.readLine()) != null)
            {
                System.out.println(word);
                s.blockWord(word);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean AddStocks()
    {
        WordServices s = new WordServices();
        
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream("NASDAQ.txt")));
            String word = null;
            while((word = br.readLine()) != null)
            {                
                String[] info = word.split("\\t");
                s.addStock(info[1], "");
                s.addAlias(info[1], info[0]);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        
    }
}
