/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mains;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.html.HTML;
import org.htmlparser.parserapplications.StringExtractor;

/**
 *
 * @author banacer
 */
public class test {
    public static void main(String[] args) throws IOException
    {
        Brain b = new Brain();
        Brain.launchExternalParsing();
        //Brain.sendToAnalyzer("hello my name is nacer", "GOOG");
    }
    
}


/*
 * 
 * BufferedWriter bw = null;
        try {
            Brain b = new Brain();
            String article = b.extractArticle("C:\\Users\\banacer\\Dropbox\\caps\\Source code\\Rss(java)\\Depository\\Output\\219.txt");
            System.out.println(article.length());
            System.out.println(article);
            System.out.println("bye");
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("test.txt")));
            bw.write(article, 0, article.length());
            bw.flush();
            bw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
 */