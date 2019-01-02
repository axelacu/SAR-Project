package fr.SAR.projet;

import fr.SAR.projet.producteurConsommateur.Consommateur;
import fr.SAR.projet.producteurConsommateur.Producteur;

import java.util.Scanner;

/**
 * Main application
 */
public class App {
    public static void main(String[] args) {

        System.out.println("What is your id ? : ");
        Scanner sc = new Scanner(System.in);

        int id = Integer.parseInt(sc.nextLine());
        String[] context = new String[]{"25.46.150.102:4020", "25.46.150.102:4021", "25.46.150.102:4022"};
        Context.setContext(context, ":");
        //creating site.
        Site site = new Site(id);
        System.out.println("Etes vous le consommateur? Y or N");
        String rep = sc.nextLine();
        if (rep.equals("Y")) {
            Consommateur consommateur = new Consommateur(10);
            consommateur.setJetonContext(site.getOutSuccessor(), site.getInPredecessor());
            consommateur.initialize_Consommateur(site.getId());
        } else {
            Producteur producteur = new Producteur(8);
            producteur.setJetonContext(site.getOutSuccessor(), site.getInPredecessor());
            producteur.initialize(site.getId());
        }
    }
}
