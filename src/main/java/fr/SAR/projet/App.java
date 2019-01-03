package fr.SAR.projet;

import fr.SAR.projet.ElectionUnidirectionelle.Election;
import fr.SAR.projet.election.ElectionFranklin;
import fr.SAR.projet.producteurConsommateur.Consommateur;
import fr.SAR.projet.producteurConsommateur.Producteur;

import java.io.IOException;
import java.util.Scanner;

/**
 * Main application
 */
public class App {
	//static String[] context = new String[]{"25.57.89.188:4020", "25.57.89.188:4021", "25.57.89.188:4022"};
	static String[] context = new String[]{"25.46.150.102:4020", "25.46.130.120:4020", "25.57.89.188:4020", "25.84.72.231:4020"};
	//static String[] context = new String[]{"25.46.130.120:4020", "25.57.89.188:4020", "25.84.72.231:4020"};
	//static String[] context = new String[]{"25.46.150.102:4020", "25.46.130.120:4020", "25.57.89.188:4020"};
    public static void main(String[] args) {
        launch();
    }

    private static int election(Site site, boolean participate){
        int elect ;
        Election election = new Election(site.getId(),site.getOutSuccessor(),site.getInPredecessor(),participate);
        elect = election.initializeElection();
        return elect;
    }
    
    private static int electionFranklin(Site site, boolean participate) {
    		int elu = 0;

    		int idPred = Context.idPredecesseur(site.getId()); // retourne id du site precedent
    		ElectionFranklin electionFranklin;
            electionFranklin = new ElectionFranklin(site.getId(),idPred,site.getoOutSucessor(), site.getoInPredecessor(), site.getoInSucessor(), site.getoOutPredecessor(), participate);
            elu = electionFranklin.initializeElectionFranklin();
    		
    		return elu;
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
                leader = electionFranklin(site,true);
            }else{
                leader = electionFranklin(site,false);
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
