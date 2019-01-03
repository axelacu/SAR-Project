package fr.SAR.projet.election;

import static java.lang.Thread.sleep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import fr.SAR.projet.Site;
import fr.SAR.projet.ElectionUnidirectionelle.Etat;
import fr.SAR.projet.message.Confirmation;
import fr.SAR.projet.message.Requete;
import fr.SAR.projet.message.ToSend;


public class ElectionFranklin {
	Etat etat;
	int nbreq;
	int conc;
	boolean dir = false;
	int concav;
	int chef;
	boolean initiator;
	ObjectOutputStream outSuccessor;
    ObjectInputStream inPredecessor;
    ObjectInputStream inSuccessor;
    ObjectOutputStream outPredecessor;
	private int siteid;
	private int idPred;


	public ElectionFranklin(int siteid, int idPred, ObjectOutputStream outSuccessor, ObjectInputStream inPredecessor, ObjectInputStream inSuccessor, ObjectOutputStream outPredecessor, boolean initiator){
        try {
            etat = Etat.Repos;
            this.siteid = siteid;
            this.idPred = idPred;
            this.initiator = initiator;
            this.outSuccessor = outSuccessor;
            this.inPredecessor=inPredecessor;
            this.inSuccessor = inSuccessor;
            this.outPredecessor=outPredecessor;
            this.chef = -1;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
	
		
	public int leader() {
			
			if(etat == etat.Repos) {
				etat = etat.en_cours;
				System.out.println("Etat : " + etat);
				concav = siteid;
				chef = siteid;
				while(etat != etat.en_cours){
					nbreq = 0;
					if(concav != siteid) {
						conc = concav;
						if(conc<chef) {
							chef = conc;
						}
						concav = siteid;
						System.out.println("Site " + siteid + " envoie à son successeur et predecesseur");
						envoyer_a(outSuccessor, new Requete(siteid));
						envoyer_a(outPredecessor, new Requete(siteid));
						
					}
					
				}
				if(concav != siteid) {
					Requete requete = new Requete(concav);
					if(dir) {
						envoyer_a(outSuccessor, requete);
					} else {
						envoyer_a(outSuccessor, requete);
					}
				}
				
			}
			
			 while(etat != etat.termine){
		            try {
		                sleep(1000);
		            } catch (InterruptedException e) {
		                e.printStackTrace();
		            }
		        }
			 System.out.println("elu : " + chef);
			return chef;
		}
	
	private void sur_reception_de(int j, ToSend req) {
		if(req instanceof Requete) {
			
			Requete requete = (Requete)req;
			
			if(etat == etat.Repos || requete.getSiteId() < chef) {
				chef = requete.getSiteId();
			} else if(etat == etat.en_cours){
				if(nbreq == 0) {
					conc=requete.getSiteId();
					nbreq=1;
					dir = (j == this.idPred);
				} else if((dir && j==this.idPred) || (!dir && j != this.idPred)) {
					concav=requete.getSiteId();
				} else {
					nbreq = 2;
					if(chef<siteid) {
						etat = etat.attente;
						System.out.println("Etat : " + etat);
					} else if(conc == siteid || requete.getSiteId() == conc) {
						etat = etat.termine;
						System.out.println("Etat : " + etat);
						envoyer_a(outSuccessor, new Confirmation(siteid)); // envoie de la confirmation
					}
				}
			} else {
				etat = etat.attente;
				System.out.println("Etat : " + etat);
				Requete req2 = new Requete(requete.getSiteId());
				if(j == this.idPred) {
					envoyer_a(outSuccessor, req2);
				} else {
					envoyer_a(outPredecessor, req2);
				}
			}
		} else if(req instanceof Confirmation) {
			Confirmation conf = (Confirmation)req;
			if(siteid != conf.getConf()) {
				System.out.println("Envoie de la confirmation");
				envoyer_a(outSuccessor, new Requete(conf.getConf()));
				etat = etat.termine;
				System.out.println("Etat : " + etat);
			}
			
		}
		
	}

	
	 public void envoyer_a(ObjectOutputStream oos, ToSend content){
	        try {
	        		oos.writeObject(content);
	        		oos.writeInt(siteid);
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	 
	 
	    public Runnable callSRDpredecessor(){
	        return new Runnable() {
	            @Override
	            public void run() {
	                try {
	                    while(etat != etat.termine){
	                    		Object object = inPredecessor.readObject();
	                    		int siteEmetteur = inPredecessor.readInt(); // j
	                    		System.out.println("emetteur : " + siteEmetteur);
	                    		if(object!=null){
	                                ToSend message = (ToSend) object;
	                                sur_reception_de(siteEmetteur, message);
	                            }
	                        sleep(1000);
	                    }
	                }catch (Exception e){
	                    System.err.println(e);
	                }
	            }
	        };
	    }
	    
	    public Runnable callSRDsuccessor(){
	        return new Runnable() {
	            @Override
	            public void run() {
	                try {
	                    while(etat != etat.termine){
	                    		Object object = inSuccessor.readObject();
	                    		int siteEmetteur = inPredecessor.readInt(); // j
	                    		if(object!=null){
	                                ToSend message = (ToSend) object;
	                                sur_reception_de(siteEmetteur, message);
	                            }
	                        sleep(1000);
	                    }
	                }catch (Exception e){
	                    System.err.println(e);
	                }
	            }
	        };
	    }
	    
	    
	    public int initializeElectionFranklin(){
	        Thread srdp=new Thread(callSRDpredecessor());
	        Thread srds=new Thread(callSRDsuccessor());
	    
	        	srdp.start();
	        	srds.start();
	        if (this.initiator){
	            leader();
	        }
	        try{
	            srdp.join();
	            srds.join();

	        }catch (Exception e){
	            System.out.println("Probleme Leader");
	            e.printStackTrace();
	        }finally {
	            try{
	                outSuccessor.close();
	                inPredecessor.close();
	                inSuccessor.close();
	                outPredecessor.close();
	            }catch (Exception e){
	                System.out.println("Probleme fermeture du canal du successeur");
	            }
	        }
	        return this.chef;


	    }
	    
	    
}
