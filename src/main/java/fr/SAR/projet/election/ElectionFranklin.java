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
	boolean dir;
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
				while(etat == etat.en_cours){
			
					nbreq = 0;
					if(concav != siteid) {
						nbreq = 1;
						conc = concav;
						if(conc<chef) {
							chef = conc;
						}
						concav = siteid;
					}
					System.out.println("Site " + siteid + " envoie à son successeur et predecesseur");
					Requete req = new Requete(siteid);
					envoyer_a(outSuccessor, req);
					envoyer_a(outPredecessor, req);
					
					
					while(nbreq != 2){ 
			            try {
			                sleep(1000);
			            } catch (InterruptedException e) {
			                e.printStackTrace();
			            }
			        }
					
				}
				if(concav != siteid) {
					Requete requete = new Requete(concav);
					System.out.println("dir : " + dir);
					if(dir) {
						envoyer_a(outSuccessor, requete);
					} else {
						envoyer_a(outPredecessor, requete);
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
	
	private void sur_reception_de(ToSend req) {
		
		if(req instanceof Requete) {
			int j = ((Requete) req).getPrecedent();
			Requete requete = (Requete)req;
			System.out.println("site " + siteid + " recois la requete : " + requete.getSiteId() + " du site " + j );
			if(etat == etat.Repos || requete.getSiteId() < chef) {
				chef = requete.getSiteId();
			} 
			if(etat == etat.en_cours){
				if(nbreq == 0) {
					conc=requete.getSiteId();
					nbreq=1;
					dir = (j == this.idPred);
				} else if((dir && j == this.idPred) || (!dir && j != this.idPred)) {
					concav=requete.getSiteId();
				} else {
					nbreq = 2;
					if(chef<siteid) {
						etat = etat.attente;
						System.out.println("Etat : " + etat);
						
					} else if(conc == siteid || requete.getSiteId() == conc) {
						etat = etat.termine;
						System.out.println("Etat : " + etat);
						System.out.println("Envoie de la confirmation");
						envoyer_a(outSuccessor, new Confirmation(siteid)); // envoie de la confirmation
					}
				}
			} else {
				etat = etat.attente;
				System.out.println("Etat : " + etat);
				Requete req = new Requete(requete.getSiteId());
				if(j == this.idPred) {
					envoyer_a(outSuccessor, req);
				} else {
					envoyer_a(outPredecessor, req);
				}
			}
		} else if(req instanceof Confirmation) {
			Confirmation conf = (Confirmation)req;
			System.out.println("site " + siteid + " recois la confirmation : " + conf.getConf());
			if(siteid != conf.getConf()) {
				System.out.println("Envoie de la confirmation");
				envoyer_a(outSuccessor, new Confirmation(conf.getConf()));
				etat = etat.termine;
				System.out.println("Etat : " + etat);
			}
			
		}
		
	}

	
	 public void envoyer_a(ObjectOutputStream oos, ToSend content){
		 	if(content instanceof Requete) {
		 		Requete req = (Requete)content;
		 		req.setPrecedent(siteid);
		 	}
	        try {  		
	        		oos.writeObject(content);
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
	                    		if(object!=null){
	                                ToSend message = (ToSend) object;
	                                sur_reception_de(message);
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
	                    		
	                    		if(object!=null){
	                                ToSend message = (ToSend) object;
	                                sur_reception_de(message);
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
	    		System.out.println("Etat : " + etat);
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
	               // outSuccessor.close();
	                //inPredecessor.close();
	                //inSuccessor.close();
	                //outPredecessor.close();
	            }catch (Exception e){
	                System.out.println("Probleme fermeture du canal du successeur");
	            }
	        }
	        System.out.println("CHEF : " + this.chef);
	        return this.chef;


	    }
	    
	    
}
