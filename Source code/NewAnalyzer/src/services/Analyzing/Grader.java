/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package services.Analyzing;

import data.dto.Keyword;
import data.dto.Stock;
import java.util.Vector;

/**
 *
 * @author banacer
 */
public class Grader {
    private Vector<Keyword> List;
    private Stock stock;
    
    public Grader(Vector<Keyword> list, Stock stock)
    {
        this.List = list;
        this.stock = stock;
    }
    
    public double grade()
    {
        double change = 0;
        for(int i = 0; i < this.List.size(); i++)
        {
            change += this.List.elementAt(i).getGrade();
        }
        change /= this.List.size();
        return change;
    }
}
