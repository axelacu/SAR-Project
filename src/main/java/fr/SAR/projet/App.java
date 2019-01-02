package fr.SAR.projet;

import fr.SAR.projet.producteurConsommateur.Consommateur;
import fr.SAR.projet.producteurConsommateur.Producteur;

import java.util.Scanner;

/**
 * Main application
 */
public class App {
    public static void main(String[] args) {
        int i;
        System.out.println("What is your id ? : ");
        Scanner sc = new Scanner(System.in);

        int id = Integer.parseInt(sc.nextLine());
        String[] context = new String[]{"25.46.150.102:4020", "25.46.130.120:4020", "25.57.89.188:4020"};
        Context.setContext(context, ":");
        //creating site.
        Site site = new Site(id);

        //election.
        int consommateurid = 1;
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
}
