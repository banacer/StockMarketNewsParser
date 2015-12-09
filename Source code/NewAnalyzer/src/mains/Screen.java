/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mains;

import java.net.*;
import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author banacer
 */
public class Screen {
    public static void main(String[] args)
    {
        try {
            ServerSocket server = new ServerSocket(55565);
            Socket socket = null;
            BufferedReader br = null;
            String ticker = null;
            double change = 0;
            StringTokenizer tok = null;
            System.out.println("Ticker\t\t\tExpected Change (%)");
            while(true)
            {
                socket = server.accept();
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = br.readLine();
                tok = new StringTokenizer(message, "#");
                ticker = tok.nextToken();
                change = Double.parseDouble(tok.nextToken());
                System.out.println(ticker+"\t\t\t"+change);
            }
        } catch (IOException ex) {
            Logger.getLogger(Screen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
