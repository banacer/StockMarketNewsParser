/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.dto;

import java.sql.Timestamp;

/**
 *
 * @author banacer
 */
public class Article {
    private int id;
    private String url;
    private Stock stock;
    private boolean downloaded;
    private boolean analyzed;
    private Timestamp date;
    
    
    public Article()
    {
        this.id = 0;
        this.url = null;
        this.downloaded = this.analyzed = false;
        this.date = null;
    }
    
    public Article(int id,String url,int stockId,boolean downloaded, boolean analyzed,Timestamp date)
    {
        this.id = id;
        this.url = url;
        this.stock = new Stock(stockId);
        this.downloaded = downloaded;
        this.analyzed = analyzed;
        this.date = date;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the downloaded
     */
    public boolean isDownloaded() {
        return downloaded;
    }

    /**
     * @return the analyzed
     */
    public boolean isAnalyzed() {
        return analyzed;
    }

    /**
     * @return the date
     */
    public Timestamp getDate() {
        return date;
    }

    /**
     * @param downloaded the downloaded to set
     */
    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    /**
     * @param analyzed the analyzed to set
     */
    public void setAnalyzed(boolean analyzed) {
        this.analyzed = analyzed;
    }

    /**
     * @return the stock
     */
    public Stock getStock() {
        return stock;
    }
}
