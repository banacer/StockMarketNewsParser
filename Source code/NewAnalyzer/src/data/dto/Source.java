/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.dto;

import java.net.*;

/**
 *
 * @author banacer
 */
public class Source {
    private int id;
    private URL link;
    private String name;
    private double credibility;
    
    public Source()
    {
        this.id = 0;
        this.link = null;
        this.name = null;
        this.credibility = 0;
    }
    
    public Source(int id)
    {
        this.id = id;
        this.link = null;
        this.name = null;
        this.credibility = 0;
    }
    
    public Source(int id, String link,String name,double credibility) throws MalformedURLException
    {
        this.id = id;
        this.link = new URL(link);
        this.name = name;
        this.credibility = credibility;
    }
    
    
}
