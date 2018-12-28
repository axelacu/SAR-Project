package fr.SAR.projet.producteurConsommateur.producteur;

import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.message.Message;
import fr.SAR.projet.message.ToSend;
import fr.SAR.projet.producteurConsommateur.Consommateur;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
    OutputStream outConsommateur;
    OutputStream outSuccesseur;

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
        attendre_produir();
        tableau[in] = message;
        in = (in + 1) % N;
        nbmess++;
    }
    public  void sur_reception_de(Jeton jeton){
        temp = Math.min(nbmess-nbaut,jeton.getVal());
        nbaut += temp;
        jeton.setVal(jeton.getVal() - temp);
        envoyer_a(successeur,jeton);
        //TODO : envoyer via Object outputStream du successeur.

    }
    public void facteur(){
        while(true){
            attendre_facteur();
            envoyer_a(consommateur, tableau[out]);
            out = (out + 1) % N;
            nbaut--;
            nbmess--;
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
    public void attendre_produir(){
        while(!(nbmess<N)){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void envoyer_a(Socket succ, ToSend content){
        try {
            ObjectOutputStream objectStream = new ObjectOutputStream(succ.getOutputStream());
            objectStream.writeObject(content);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSuccesseur(Socket successeur) {
        this.successeur = successeur;
        //TODO : voir si mettre un output stream.
    }

    public void setConsommateur(Socket consommateur) {
        this.consommateur = consommateur;
        //TODO : Verifier si necessaire.
        /*
        try {
            outConsommateur = consommateur.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public static void main(String[] args){
        InetAddress add = null;
        try {
            add = InetAddress.getByName("25.46.130.120");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            Socket socket = new Socket(add,4020);
            Producteur producteur = new Producteur();
            Jeton jeton = new Jeton(4);
            producteur.envoyer_a(socket,jeton);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
