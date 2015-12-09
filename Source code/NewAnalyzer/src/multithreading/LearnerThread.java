/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multithreading;

import data.dto.Group;
import data.dto.Keyword;
import data.dto.Stock;
import finance.StockBean;
import finance.StockTickerDAO;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.FailedLoginException;
import services.Learning.Learner;

/**
 *
 * @author banacer
 */
public class LearnerThread extends Thread{
    
    private Timestamp startTime;
    private Vector<Keyword> List;
    private Stock stock;
    private double oldPrice;
    
    public LearnerThread(Vector<Keyword> list,Stock stock,Timestamp startTime,double oldPrice)
    {
        this.List = list;
        this.stock = stock;
        this.startTime = startTime;
        this.oldPrice = oldPrice;
    }
    
    public void run()
    {
        try {
            Learner learner = new Learner();
            Calendar x = new GregorianCalendar();        
            x.setTime(startTime);
            x.add(Calendar.HOUR, 2);
            Date d = x.getTime();
            Timestamp diff = diff(new Date(),d);
            Thread.sleep(diff.getTime()); // SLEEP WAITING FOR 20 MINUTES TO PASS
            StockBean  stock = StockTickerDAO.getInstance().getStockPrice(this.stock.getTicker());
            double change = (stock.getPrice() - oldPrice) / oldPrice;
            change *= 100;
            //CHANGE IS IN PERCENTAGE
            
            //UPDATE GRADE IN DB
            
            for(int i = 0; i < this.List.size(); i++)
            {
                learner.gradeKeyword(this.List.elementAt(i), change);
                if(this.List.elementAt(i).isIsWord())
                    learner.gradeGroup(new Group(this.List.elementAt(i).getGroup()), change);                
            }
            //WORD DONE!!
        } catch (IOException ex) {
            Logger.getLogger(LearnerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FailedLoginException ex) {
            Logger.getLogger(LearnerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public Timestamp diff (java.util.Date t1, java.util.Date t2)
    {
        // Make sure the result is always > 0
        if (t1.compareTo (t2) < 0)
        {
            java.util.Date tmp = t1;
            t1 = t2;
            t2 = tmp;
        }
    
        // Timestamps mix milli and nanoseconds in the API, so we have to separate the two
        long diffSeconds = (t1.getTime () / 1000) - (t2.getTime () / 1000);
        // For normals dates, we have millisecond precision
        int nano1 = ((int) t1.getTime () % 1000) * 1000000;
        // If the parameter is a Timestamp, we have additional precision in nanoseconds
        if (t1 instanceof Timestamp)
            nano1 = ((Timestamp)t1).getNanos ();
        int nano2 = ((int) t2.getTime () % 1000) * 1000000;
        if (t2 instanceof Timestamp)
            nano2 = ((Timestamp)t2).getNanos ();
    
        int diffNanos = nano1 - nano2;
        if (diffNanos < 0)
        {
            // Borrow one second
            diffSeconds --;
            diffNanos += 1000000000;
        }
    
        // mix nanos and millis again
        Timestamp result = new Timestamp ((diffSeconds * 1000) + (diffNanos / 1000000));
        // setNanos() with a value of in the millisecond range doesn't affect the value of the time field
        // while milliseconds in the time field will modify nanos! Damn, this API is a *mess*
        result.setNanos (diffNanos);
        return result;
    }
}
