/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mains;

import data.dal.WordServices;
import data.dto.Article;
import data.dto.Keyword;
import data.dto.Stock;
import data.dto.Text;
import finance.*;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import services.Learning.Learner;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Vector;
import multithreading.LearnerThread;
import multithreading.WikiStaticCollection;
import services.Analyzing.Grader;
/**
 *
 * @author banacer
 */
public class Execution extends Thread{
    public static Vector<String> Deposit = new Vector<String>();
    public static Vector<Stock> stockList = new Vector<Stock>();
    
    public static void analyzer()
    {
        try 
        {
            //INITIALIZE THE WIKIS
            new WikiStaticCollection();
            //SHOULD BE DONE ALL TIME
            while(true)
            {
                //GET ARTICLE FROM DEPOSIT
                String text = null;
                if(!Deposit.isEmpty() && !stockList.isEmpty())
                {
                    //GET ARTICLE
                    text = Deposit.elementAt(0);
                    Deposit.remove(0);
                    Text article = new Text(new Article(), text);
                    //GET THE STOCK
                    Stock myStock = stockList.elementAt(0);
                    stockList.remove(0);
                    //DECOMPOSITION OF FILE
                    //GET THE START TIME OF THE ANALYSIS
                    java.util.Date today = new java.util.Date();
                    Timestamp startTime = new Timestamp(today.getTime());
                    
                    //GET THE PRICE BEFORE THE ANALYSIS
                    
                    StockBean bean = StockTickerDAO.getInstance().getStockPrice(myStock.getTicker());
                    double oldPrice = bean.getPrice();
                    Learner learner = new Learner();
                    learner.fullTextDecomposition(article);
                    
                    //THIS IS THE RESULT OF THE DECOMPOSITION THE FOLLOWING LIST IS THE LIST OF KEYWORDS
                    Vector<Keyword> KeyList = learner.kList;
                    //SHOULD BE GRADED AS WELL AS LEARNED FROM IT IN SEPARATE THREADS
                    //LAUNCH GRADER
                    Grader grader = new Grader(KeyList, myStock);
                    double expectedChange = grader.grade();
                    //SEND RESULT
                    sendDisplayData(myStock.getTicker(), expectedChange);                    
                    //LAUNCH LEARNER
                    LearnerThread LT = new LearnerThread(KeyList, myStock, startTime, oldPrice);
                    LT.start();
                }
                else
                {   
                    Thread.sleep(30*1000); //SLEEP FOR 30 SECONDS WAITING FOR NEW ARTICLES
                    
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }
    
    public void fetchArticle()
    {
        try 
        {
            WordServices service = new WordServices();
            ServerSocket server = new ServerSocket(5565);
            Socket s = null;
            BufferedReader br = null;
            BufferedWriter bw = null;
            while(true)
            {
                    s = server.accept();
                    br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    String article = "";
                    String line = null;
                    while((line = br.readLine()) != null)
                    {
                        if(!line.equalsIgnoreCase("salamlabasbanacer"))
                            article += line;
                        else
                            break;
                    }                    
                    //RECIEVED AND NOW INSERTED INTO DEPOSIT                    
                    Deposit.addElement(article);
                    //SEND CONFIRMATION MESSAGE
                    String message = "YES\n";
                    bw.write(message, 0, message.length());
                    bw.flush();
                    String ticker = br.readLine();
                    
                    //RECIEVED AND NOW INSERT INTO STOCKLIST
                    stockList.addElement(service.getStock(ticker));                        
            }
        } 
        catch (IOException ex) 
        {
                ex.printStackTrace();
        }
    }
    
    public void run()
    {
        System.out.println("Fetcher Started.");
        fetchArticle();        
    }
    
    public static void main(String[] args)
    {
        Execution exe = new Execution();
        exe.start();
        Execution.analyzer();
    }
    public static void sendDisplayData(String ticker,double change)
    {
        try 
        {
            Socket s = new Socket("localhost", 55565);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            String message = ticker+"#"+change+"\n";
            bw.write(message,0, message.length());
            bw.close();
            s.close();
        }
        catch (Exception ex) 
        {
            
        }
        
    }
            
}
