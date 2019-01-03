package fr.SAR.projet;

import fr.SAR.projet.ElectionUnidirectionelle.Election;
import fr.SAR.projet.producteurConsommateur.Consommateur;
import fr.SAR.projet.producteurConsommateur.Producteur;

import java.util.Scanner;

/**
 * Main application
 */
public class App {
    static String[] context = new String[]{"25.46.150.102:4013", "25.46.150.102:4015","25.46.150.102:4016"};

    public static void main(String[] args) {
        launch();
    }

    private static int election(Site site, boolean participate){
        int elect ;
        Election election = new Election(site.getId(),site.getOutSuccessor(),site.getInPredecessor(),participate);
        elect = election.initializeElection();
        System.out.println("On a fini l'election");
        return elect;
    }

    private static void launch(){

        System.out.println("What is your id ? : ");
        Scanner sc = new Scanner(System.in);

        int id = Integer.parseInt(sc.nextLine());
        String nickName;
        Context.setContext(context, ":");
        do{
            System.out.println("Give a nick name please : ");
            nickName = sc.nextLine();
        }while (nickName==null);

        //creating site.
        Site site = new Site(id);

        int leader;
        String rep;
        do{
            System.out.println("Voulez-vous participer à l'election des consommateurs? Y or N");
            rep = sc.nextLine();
            if(rep.equals("Y")){
                leader = election(site,true);
            }else{
                leader = election(site,false);
            }
            System.out.println("Le consommateur elu est: "+leader);

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
                Producteur producteur = new Producteur(10,nickName);
                producteur.setJetonContext(site.getOutSuccessor(), site.getInPredecessor());
                producteur.initialize(leader);
            }

            System.out.println("Voulez-vous continuer ? Y or N");
            rep = sc.nextLine();

        }while(!rep.equals("N"));

        site.close();
    }
}
