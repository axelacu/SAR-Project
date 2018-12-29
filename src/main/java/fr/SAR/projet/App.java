package fr.SAR.projet;

import fr.SAR.projet.Test.Context;
import fr.SAR.projet.Test.Site;
import fr.SAR.projet.producteurConsommateur.consommateur.Consommateur;
import fr.SAR.projet.producteurConsommateur.producteur.Producteur;

import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        System.out.println("What is your id ? : ");
        Scanner sc = new Scanner(System.in);

        int id = Integer.parseInt(sc.nextLine());
        String[] context = new String[]{"25.46.150.102:4020","25.46.130.120:4020","25.46.130.120:4030"};
        Context.setContext(context,":");
        //creating site.
        Site site = new Site(id);
        System.out.println("Etes vous le consommateur? Y or N");
        String rep=sc.nextLine();
        if(rep.equals("Y")){
            Consommateur consommateur=new Consommateur(10);
            consommateur.setJetonContext(site.getOutSuccessor(),site.getInPredecessor());
            consommateur.initialize_Consommateur(site.getId());

        }else{
            Producteur producteur = new Producteur(5);
            producteur.setJetonContext(site.getOutSuccessor(),site.getInPredecessor());
            producteur.initialize(10);
        }

    }
}
