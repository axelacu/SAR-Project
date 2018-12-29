package fr.SAR.projet.producteurConsommateur.consommateur;


import java.util.*;
import java.net.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.lang.Thread.*;

import fr.SAR.projet.Test.Context;
import fr.SAR.projet.Test.Serveur;
import fr.SAR.projet.Test.Site;
import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.message.Message;
import fr.SAR.projet.message.ToSend;
import fr.SAR.projet.producteurConsommateur.producteur.Producteur;


public class Consommateur {
    ArrayList<ObjectOutputStream> producteurs=new ArrayList<>();
    Message[] T;
    int N;
    int inc=0;
    int outc=0;
    int NbMess=0;
    int NbCell=0;


    Socket successeur;
    Socket predecesseur;

    public ObjectOutputStream outOSuccesseur;
    public ObjectInputStream inOpredecesseur;

    public Object monitorInc;
    public  Object monitorOutC;
    public  Object monitorJeton;



    public Consommateur(int N){
        this.N=N;
        T=new Message[N];
    }

    public  boolean Sur_Reception_De(ToSend toSend){
        try {
            if (toSend instanceof Message) {
                synchronized (monitorInc) {
                    T[inc] = ((Message) toSend);
                    inc = (inc + 1) % N;
                    NbMess++;
                }
            }
            if (toSend instanceof Jeton) {
                synchronized (monitorJeton) {
                    Jeton jeton = (Jeton) toSend;
                    jeton.setVal(NbCell);
                    envoyer_a(outOSuccesseur,jeton);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }



    public  void consommer(){
        while(true){
            while (NbMess > 0) {
                synchronized (monitorOutC) {
                    System.out.println("Je consomme le message:");
                    System.out.println(T[outc]);
                    outc = (outc + 1) % N;
                    NbMess--;
                    NbCell++;
                }

            }
        }
    }


    public  Runnable callSRD(final ToSend toSend){
        return new Runnable() {
            @Override
            public void run() {
                Sur_Reception_De(toSend);
            }
        };
    }
    public  Runnable callConsommer(){
        return new Runnable() {
            @Override
            public void run() {
                consommer();
            }
        };
    }

    public void envoyer_a(ObjectOutputStream outOSuccesseur, ToSend content){
        try {
            outOSuccesseur.writeObject(content);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lancer_Consommateur(Site site,int port){ //Etablie les connexions entre le conso et chaque producteur
        try{

            //nouveau serveur pour la connexion avec chaque site
            InetSocketAddress address = new InetSocketAddress(site.getAdress(), port);
            ServerSocket se = new ServerSocket();
            se.bind(address);

            for(int i=0;i<Context.getContext().length;i++){

                //ThreadProducteur threadProducteur=new ThreadProducteur();
            }

        }catch(Exception e){
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
