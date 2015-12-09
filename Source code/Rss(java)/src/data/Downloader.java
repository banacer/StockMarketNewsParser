/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import data.dto.Article;
import java.io.*;
import java.net.*;

/**
 *
 * @author banacer
 */
public class Downloader {
    private Article article;
    private File file;
    private String abspath;
    public Downloader()
    {
        java.io.File file = new java.io.File("");   //Dummy file
        this.abspath = file.getAbsolutePath();
        
        this.article = null;
        this.file = null;        
    }
    
    public Downloader(Article article)
    {
        java.io.File file = new java.io.File("");   //Dummy file
        this.abspath = file.getAbsolutePath();
        
        this.article = article;
        if(isDownloaded())
        {
            this.file = new File(abspath+"\\Depository\\Pages\\"+this.article.getId()+".html");            
        }
        else
        {
            download();
            this.article.setDownloaded(true);
            //Set to downloaded in the DB
            (new DBService()).setArticleDownloaded(this.article.getId());
        }
    }
    /**
     * @return the article
     */
    public Article getArticle() {
        return article;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @return the downloaded
     */
    public boolean isDownloaded() {
        return article.isDownloaded();
    }
    
    private boolean download()
    {
        URL url;
        InputStream is = null;
        DataInputStream dis;
        String line;
        String data = "";

        try 
        {
            url = new URL(article.getUrl());
            //ADD IF LINK EXISTS FUNCTION
            is = url.openStream();  // throws an IOException
            dis = new DataInputStream(new BufferedInputStream(is));            
            while ((line = dis.readLine()) != null) {
                data += line+"\n";
            }
            //AT THIS POINT FILE HAVE BEEN DOWNLOADED
            //IT SHOULD BE SAVED ON HARD DISK
            this.file = new File(abspath+"\\Depository\\Pages\\"+this.article.getId()+".html");
            System.out.println(file.getAbsolutePath());
            this.file.createNewFile();
            OutputStreamWriter writer = new FileWriter(file);
            writer.write(data, 0, data.length());
            writer.flush();
            writer.close();
            return true;
        } 
        catch (Exception mue) 
        {
            mue.printStackTrace();
            return false;
        }
    }
}
