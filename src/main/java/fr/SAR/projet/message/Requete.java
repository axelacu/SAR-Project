package fr.SAR.projet.message;

public class Requete extends ToSend {
	
	private int siteId;
	
	public Requete(int siteId) {
		this.siteId = siteId;
	}
	
	public int getSiteId() {
		return siteId;
	}
	
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
}
