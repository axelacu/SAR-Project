package fr.SAR.projet.Test;

import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.producteurConsommateur.producteur.Producteur;

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

    public Socket getSuccessor() {
        return successor;
    }

    public Socket getPredecesseur() {
        return predecesseur.getPredecesseur();
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
        Producteur producteur = new Producteur(10);
        producteur.setJetonContext(site.getSuccessor(),site.getPredecesseur());
        Jeton jeton = new Jeton(5);
        System.out.println("Voulez vous envoyer le jeton :");
        String reponse = sc.nextLine();
        if(reponse.equals("Y"))
            producteur.envoyer_a(producteur.outOSuccesseur,jeton);

    }
}
