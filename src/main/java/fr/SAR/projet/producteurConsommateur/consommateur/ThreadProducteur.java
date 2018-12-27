package fr.SAR.projet.producteurConsommateur.consommateur;

import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.message.Message;
import fr.SAR.projet.message.ToSend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ThreadProducteur extends Thread{




    Socket se;
    String nom;
    BufferedReader in;
    PrintWriter out;



    public ThreadProducteur(Socket socket,String name){
        se = socket;
        start();
    }






    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(se.getInputStream()));
            out= new PrintWriter(se.getOutputStream(),true);
            String req;
            while(true){
                req = in.readLine();
                if(req.equals("bye")) break;
            }
        }catch (IOException e){
            System.err.println("Erreur : " +e);
        }
        finally{
            try{
                se.close(); in.close(); out.close();
            }
            catch (IOException e){}
        }
    }
}
