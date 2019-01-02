package fr.SAR.projet;

import fr.SAR.projet.producteurConsommateur.Consommateur;
import fr.SAR.projet.producteurConsommateur.Producteur;

import java.util.Scanner;

/**
 * Main application
 */
public class App {
    static String[] context = new String[]{"25.46.150.102:4020", "25.46.130.120:4020", "25.57.89.188:4020"};

    public static void main(String[] args) {

        System.out.println("What is your id ? : ");
        Scanner sc = new Scanner(System.in);

        int id = Integer.parseInt(sc.nextLine());
        Context.setContext(context, ":");
        //creating site.
        Site site = new Site(id);

        int consommateurid = 0;


        //
        System.out.println("Etes vous le consommateur? Y or N");
        String rep = sc.nextLine();
        if (rep.equals("Y")) {
            Consommateur consommateur = new Consommateur(10);
            consommateur.setJetonContext(site.getOutSuccessor(), site.getInPredecessor());
            consommateur.initialize_Consumer(consommateurid);
        } else {
            Producteur producteur = new Producteur(8);
            producteur.setJetonContext(site.getOutSuccessor(), site.getInPredecessor());
            producteur.initialize(consommateurid);
        }
    }

    private static int election(int id,boolean participate){
        int elect = 0;
        //election.
        return elect;
    }

    private static void launch(){

        System.out.println("What is your id ? : ");
        Scanner sc = new Scanner(System.in);

        int id = Integer.parseInt(sc.nextLine());
        Context.setContext(context, ":");
        //creating site.
        Site site = new Site(id);

        int leader;
        String rep;
        do{
            System.out.println("Voulez-vous participer à l'election des consommateurs? Y or N");
            rep = sc.nextLine();
            if(rep.equals("Y")){
                leader = election(site.getId(),true);
            }else{
                leader = election(site.getId(),false);
            }

            if(site.getId() == leader){
                site.setConsumer(true);
            }else{
                site.setConsumer(false);
            }
            if (site.isConsumer()) {
                Consommateur consommateur = new Consommateur(10);
                consommateur.setJetonContext(site.getOutSuccessor(), site.getInPredecessor());
                consommateur.initialize_Consumer(leader);
            } else {
                Producteur producteur = new Producteur(8);
                producteur.setJetonContext(site.getOutSuccessor(), site.getInPredecessor());
                producteur.initialize(leader);
            }

            System.out.println("Voulez-vous continuer ? Y or N");
            rep = sc.nextLine();

        }while(!rep.equals("N"));

        site.close();
    }
}
