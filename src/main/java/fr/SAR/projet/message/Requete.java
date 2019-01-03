package fr.SAR.projet.message;

import fr.SAR.projet.Context;

public class Requete extends ToSend {
	
	private int siteId;
	private int precedent;
	
	public Requete(int siteId) {
		this.siteId = siteId;
	}
	
	public int getSiteId() {
		return siteId;
	}
	
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getPrecedent() {
		return precedent;
	}

	public void setPrecedent(int precedent) {
		this.precedent = precedent;
	}
}
