package fr.SAR.projet.producteurConsommateur.producteur;


import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.message.Message;
import fr.SAR.projet.message.ToSend;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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
    Socket consommateur;
    Socket successeur;
    Socket predecesseur;
    ObjectOutputStream outOConsommateur;
    ObjectOutputStream outOSuccesseur;
    ObjectInputStream inOpredecesseur;

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

    public void setSuccesseur(Socket successeur) {
        this.successeur = successeur;
        //TODO : voir si mettre un output stream.
        try {
            outOSuccesseur = new ObjectOutputStream(successeur.getOutputStream());
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

    public static void main(String[] args){

        InetAddress add = null;
        try {
            add = InetAddress.getByName("25.46.150.102");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String reponse;
        try {
            Jeton jeton = new Jeton(4);
            Socket socket = new Socket(add,4020);
            Producteur producteur = new Producteur(10);
            Scanner sc = new Scanner(System.in);
            Thread th = new Thread(producteur.callSRD());
            producteur.setSuccesseur(socket);
            producteur.setPredecesseur(socket);
            th.start();
            System.out.println("Voulez vous envoyer le jeton :");
            reponse = sc.nextLine();
            if(reponse.equals("Y"))
                producteur.envoyer_a(producteur.outOSuccesseur,jeton);
            System.out.println("Je fais un sleep");
            while(true){

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPredecesseur(Socket predecesseur) {
        this.predecesseur = predecesseur;
        try {
            inOpredecesseur = new ObjectInputStream(predecesseur.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
