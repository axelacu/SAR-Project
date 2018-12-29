package fr.SAR.projet.Test;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Site {
    boolean consumer = true;
    private int id;
    private Socket successor;
    public Site(int id){
        this.id = id;
        //open server
        Serveur serveur = new Serveur(Context.getAddress(id),Context.getPort(id));

        //connecting to client
        int idPred = Context.idPredecesseur(id);
        Client predecesseur = new Client(Context.getAddress(idPred),Context.getPort(idPred));
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
        String[] context = new String[]{"192.168.1.28:4020", "192.168.56.1:4020","25.46.130.120:4020"};
        Context.setContext(context,":");
        //creating site.
        Site site = new Site(id);

    }
}
