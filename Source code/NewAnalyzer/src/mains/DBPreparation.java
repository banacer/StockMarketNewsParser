package mains;

import data.dal.SchemaTransfer;
import data.dal.WordServices;
import java.io.*;

public class DBPreparation {
    
    public static void main(String[] args)
    {
        //OLD METHOD
        /*
        System.out.println("Step 1");
        moveToDB();
        System.out.println("Step 2");
        assignToGroup();
        */
        //NEW METHOD
        
        SchemaTransfer t = new SchemaTransfer();
        /*System.out.println("Word transfet started");
        boolean b = t.MoveToDB();        
        System.out.println(b);
        boolean c = t.BlockWords();
        System.out.println(c);*/
        boolean d = t.AddStocks();
        System.out.println(d);
        System.out.println("Job done");
    }
    
    public static void moveToDB()
    {
        try {
            WordServices wservices = new WordServices();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("english_list.txt")));
            String word = null;
            while((word = br.readLine()) != null)
            {
                wservices.addWord(word);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
    }
    public static void assignToGroup()
    {
        WordServices service = new WordServices();        
        
        for(int i = 1; i <= 58113; i++)
        {
            
            String word = service.getWord(i);
            
            //System.out.println(word);
            if(word != null)
            {
                //System.out.println("index  "+i);
                i = service.createGroup(word);
                //System.out.println("index1  "+i);
            }
            
            
        }
    }
}
