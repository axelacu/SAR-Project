package fr.SAR.projet.election;

import static java.lang.Thread.sleep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import fr.SAR.projet.Site;
import fr.SAR.projet.message.Confirmation;
import fr.SAR.projet.message.Requete;
import fr.SAR.projet.message.ToSend;


public class Election {
	int precedent;
	String etat = "repos";
	int nbreq;
	int conc;
	boolean dir = false;
	int concav;
	int chef = 0;
	Site site;
	private OutputStream outSuccessor;
	private Socket predecesseur;
    private InputStream inPredecessor;
    private InputStream inSuccessor;
	
	public Election(Site site) {
		this.site = site;
		outSuccessor = site.getOutSuccessor();
		inPredecessor = site.getInPredecessor();
		inSuccessor = site.getInSuccesor();
		predecesseur = site.getPredecesseur();
	}
		
	public int leader() {
			//PrintWriter pwPredecessor = new PrintWriter (predecesseur.getOutputStream());
			//PrintWriter pwSuccessor = new PrintWriter(outSuccessor);
			
			
			if(etat == "repos") {
				etat = "en_cours";
				System.out.println("Etat : " + etat);
				concav = site.getId();
				chef = site.getId();
				while(etat != "en_cours"){
					nbreq = 0;
					if(concav != site.getId()) {
						conc = concav;
						if(conc<chef) {
							chef = conc;
						}
						concav = site.getId();
						try {
							System.out.println("Site " + site.getId() + " envoie à son successeur et predecesseur");
							envoyer_a(new ObjectOutputStream(outSuccessor), new Requete(site.getId()));
							envoyer_a(new ObjectOutputStream(predecesseur.getOutputStream()), new Requete(site.getId()));
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
					
				}
				if(concav != site.getId()) {
					Requete requete = new Requete(concav);
					if(dir) {
						try {
							envoyer_a(new ObjectOutputStream(outSuccessor), requete);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						try {
							envoyer_a(new ObjectOutputStream(outSuccessor), requete);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
			}
			
			 while(etat != "termine"){
		            try {
		                sleep(1000);
		            } catch (InterruptedException e) {
		                e.printStackTrace();
		            }
		        }
			 System.out.println("elu : " + chef);
			return chef;
		}
	
	private void sur_reception_de(int j, int req) {
		if(etat == "repos" || req < chef) {
			chef = req;
		} else if(etat == "en_cours"){
			if(nbreq == 0) {
				conc=req;
				nbreq=1;
				dir = (j == precedent);
			} else if((dir && j==precedent) || (!dir && j != precedent)) {
				concav=req;
			} else {
				nbreq = 2;
				if(chef<site.getId()) {
					etat = "attente";
					System.out.println("Etat : " + etat);
				} else if(conc == site.getId() || req == conc) {
					etat = "termine";
					System.out.println("Etat : " + etat);
					try {
						envoyer_a(new ObjectOutputStream(outSuccessor), new Confirmation(site.getId())); // envoie de la confirmation
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			etat = "attendre";
			System.out.println("Etat : " + etat);
			if(j == precedent) {
				try {
					envoyer_a(new ObjectOutputStream(outSuccessor), new Requete(req));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					envoyer_a(new ObjectOutputStream(outSuccessor), new Requete(req));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public void sur_reception_de_conf(int j, int req) {
		if(site.getId() != req) {
			try {
				envoyer_a(new ObjectOutputStream(outSuccessor), new Requete(req));
			} catch (IOException e) {
				e.printStackTrace();
			}
			etat = "termine";
			System.out.println("Etat : " + etat);
		}
	}
	
	 public void envoyer_a(ObjectOutputStream oos, ToSend content){
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
	                    while(true){
	                    		BufferedReader in = new BufferedReader( new InputStreamReader (inPredecessor));
	                    		int rep = Integer.parseInt(in.readLine());
	                    		
	                        if(rep != 0){
	                            int idSite = rep;
	                            System.out.println(site.getId() + " a recu la requete de " + rep);
	                            sur_reception_de(idSite, idSite); // je pars du principe que j = k (pseudo code)
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
	                    while(true){
	                    		BufferedReader in = new BufferedReader( new InputStreamReader (inSuccessor));
	                    		int rep = Integer.parseInt(in.readLine());
	                    		
	                        if(rep != 0){
	                            int idSite = rep;
	                            System.out.println(site.getId() + " a recu la requete de " + rep);
	                            sur_reception_de(idSite, idSite); // je pars du principe que j = k (pseudo code)
	                        }
	                        sleep(1000);
	                    }
	                }catch (Exception e){
	                    System.err.println(e);
	                }
	            }
	        };
	    }
	    
	    public Runnable callSRDconf(){
	        return new Runnable() {
	            @Override
	            public void run() {
	                try {
	                    while(true){
	                    		BufferedReader in = new BufferedReader( new InputStreamReader (inPredecessor));
	                    		int rep = Integer.parseInt(in.readLine());
	                    		
	                        if(rep != 0){
	                            int idSite = rep;
	                            System.out.println(site.getId() + " a recu la confirmation de " + rep);
	                            sur_reception_de_conf(idSite, idSite); // je pars du principe que j = k (pseudo code)
	                        }
	                        sleep(1000);
	                    }
	                }catch (Exception e){
	                    System.err.println(e);
	                }
	            }
	        };
	    }
	    
	    
}
