package fr.SAR.projet;

import fr.SAR.projet.election.electionBidirectionnelle.ElectionFranklin;
import fr.SAR.projet.election.electionUnidirectionelle.ElectionCR;
import fr.SAR.projet.election.electionUnidirectionelle.ElectionLelann;
import fr.SAR.projet.producteurConsommateur.Consommateur;
import fr.SAR.projet.producteurConsommateur.Consommateur2;
import fr.SAR.projet.producteurConsommateur.Producteur;

import java.util.Scanner;

/**
 * Main application
 */
public class App {
    static String[] context = new String[]{"25.46.150.102:4020", "25.46.130.120:4020"};

    public static void main(String[] args) {
        launch();
    }

    private static int election(Site site, boolean participate){
        int elect = 0;
        System.out.println("*** L'election a été lancée ****");
        switch (Context.election){
            case 0:
                ElectionCR election = new ElectionCR(site.getId(),site.getOutSuccessor(),site.getInPredecessor(),participate);
                elect = election.initializeElection();
                System.out.println("Le chef a été elu il correspond a : " + elect );
                break;
            case 1:
                ElectionLelann el = new ElectionLelann(site.getId(),site.getoOutSucessor(),site.getoInPredecessor());
                elect = el.initialize(participate);
                System.out.println("Le chef a été elu il correspond a : " + elect);
                break;
            case 2:
                int idPred = Context.idPredecesseur(site.getId()); // retourne id du site precedent     getoInSucessor()/getoInSuccesor()
                ElectionFranklin electionFranklin;
                electionFranklin = new ElectionFranklin(site.getId(),idPred,site.getoOutSucessor(), site.getoInPredecessor(), site.getoInSuccesor(), site.getoOutPredecessor(), participate);
                elect = electionFranklin.initializeElectionFranklin();
                System.out.println("Le chef a été elu il correspond a : " + elect);

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
        //0 : pour Chang-Robert, 1: pour Lelann, 2: pour Franklin
        Context.setElection(1);
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
