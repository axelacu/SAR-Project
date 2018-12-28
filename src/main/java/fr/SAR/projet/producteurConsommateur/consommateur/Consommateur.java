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

import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.message.Message;
import fr.SAR.projet.message.ToSend;
import fr.SAR.projet.producteurConsommateur.producteur.Producteur;


public class Consommateur {
    public static ArrayList<ObjectOutputStream> producteurs=new ArrayList<>();
    static Message[] T;
    static int N;
    static int inc=0;
    static int outc=0;
    static int NbMess=0;
    static int NbCell=0;


    public static Object monitorInc;
    public static Object monitorOutC;
    public static Object monitorJeton;



    public Consommateur(int N){
        this.N=N;
        T=new Message[N];
    }

    public static boolean Sur_Reception_De(ToSend toSend){
        try {
            if (toSend instanceof Message) {
                synchronized (monitorInc) {
                    T[inc] = ((Message) toSend);
                    inc = (inc + 1) % N;
                    NbMess++;
                }
            }
            if (toSend instanceof Jeton) { //TODO: pas sure que ce soit obligatoire
                synchronized (monitorJeton) {
                    Jeton jeton = (Jeton) toSend;
                    jeton.setVal(NbCell);

                }

            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static void consommer(){
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


    public static Runnable callSRD(final ToSend toSend){
        return new Runnable() {
            @Override
            public void run() {
                Sur_Reception_De(toSend);
            }
        };
    }
    public static Runnable callConsommer(){
        return new Runnable() {
            @Override
            public void run() {
                consommer();
            }
        };
    }

    public static void envoyer_a(Socket succ, ToSend content){
        try {
            ObjectOutputStream objectStream = new ObjectOutputStream(succ.getOutputStream());
            objectStream.writeObject(content);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main (String[] args){
        int port = Integer.parseInt(args[1]);
        try {
            InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(args[0]), port);
            ServerSocket se = new ServerSocket();
            se.bind(address);
            Jeton jeton=new Jeton(N);
            int i=0;
            while (true){
                Socket soc=se.accept();
                producteurs.add(new ObjectOutputStream(soc.getOutputStream()));
                if(i==0){ //lancer le jeton
                    envoyer_a(soc,jeton);
                }
                ThreadProducteur threadProducteur=new ThreadProducteur(soc,"P"+i);
                threadProducteur.start();
                i++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }



}
