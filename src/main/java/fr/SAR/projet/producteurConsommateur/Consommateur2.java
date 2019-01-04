package fr.SAR.projet.producteurConsommateur;


import java.util.*;
import java.io.*;
import java.io.IOException;
import java.net.Socket;
import fr.SAR.projet.Context;
import fr.SAR.projet.election.Etat;
import fr.SAR.projet.serveurclient.Serveur;
import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.message.Message;
import fr.SAR.projet.message.ToSend;

import static java.lang.Thread.sleep;


public class Consommateur2 {

    /**
     * list for all prod
     */
    ArrayList<ThreadProducteur2> producteurs=new ArrayList<>();

    int compteur=-1;
    /**
     * for the second election
     */
    Etat etat;

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
    final public Object monitorEtat=new Object();



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


    public Consommateur2(int N){
        try {
            this.N = N;
            T = new Message[N];
            etat=Etat.en_cours;
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
        boolean tocontinue=true;
        try {
            if (toSend instanceof Message) {
                synchronized (monitorInc) {
                    synchronized (monitorNbMess) {
                        T[inc] = ((Message) toSend);
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
                            this.NbCell = 0;
                            this.compteur++;
                            System.out.println(compteur);
                            if (this.compteur == 30) {
                                tocontinue = demandToContinue();
                            }
                            if (tocontinue == false) {
                                Etat etatnew = Etat.termine;

                                this.etat = Etat.termine;
                                for (ThreadProducteur2 threadProducteur : producteurs) {
                                    threadProducteur.setEtat(etatnew);
                                }
                            } else {
                                envoyer_a(outOSuccesseur, jeton);
                            }

                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean demandToContinue(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Avez vous d'autres chose à écouter? ");
        String rep=sc.nextLine();
        if(rep.equals("Y")){
            return true;
        }else {
            return false;
        }

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

                    while(etat==Etat.en_cours){
                        synchronized (monitorEtat) {
                            if (etat == Etat.termine) continue;
                            Object object = inOpredecesseur.readObject();
                            if (object != null) {
                                if (object instanceof Jeton) {
                                    Jeton jeton = (Jeton) object;
                                    Sur_Reception_De(jeton);
                                    sleep(1000);
                                }
                            }

                        }
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
                while(etat==Etat.en_cours) {
                    synchronized (monitorEtat) {
                        if (etat == etat.termine) continue;
                        ;
                        consommer();
                        try {
                            sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
               ThreadProducteur2 threadProducteur=new ThreadProducteur2(soc,"producteur"+i,this);
               threadProducteur.start();
               producteurs.add(threadProducteur);

            }
           System.out.println("Well Done; all connection etablished");
           for(ThreadProducteur2 threadProducteur:producteurs){
               threadProducteur.join();
           }

           System.out.println("J'ai terminer d'etre le consommateur");
           serveur.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
