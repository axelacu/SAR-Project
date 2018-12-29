package fr.SAR.projet.Test;

import java.net.Socket;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Site {
    boolean consumer = true;
    private int id;
    private Socket successor;
    private Client predecesseur;


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

        System.out.println("*** Well DONE connection established ***");

    }

    public static void main(String[] args){
        System.out.println("What is your id ? : ");
        Scanner scanner = new Scanner(System.in);
        int id = Integer.parseInt(scanner.nextLine());
        // define context
        String[] context = new String[]{"25.46.150.102:4020","25.46.130.120:4020","25.46.130.120:4010"};
        Context.setContext(context,":");
        //creating site.
        Site site = new Site(id);
    }
}
