/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.dto;

/**
 *
 * @author banacer
 */
public class DBData {
    private int id;
    private String content;
    private String alias;
    private boolean word;
    
    public DBData(int id,String content, boolean word)
    {
        this.id = id;
        this.content = content;
        this.alias = null;
        this.word = word;
    }
    
    public DBData(int id,String content,String alias, boolean word)
    {
        this.id = id;
        this.content = content;
        this.alias = alias;
        this.word = word;
    }
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @return the word
     */
    public boolean isWord() {
        return word;
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }
}
