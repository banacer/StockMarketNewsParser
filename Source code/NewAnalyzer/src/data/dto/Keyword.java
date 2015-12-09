/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.dto;

import java.sql.*;
import data.dal.MySQLConnection;

/**
 *
 * @author banacer
 */
public class Keyword {
    protected int id;
    protected double grade;
    protected double num;
    protected double sd;
    protected int group;
    protected boolean isWord;
    public Keyword()
    {
        this.id = 0;
        this.grade = 0;
        this.num = 0;
        this.sd = 0;
        this.group = 0;
    }
    
    public Keyword(int id,double grade, double num, double sd,int group)
    {
        this.id = id;
        this.grade = grade;
        this.num = num;
        this.sd = sd;
        this.group = group;
    }
    public Keyword(int id,double grade, double num, double sd)
    {
        this.id = id;
        this.grade = grade;
        this.num = num;
        this.sd = sd;
        this.group = group;
    }
    public Keyword(int id)
    {
        this.id = id;
        
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the grade
     */
    public double getGrade() {
        return grade;
    }

    /**
     * @return the num
     */
    public double getNum() {
        return num;
    }

    /**
     * @return the sd
     */
    public double getSd() {
        return sd;
    }

    /**
     * @return the group
     */
    public int getGroup() {
        return group;
    }

    /**
     * @return the isWord
     */
    public boolean isIsWord() {
        return isWord;
    }
}
