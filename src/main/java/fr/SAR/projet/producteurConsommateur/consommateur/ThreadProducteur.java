package fr.SAR.projet.producteurConsommateur.consommateur;

import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.message.Message;
import fr.SAR.projet.message.ToSend;

import java.io.*;
import java.net.Socket;

public class ThreadProducteur extends Thread{

    Socket se;
    String name;
    ObjectInputStream in;
    ObjectOutputStream out;

    public ThreadProducteur(Socket socket,String name){
        se = socket;
        this.name=name;
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(se.getInputStream());
            out= new ObjectOutputStream(se.getOutputStream());
            //Consommateur.callConsommer().run();
            while (true){
                //Consommateur.callSRD(new Message((String)in.readObject())).run();
            }
        }
        catch (Exception e){
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
