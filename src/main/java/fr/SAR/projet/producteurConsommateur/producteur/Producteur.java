package fr.SAR.projet.producteurConsommateur.producteur;


import fr.SAR.projet.Test.Client;
import fr.SAR.projet.Test.Context;
import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.message.Message;
import fr.SAR.projet.message.ToSend;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Producteur extends Thread {
    int N;
    Message[] tableau;
    int in ;
    int out;
    int nbmess;
    int nbaut;
    int temp;
    int id;
    //TODO : eviter de recevoir de socket.
    Socket consommateur;
    public ObjectOutputStream outOConsommateur;
    public ObjectOutputStream outOSuccesseur;
    public ObjectInputStream inOpredecesseur;

    private Object monitorTableau;

    public Producteur(){

    }
    public Producteur(int N){
        tableau = new Message[N];
        in = 0;
        out = 0;
        nbmess = 0;
        nbaut = 0;
        // TODO : initialiser avec l'identifiant du
    }

    public void produire(Message message){
        attendre_produire();
        synchronized (monitorTableau) {
            tableau[in] = message;
            in = (in + 1) % N;
            nbmess++;
        }
    }
    public  void sur_reception_de(Jeton jeton){
        temp = Math.min(nbmess-nbaut,jeton.getVal());
        nbaut += temp;
        jeton.setVal(jeton.getVal() - temp);
        envoyer_a(outOSuccesseur,jeton);
        System.out.println("*** Le jeton a été envoyé ****");
    }
    public void facteur(){
        while(true){
            attendre_facteur();
            synchronized (monitorTableau) {
                envoyer_a(outOConsommateur, tableau[out]);
                out = (out + 1) % N;
                nbaut--;
                nbmess--;
            }
        }
    }

    public void attendre_facteur(){
        while(!(nbaut>0)){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void attendre_produire(){
        while(!(nbmess<N)){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void envoyer_a(ObjectOutputStream outOSuccesseur, ToSend content){
        try {
            outOSuccesseur.writeObject(content);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public void setConsommateur(Socket consommateur) {
        this.consommateur = consommateur;
        //TODO : Verifier si necessaire.
        try {
            outOConsommateur = new ObjectOutputStream(consommateur.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Runnable callSRD(){
        return new Runnable() {
            @Override
            public void run() {
                try {
                    while(true){
                        //TODO : voir comment faire pour les Messages
                        Object object = inOpredecesseur.readObject();
                        if(object!=null){
                            Jeton jeton = (Jeton) object;
                            System.out.println("**** Le jeton a été reçu ****");
                            sur_reception_de(jeton);
                        }
                        sleep(1000);
                    }
                }catch (Exception e){
                    System.err.println(e);
                }
            }
        };
    }

    /**
     * Runnable that call factor fonction.
     * @return
     */
    public Runnable callFacteur(){
        return new Runnable() {
            @Override
            public void run() {
                facteur();
            }
        };
    }

    public static void main(String[] args){

        int port=4020;

        try {
            InetAddress test = InetAddress.getByName("25.46.150.102");// ne fait pas partie du tp
            InetSocketAddress val = new InetSocketAddress(test,port);//ne fait pas partie du TP
            ServerSocket se = new ServerSocket();
            se.bind(val); // ne fait pas partie du tP.

            System.out.println("Le serveur est à l'écoute " + "dans l'InetAdress suivante : " + se.getInetAddress());
            System.out.println("Le serveur est à l'ecoute");
            Socket socket = se.accept();
            System.out.println(socket.getInetAddress());
            System.out.println("Connexion accepte");
            Producteur producteur = new Producteur(10);
            producteur.setSuccesseur(socket.getOutputStream());
            producteur.setPredecesseur(socket.getInputStream());
            Thread th = new Thread(producteur.callSRD());
            th.start();
            while (true){
            }
        } catch (Exception e) {
            System.out.println("laa");
            e.printStackTrace();
        }
    }


    public void setJetonContext(OutputStream successeur,InputStream predecesseur){
        this.setSuccesseur(successeur);
        this.setPredecesseur(predecesseur);
    }

    public boolean readyNeighbors(){
        if(outOSuccesseur== null || inOpredecesseur == null) return false;
        return false;
    }

    /**
     * Search the server of the consumer.
     * @param id id of the consummer.
     * @return
     */
    public boolean searchingConsumer(int id){
        Client client = new Client(Context.getAddress(id), Context.getportConsumer());
        setConsommateur(client.getSserv());
        return true;
    }


    public void initialize(){
        //TODO : Faire un join
        if(!readyNeighbors()){
            System.err.println("*** You can't You need to define your neighbors ***");
            return;
        }
        Thread thSRD = new Thread(callSRD());
        Thread thFacteur = new Thread(callFacteur());


        //TODO : Lancer les threads



    }
    //TODO : a definir pour rendre plus propres.
    public void close(){
    }
}
