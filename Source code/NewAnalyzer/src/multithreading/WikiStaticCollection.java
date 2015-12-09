/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multithreading;

import java.io.IOException;
import java.util.*;
import javax.security.auth.login.FailedLoginException;
import org.wikipedia.Wiki;

/**
 *
 * @author banacer
 */
public class WikiStaticCollection extends Thread{
    
    private static int maxObjects = 5;
    private static Vector<Wiki> wlist;
    private static Vector<Boolean> uList;
    private static final String login = "Newanalyzer";
    private static final char[] password = "mynews".toCharArray();
    public WikiStaticCollection() throws IOException, FailedLoginException
    {
        if(wlist == null)
        {
            wlist = new Vector<Wiki>();
            uList = new Vector<Boolean>();
            
            for(int i = 0;i < maxObjects; i++)
            {
                Wiki wiki = new Wiki();
                wiki = new Wiki("en.wikipedia.org");
                wiki.setThrottle(5000); // set the edit throttle to 0.2 Hz
                wiki.login(login+""+i, password); // log in as user
                
                wlist.add(wiki);
                uList.add(Boolean.FALSE);
            }
        }
    }
    
    public static Wiki getWiki()
    {
        for(int i = 0; i < maxObjects; i++)
        {
            if(!uList.elementAt(i).booleanValue())
            {
                uList.setElementAt(Boolean.TRUE, i);
                return wlist.elementAt(i);
            }
        }
        return null;
    }
    
    public static void returnWiki(Wiki wiki)
    {
        for(int i = 0; i < maxObjects; i++)
        {
            if(wiki == wlist.elementAt(i))
            {
                uList.setElementAt(Boolean.FALSE, i);
                System.out.println("Wiki "+i+" returned successfully" );
            }
        }
    }
    
}
