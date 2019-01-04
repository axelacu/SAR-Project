package fr.SAR.projet;

import fr.SAR.projet.ElectionUnidirectionelle.Election;
import fr.SAR.projet.electionFrancklin.Franklin;
import fr.SAR.projet.electionLelan.ElectionLelann;
import fr.SAR.projet.producteurConsommateur.Consommateur;
import fr.SAR.projet.producteurConsommateur.Producteur;

import java.util.Scanner;

/**
 * Main application
 */
public class App {
    static String[] context = new String[]{"25.46.150.102:4020", "25.46.130.120:4020", "25.84.72.231:4020"};

    public static void main(String[] args) {
        launch();
    }

    private static int election(Site site, boolean participate){
        int elect = 0;
        System.out.println("*** L'election a été lancée ****");
        switch (Context.election){
            case 0:
                Election election = new Election(site.getId(),site.getOutSuccessor(),site.getInPredecessor(),participate);
                elect = election.initializeElection();
                System.out.println("Le chef a été elu il correspond a : " + elect );
                break;
            case 1:
                ElectionLelann el = new ElectionLelann(site.getId(),site.getoOutSucessor(),site.getoInPredecessor());
                elect = el.initialize(participate);
                System.out.println("Le chef a été elu il correspond a : " + elect);
                break;
            case 2:

                break;
        }
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

            if(site.getId() == leader){
                site.setConsumer(true);
            }else{
                site.setConsumer(false);
            }
            if (site.isConsumer()) {
                Consommateur consommateur = new Consommateur(3);
                consommateur.setJetonContext(site.getoOutSucessor(), site.getoInPredecessor());
                consommateur.initialize_Consumer(leader);
            } else {
                Producteur producteur = new Producteur(3,nickName);
                producteur.setJetonContext(site.getoOutSucessor(), site.getoInPredecessor());
                producteur.initialize(leader);
            }

            System.out.println("Voulez-vous continuer ? Y or N");
            rep = sc.nextLine();

        }while(!rep.equals("N"));

        site.close();
    }
}
