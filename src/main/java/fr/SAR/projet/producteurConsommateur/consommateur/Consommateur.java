package fr.SAR.projet.producteurConsommateur.consommateur;


import java.util.*;
import java.io.*;
import java.io.IOException;
import java.net.Socket;
import fr.SAR.projet.Test.Context;
import fr.SAR.projet.Test.Serveur;
import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.message.Message;
import fr.SAR.projet.message.ToSend;

import static java.lang.Thread.sleep;


public class Consommateur {
    ArrayList<ObjectOutputStream> producteurs=new ArrayList<>();
    Message[] T;
    int N;
    int inc=0;
    int outc=0;
    int NbMess=0;
    int NbCell=0;



    public ObjectOutputStream outOSuccesseur;
    public ObjectInputStream inOpredecesseur;

    final public Object monitorInc=new Object();
    final public  Object monitorNbMess= new Object();
    final public  Object monitorJeton=new Object();
    final public Object monitorNbCell=new Object();



    private void setSuccesseur(OutputStream outSuccessor) {
        try {
            outOSuccesseur = new ObjectOutputStream(outSuccessor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPredecesseur(InputStream inPredecessor) {
        try {
            inOpredecesseur = new ObjectInputStream(inPredecessor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setJetonContext(OutputStream successeur,InputStream predecesseur){
        this.setSuccesseur(successeur);
        this.setPredecesseur(predecesseur);
    }


    public Consommateur(int N){
        try {
            this.N = N;
            T = new Message[N];

        }catch(Exception e){
            System.out.println("Erreur Consommateur");
            e.printStackTrace();
        }
    }




    public  boolean Sur_Reception_De(ToSend toSend){
        try {
            if (toSend instanceof Message) {
                synchronized (monitorInc) {
                    synchronized (monitorNbMess) {
                        T[inc] = ((Message) toSend);
                        System.out.println(T[inc].getMessage());
                        inc = (inc + 1) % N;
                        this.NbMess++;
                        System.out.println("Le nombre de message a ete augmenter ");
                    }
                }
            }
            if (toSend instanceof Jeton) {
                synchronized (monitorJeton) {
                    synchronized (monitorNbCell) {
                        Jeton jeton = (Jeton) toSend;
                        System.out.println("Avant changement :" + jeton.getVal());
                        jeton.setVal(jeton.getVal() + this.NbCell);
                        this.NbCell=0;
                        System.out.println("Apres changement :" + jeton.getVal());
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
        if (this.NbMess > 0) {
                synchronized (monitorNbMess) {
                    synchronized (monitorNbCell) {
                        System.out.println("Je consomme le message:  ");
                        System.out.println(T[outc].getMessage());
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
                            Jeton jeton = (Jeton) object;
                            System.out.println("**** Le jeton a été reçu ****");
                            Sur_Reception_De(jeton);
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
            System.out.println("Le consomateur a envoyer le jeton");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean readyNeighbors(){
        if(outOSuccesseur== null || inOpredecesseur == null) return false;
        return true;
    }

    public void initialize_Consommateur(int id){ //Etablie les connexions entre le conso et chaque producteur
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
           System.out.println("je peux consommer");
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

    /*public static void main (String[] args){
        int port = Integer.parseInt(args[1]);
        try {
            InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(args[0]), port);
            ServerSocket se = new ServerSocket();
            se.bind(address);
            Jeton jeton=new Jeton(N);
            int i=0;
            while (true){
                Socket soc=se.accept();
                //producteurs.add(new ObjectOutputStream(soc.getOutputStream()));
                if(i==0){ //lancer le jeton
                    //envoyer_a(soc,jeton);
                }
                //ThreadProducteur threadProducteur=new ThreadProducteur(soc,"P"+i);
                threadProducteur.start();
                i++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }*/



}
