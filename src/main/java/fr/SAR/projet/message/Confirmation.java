package fr.SAR.projet.message;

public class Confirmation extends ToSend {
	
	int conf;
	
	public Confirmation(int conf){
		this.conf = conf;
	}
	
	public int getConf() {
		return conf;
	}
	
	public void setConf(int conf) {
		this.conf = conf;
	}
	
	
}
