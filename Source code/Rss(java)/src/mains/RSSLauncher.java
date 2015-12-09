/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mains;

import data.DBService;
import data.dto.Article;
import data.dto.Stock;
import java.io.IOException;
import java.net.*;
import multithreading.RSSThread;

/**
 *
 * @author banacer
 */
public class RSSLauncher 
{
    private final static String url = "http://www.google.com/finance/company_news?q=NASDAQ:#&output=rss";
    //"http://feeds.finance.yahoo.com/rss/2.0/headline?s=#&region=US&lang=en-US";
    
    public static void main(String[] args)
    {
        int minutes = 5;
        //CONTINUOUS READING OF INFORMATION FROM THE NET.
        while(true)
        {
            try {
                readFeed();
                Thread.sleep(minutes * 60 * 1000);
            } catch (InterruptedException ex) {
                
            }
        }
    }
    
    public static void readFeed()
    {
        int numThreads = 0;
        DBService service = new DBService();
        Stock[] stock = service.getStockList();
        RSSThread[] tList = new RSSThread[5];
        int index = 0;
        for(int i = 0; i < stock.length; i++)
        {
            try {
                if(index == 5)
                    index = 0;
                //CREATE THE RSS LINK
                String link = url.replace("#", stock[i].getTicker());
                //RUN THE THREAD TO BRING INFO
                
                if(LinkExist(link))
                {
                    while(tList[index] != null && !tList[index].isFinished())
                        Thread.sleep(1000); //SLEEP FOR 1 SECOND(S) WAITING FOR JOB TO FINISH                    
                    tList[index] = new RSSThread(stock[i]);
                    tList[index].start();
                    numThreads++;
                    index++;
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            
        }
    }
    
    public static boolean LinkExist(String url)
    {
        try {
            URL u = new URL (url);             
            HttpURLConnection huc =  (HttpURLConnection) u.openConnection (); 
            huc.setRequestMethod ("GET"); 
            huc.connect();                         
            int code = huc.getResponseCode();
            //System.out.println(url+"  code: "+code);
            if(code == 200)
                return true;
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static void DownloadAndExtract(int id)
    {
        DBService service = new DBService();
        Article article = service.getArticle(id);
        
    }
    
    
}
    
