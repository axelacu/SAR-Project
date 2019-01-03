package fr.SAR.projet.message;

public class Requete extends ToSend {
	
	private int siteId;
	private int sender;
	
	public Requete(int siteId) {
		this.siteId = siteId;
	}

	public Requete(int siteId, int sender){
		this.siteId = siteId;
		this.sender = sender;
	}
	
	public int getSiteId() {
		return siteId;
	}
	
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getSender(){
		return sender;
	}
}
