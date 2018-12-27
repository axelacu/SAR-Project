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


public class Consommateur {
    static String[] T;
    static int N;
    static int inc=0;
    static int outc=0;
    static int NbMess=0;
    static int NbCell=0;


    public Object monitorInc;
    public Object monitorOutC;
    public Object monitorJeton;



    public Consommateur(int N){
        this.N=N;
        T=new String[N];
    }

    public boolean Sur_Reception_De(ToSend toSend){

        if(toSend instanceof Message){
            synchronized (monitorInc) {
                T[inc] = ((Message) toSend).getMessage();
                inc = (inc + 1) % N;
                NbMess++;
            }
        }
        if (toSend instanceof Jeton){ //TODO: pas sure que ce soit obligatoire
            synchronized (monitorJeton){
                Jeton jeton=(Jeton) toSend;
                jeton.setVal(NbCell);

            }

        }
        return true;
    }
    public void consommer(ToSend toSend){

    }
    public Runnable callSRD(final ToSend toSend){
        return new Runnable() {
            @Override
            public void run() {
                Sur_Reception_De(toSend);
            }
        };
    }
    public Runnable callConsommer(final ToSend toSend){
        return new Runnable() {
            @Override
            public void run() {
                consommer(toSend);
            }
        };
    }

    public static void main (String[] args){
        try {
            int port = 4020;
            InetAddress test = InetAddress.getByName("25.46.150.102");// ne fait pas partie du tp
            InetSocketAddress val = new InetSocketAddress(test, port);//ne fait pas partie du TP
            ServerSocket se = new ServerSocket();
            se.bind(val);
            int i=0;
            while (true){ //pour ne pas que le serveur se decconnecte
                Socket soc=se.accept();
                ThreadProducteur threadProducteur=new ThreadProducteur(soc,"P"+i);
                threadProducteur.start();
                i++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }



}
