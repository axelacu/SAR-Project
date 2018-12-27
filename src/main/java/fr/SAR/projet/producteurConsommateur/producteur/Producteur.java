package fr.SAR.projet.producteurConsommateur.producteur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Producteur extends Thread {
    int nbCell;
    int prod;
    BufferedReader in;
    PrintWriter out;
    Socket socketConsommateur;

    public Producteur(String hoteConso, int port,int tailleBuf){
        nbCell = tailleBuf;
        InetAddress hote = null;
        try {
            hote = InetAddress.getByName(hoteConso);
        }catch (UnknownHostException e){
            System.err.println("Machine inconnue :" +e);
        }

        try{
            socketConsommateur = new Socket(hote,port);
            in = new BufferedReader(new InputStreamReader(socketConsommateur.getInputStream()));
            out = new PrintWriter(socketConsommateur.getOutputStream(),true);
        }catch (IOException e){
            System.err.println("Impossible de creer la socket du client : " +e);
        }
    }
    public void produire(String message){
        attendre();
        envoyerA(out,message);
        nbCell--;
    }
    public void envoyerA(PrintWriter out, String message){
        out.println(message);
        System.out.println("message envoyé");
    }
    public void attendre(){
        while(nbCell<0){
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void surReceptionDe(){
        System.out.println("Je suis dans sureception");
        String ack = null;
        try {
            ack = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(ack!= null) {
            if (ack.equals("Ack")) nbCell++;
            System.out.println("Message Ack");
        }else{
            System.out.println("Pas d'ackittement.");
        }
        System.out.println("Je sort de sureception");
    }
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        if(args.length!=2){
            System.err.println("Bad saisi");
            System.exit(1);
        }
        int port = Integer.parseInt(args[1]);
        Producteur producteur = new Producteur(args[0],port,5);
        String message;
        do{
            System.out.println("Ecrire message : ");
            message = sc.nextLine();
            System.out.println("Fin d'ecriture du message");
            producteur.produire(message);

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            producteur.surReceptionDe();
        }while(!message.equals("Bye"));

    }
}
