/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.dto;

/**
 *
 * @author banacer
 */
public class Text {
    private Article article;
    private String text;
    private double change;
    private boolean learning;
    
    public Text(Article article, String text)
    {
        this.article = article;
        this.text = text;
        this.change = 0;
        this.learning = false;
    }
    
    public Text(Article article, String text,double change)
    {
        this.article = article;
        this.text = text;
        this.change = change;
        this.learning = true;
    }

    /**
     * @return the article
     */
    public Article getArticle() {
        return article;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @return the change
     */
    public double getChange() {
        return change;
    }

    /**
     * @return the learning
     */
    public boolean isLearning() {
        return learning;
    }
}
