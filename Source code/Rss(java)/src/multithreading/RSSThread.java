/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multithreading;

import data.DBService;
import data.dto.Stock;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import rss.*;

/**
 *
 * @author banacer
 */
public class RSSThread extends Thread{
    
    private final static String url = "http://www.google.com/finance/company_news?q=NASDAQ:#&output=rss";
    //"http://feeds.finance.yahoo.com/rss/2.0/headline?s=#&region=US&lang=en-US";
    private Stock stock;    
    private DBService service;
    private boolean finished;
    
    public RSSThread(Stock stock)
    {
        this.stock = stock;
        this.service = new DBService();
        this.finished = false;
    }
    
    public void run()
    {
        try
        {
        String link = url.replace("#", stock.getTicker());        
        RSSFeedParser parser = new RSSFeedParser(link);
        Feed feed = parser.readFeed();        
        if(feed == null)
        {            
            this.finished = true;
            return;
        }
	for (FeedMessage message : feed.getMessages())
        {            
            //IMPORTANT!!!!!!! Add the fact that it appeared in the last 48 hours
            Timestamp date = service.StringToDate(message.getPubDate());            
            if(!service.exist(message.getLink()) && onTimeInterval(date))
            {
                //STORE THE ARTICLE IN THE DB
                service.addArticle(message.getLink(),stock.getId(),date);
                System.out.println("Link saved!!");
            }
        }
        this.finished = true;
        }
        catch(Exception ex)
        {
            this.finished = true;
        }
    }
    
    public boolean onTimeInterval(Timestamp date)
    {        
        Calendar c1 = new GregorianCalendar();
        c1.add(Calendar.HOUR, -2);        
        Calendar c2 = new GregorianCalendar();
        Calendar x = new GregorianCalendar();        
        x.setTime(date);        
        
        if(c1.compareTo(x) < 0 && x.compareTo(c2) < 0)
            return true;
        return false;
    }

    /**
     * @return the finished
     */
    public boolean isFinished() {
        return finished;
    }
    
    
}
