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
    ObjectInputStream inOpredecesseur;

    public ThreadProducteur(Socket socket,String name,Consommateur consommateur,ObjectInputStream inOpredecesseur){
        try {
            se = socket;
            this.name = name;
            this.consommateur=consommateur;
            in = new ObjectInputStream(se.getInputStream());
            out = new ObjectOutputStream(se.getOutputStream());
            this.inOpredecesseur=inOpredecesseur;
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public Runnable receiveMessage(){
        return new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        Message message = (Message) inOpredecesseur.readObject();
                        consommateur.Sur_Reception_De(message);
                    }
                } catch(Exception e){
                    System.out.println("Erreur dans l'attente d'un message");
                    e.printStackTrace();
                }
            }
        };
    }


    public Runnable receiveJeton(){
        return new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Jeton jeton = (Jeton) in.readObject();
                        consommateur.Sur_Reception_De(jeton);
                    }
                } catch(Exception e){
                    System.out.println("Erreur dans l'attente du jeton");
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void run() {
        try {
            Thread consumer=new Thread(consommateur.callConsommer());
            consumer.start();

            Thread srdJeton = new Thread(receiveJeton());
            srdJeton.start();

            Thread srdMessage=new Thread(receiveMessage());
            srdMessage.start();


            Thread.currentThread().notifyAll();

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
