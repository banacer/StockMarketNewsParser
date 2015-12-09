/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mains;

import data.DBService;
import data.Downloader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author banacer
 */
public class Brain {
    private static String abspath;
    private boolean first = true;
    public synchronized static void launchExternalParsing()
    {
        java.io.File file = new java.io.File("");   //Dummy file
        abspath = file.getAbsolutePath();
        DBService s = new DBService();
        Integer[] artList = s.getToBeAnalyzedList();
        Downloader downloader = null;
        Runtime r = Runtime.getRuntime();
        for(int i = 0; i < artList.length; i++)
        {
            try {
                downloader = new Downloader(s.getArticle(artList[i]));
                String inputPath = downloader.getFile().getAbsolutePath();
                String outputPath = abspath+"\\Depository\\Output\\"+downloader.getArticle().getId()+".txt";
                //PREPARES THE CONFIG FILE
                modifyConfigFile(inputPath, outputPath);
                
                //EXECUTES CURRENT PROGRAM
                Process process = r.exec("cmd /c start c:/x.bat");    
                System.out.println();
                //WAIT FOR EXTERNAL PROCESS TO FINISH EXECUTION
                process.waitFor();
                //DESTROYS THE EXTERNAL PROCESS
                process.destroy();
                Thread.sleep(500);
                //EXTRACTING ARTICLE FROM TEXT FILE
                String article = extractArticle(outputPath);
                //REMOVE NEWLINES
                article = removeNewLine(article);                
                //SHOULD BE SENT TO ANALYZER
                sendToAnalyzer(article, downloader.getArticle().getStock().getTicker());
            } 
            catch (Exception ex)
            {                
            }            
        }
    }
    
    public static void main(String[] args)
    {
        while(true)
        {
            launchExternalParsing();
        }
    }
    
    public static boolean modifyConfigFile(String input,String output)
    {
        BufferedReader br = null;
        try 
        {
            File file = new File("config.cfg");
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            String config = "";
            int count = 1;
            while ((line = br.readLine()) != null) 
            {
                if(count == 4 || count == 5)
                {
                    if(count == 4)
                    {
                        config += "Source="+input;
                    }
                    if(count == 5)
                    {
                        config +="Dest="+output;
                    }
                }
                else
                    config += line;
                config += "\n";
                count++;
            }
            br.close();
            //COPY THE NEW CONFIGURATION
            
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            bw.write(config,0, config.length());
            bw.close();
            return true;
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
            return false;
        }            
    }
    
    public static synchronized String extractArticle(String path)
    {
        BufferedReader br = null;
        try {
            File file = new File(path);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            String article = "";
            String part = "";
            int count = 1;
            while ((line = readLine(br)) != null)
            {
                
                //THIS CONDITION MEANS THAT THE LINE IS NOT EMPTY OR CONTAINS ONLY THE NEWLINE COMMAND
                if(line.length() > 0 && !(line.length() == 1))
                {
                    part += line;
                    count++;
                }
                else
                {
                    
                    if(count > 8)
                    {
                        //System.out.println("yes::\n"+part);
                        article += part;
                        part = "";
                        count = 1;
                    }
                }
            }
            return article;
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
            return null;
        }            
    }
    private static String readLine(BufferedReader br) throws IOException
    {
        String result = "";        
        int charac = 0;
        while((charac = br.read()) != 10)
        {
            if(charac == -1)
                break;
            result += (char) charac;
            
        }
        if(charac == -1)
        {
            if(result.compareTo("") == 0)
                return null;            
        }
        return result;
        
    }
    
    public static synchronized boolean sendToAnalyzer(String article,String ticker)
    {
        try 
        {
            //CONNECT TO SERVER
            Socket s = new Socket("localhost", 5565);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            
            //SEND ARTICLE
            article += '\n';
            bw.write(article, 0, article.length());
            bw.flush();
            String message = "salamlabasbanacer\n";
            bw.write(message,0,message.length());
            bw.flush();
            //RECIEVE YES\n MESSAGE FROM SERVER
            String response = br.readLine();
            if(response.equalsIgnoreCase("YES"))
            {
                //SEND TOCKER NOW
                ticker += '\n';
                bw.write(ticker, 0, ticker.length());
                bw.flush();
                //END OF TASK
                return true;
            }
            else
                return false;
            
            
        } 
        catch (UnknownHostException ex) 
        {
            ex.printStackTrace();
            return false;
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static synchronized String removeNewLine(String article)
    {
        String result = "";
        for(int i = 0; i < article.length(); i++)
        {
            if(article.charAt(i) != '\n')
                result +=article.charAt(i);
        }
        return result;
    }
}
