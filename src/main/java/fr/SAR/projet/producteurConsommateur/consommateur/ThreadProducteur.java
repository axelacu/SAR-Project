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
    Consommateur consommateur;

    public ThreadProducteur(Socket socket,String name,Consommateur consommateur){
        try {
            se = socket;
            this.name = name;
            this.consommateur=consommateur;
            in = new ObjectInputStream(se.getInputStream());
            out = new ObjectOutputStream(se.getOutputStream());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            Thread consumer=new Thread(consommateur.callConsommer());
            consumer.start();
            boolean b=true;
            while (b){ //Ici,  sur cette socket il ne peut recevoir que les messages
                ToSend message=(ToSend)in.readObject();
                b=consommateur.Sur_Reception_De(message);
            }
            System.out.println("Probleme SRD; la socket va se deconnecter");
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
