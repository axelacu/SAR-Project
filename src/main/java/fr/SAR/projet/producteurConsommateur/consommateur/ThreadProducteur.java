package fr.SAR.projet.producteurConsommateur.consommateur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ThreadProducteur extends Thread{
    public Consommateur serv;
    public Socket se;
    String nom;
    BufferedReader in;
    PrintWriter out;
    public ThreadProducteur(Socket socket, Consommateur consommateur){
        se = socket;
        serv = consommateur;
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
                if(req.equals("Bye")) break;
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
    public void Envoyer(String s){
        out.println(s);
        out.flush();
    }
}
