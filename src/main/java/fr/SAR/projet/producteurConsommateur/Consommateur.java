package fr.SAR.projet.producteurConsommateur;


import java.util.*;
import java.io.*;
import java.io.IOException;
import java.net.Socket;
import fr.SAR.projet.Context;
import fr.SAR.projet.serveurclient.Serveur;
import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.message.Message;
import fr.SAR.projet.message.ToSend;

import static java.lang.Thread.sleep;


public class Consommateur {
    //TODO: à voir si on a besoin de la garder
    ArrayList<ObjectOutputStream> producteurs=new ArrayList<>();

    /**
     * Vector of messages
     */
    Message[] T;
    /**
     * size of vector
     */
    int N;
    /**
     * insertion and extraction index
     */
    int inc=0;
    int outc=0;
    /**
     * number of messages in the vector
     */
    int NbMess=0;
    /**
     * the messages the consumer has to consume in the meantime
     */
    int NbCell=0;


    /**
     * unidirectional ring
     */
    public ObjectOutputStream outOSuccesseur;
    public ObjectInputStream inOpredecesseur;

    /**
     * synchronization
     */
    final public Object monitorInc=new Object();
    final public  Object monitorNbMess= new Object();
    final public  Object monitorJeton=new Object();
    final public Object monitorNbCell=new Object();



    private void setSuccesseur(ObjectOutputStream outSuccessor) {
        outOSuccesseur = outSuccessor;
    }

    private void setPredecesseur(ObjectInputStream inPredecessor) {
        inOpredecesseur = inPredecessor;
    }

    /**
     * defines the successor and predecessor for the token
     * @param successor
     * @param predecessor
     */


    public void setJetonContext(ObjectOutputStream successor,ObjectInputStream predecessor){
        this.setSuccesseur(successor);
        this.setPredecesseur(predecessor);
    }


    public Consommateur(int N){
        try {
            this.N = N;
            T = new Message[N];
        }catch(Exception e){
            System.out.println("Error consumer");
            e.printStackTrace();
        }
    }



    /**
     * when receiving a message
     * @param toSend
     * @return
     */


    public  boolean Sur_Reception_De(ToSend toSend){
        try {
            if (toSend instanceof Message) {
                synchronized (monitorInc) {
                    synchronized (monitorNbMess) {
                        T[inc] = ((Message) toSend);
                        //System.out.println(T[inc].getMessage());
                        inc = (inc + 1) % N;
                        this.NbMess++;
                    }
                }
            }
            if (toSend instanceof Jeton) {
                synchronized (monitorJeton) {
                    synchronized (monitorNbCell) {
                        Jeton jeton = (Jeton) toSend;
                        jeton.setVal(jeton.getVal() + this.NbCell);
                        this.NbCell=0;
                        envoyer_a(outOSuccesseur, jeton);
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }



    public  void consommer(){
        int a;
        if (this.NbMess > 0) {
                synchronized (monitorNbMess) {
                    synchronized (monitorNbCell) {
                        System.out.println("Je consomme le message:  ");
                        System.out.println(T[outc].getMessage());
                        if(T[outc].equals("Consommer please")){
                            //TODO: relancer l'election
                        }
                        T[outc]=null; //supprimer le message
                        outc = (outc + 1) % N;
                        this.NbMess--;
                        this.NbCell++;
                    }
                }
            }
    }



    public Runnable callSRDJeton(){
        return new Runnable() {
            @Override
            public void run() {
                try {
                    while(true){
                        Object object = inOpredecesseur.readObject();
                        if(object!=null){
                            if(object instanceof Jeton) {
                                Jeton jeton = (Jeton) object;
                                Sur_Reception_De(jeton);
                            }
                        }
                        sleep(1000);
                    }
                }catch (Exception e){
                    System.err.println(e);
                }
            }
        };
    }

    public  Runnable callConsommer(){
        return new Runnable() {
            @Override
            public void run() {
                while(true) {
                    consommer();
                    try {
                        sleep(1000);
                    }catch(Exception e){
                        e.printStackTrace();
                        System.out.println("probleme consommateur");
                    }
                }
            }
        };
    }

    public void envoyer_a(ObjectOutputStream outOSuccesseur, Jeton content){
        try {
            outOSuccesseur.writeObject(content);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean readyNeighbors(){
        if(outOSuccesseur== null || inOpredecesseur == null) return false;
        return true;
    }

    public void initialize_Consumer(int id){ //Etablie les connexions entre le conso et chaque producteur
        try {
            if(!readyNeighbors()){
                System.err.println("*** You can't You need to define your neighbors ***");
                return;
            }

           Serveur serveur = new Serveur(Context.getAddress(id), Context.getportConsumer());
           Jeton jeton=new Jeton(N);
           envoyer_a(outOSuccesseur,jeton);
           Thread threadJeton=new Thread(callSRDJeton());
           threadJeton.start();
           Thread consumer=new Thread(callConsommer());
           consumer.start();


           for (int i = 0; i < Context.getContext().length; i++) {
               if(i==id) continue;
               Socket soc = serveur.ajoutClient();
               ThreadProducteur threadProducteur=new ThreadProducteur(soc,"producteur"+i,this);
               threadProducteur.start();

            }
           System.out.println("Well Done; all connection etablished");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
