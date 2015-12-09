/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.dto;

import java.net.URL;

/**
 *
 * @author banacer
 */
public class Article {
    private int id;
    private Source source;
    private URL link;
    
    public Article()
    {
        this.id = 0;
        this.source = new Source();
        this.link = null;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the source
     */
    public Source getSource() {
        return source;
    }

    /**
     * @return the link
     */
    public URL getLink() {
        return link;
    }
}
