package fr.SAR.projet.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Site {
    boolean consumer = true;
    private int id;
    private Socket successor;
    private Client predecesseur;
    private OutputStream outSuccessor;
    private InputStream inPredecessor;


    public Site(int id){
        this.id = id;
        //open server
        Serveur serveur = new Serveur(Context.getAddress(id),Context.getPort(id));

        //connecting to client
        int idPred = Context.idPredecesseur(id);
        predecesseur = new Client(Context.getAddress(idPred),Context.getPort(idPred));
        predecesseur.start();

        //waiting from successor;
        System.out.println("Waiting two side connection... ");
        successor = serveur.ajoutClient();
        while(!predecesseur.connect()){
            try {
                System.out.println("Waiting predecessor...");
                sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            outSuccessor = successor.getOutputStream();
            inPredecessor = predecesseur.getInputStream();
        } catch (IOException e) {
            System.err.println("Error connecting the Input and outpur Stream");
        }

        System.out.println("*** Well DONE connection established ***");

    }

    public InetAddress getAdress(){
        return Context.getAddress(id);
    }

    public Socket getSuccessor() {
        return successor;
    }

    public Socket getPredecesseur() {
        return predecesseur.getSserv();
    }


    public void setConsumer(boolean consumer) { //à appeler pour les producteurs
        this.consumer = consumer;
    }


    public void lancerConsommateur() {
        try {
            Serveur serveur = new Serveur(Context.getAddress(id), Context.getportConsumer());
            for (int i = 0; i < Context.getContext().length - 1; i++) {
                Socket soc = serveur.ajoutClient();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args){
        System.out.println("What is your id ? : ");
        Scanner sc = new Scanner(System.in);

        int id = Integer.parseInt(sc.nextLine());


        // define context
        String[] context = new String[]{"25.46.150.102:4020","25.46.130.120:4020","25.46.130.120:4010"};
        Context.setContext(context,":");
        //creating site.
        Site site = new Site(id);
        System.out.println("Etes vous le consommateur? ");
        String rep=sc.nextLine();
        if(rep.equals("Y")){ //Cette machine est le consommateur

        }
        /*
        Producteur producteur = new Producteur(10);
        producteur.setJetonContext(site.getSuccessor(),site.getSserv());
        Jeton jeton = new Jeton(5);
        System.out.println("Voulez vous envoyer le jeton :");
        String reponse = sc.nextLine();
        if(reponse.equals("Y")) {
            producteur.envoyer_a(producteur.outOSuccesseur, jeton);
            System.out.println("Le jeton a été envoyé ");
        }
        while(true){
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }

    public InputStream getInPredecessor() {
        return inPredecessor;
    }

    public OutputStream getOutSuccessor() {
        return outSuccessor;
    }
}
