package fr.SAR.projet.ElectionUnidirectionelle;

import fr.SAR.projet.message.Confirmation;
import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.message.Requete;
import fr.SAR.projet.message.ToSend;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import static java.lang.Thread.sleep;

public class Election  {
    ObjectOutputStream successor;
    boolean initiator;
    Etat etat;
    int identity;
    int id;


    public Election(Etat etat, OutputStream successor,boolean initiator,int id){
        try {
            etat = Etat.Repos;
            this.initiator = initiator;
            this.identity = -1;
            this.successor = new ObjectOutputStream(successor);
            this.id = id;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean Sur_reception_De(Jeton jeton){
        return true;
    }


    public void Leader(){ //pour les initiateurs uniquement
        try {
            if (etat == Etat.Repos) {
                etat = etat.en_cours;
                identity = this.id;
                Requete requete = new Requete(this.id);
                envoyer_a(successor, requete);
                attendre_terminer();
                envoyer_a(successor,new Confirmation(this.identity));
            }
        }catch(Exception e) {
            System.out.println("probleme leader");
            e.printStackTrace();
        }

    }

    public void attendre_terminer(){
        while(etat!=Etat.termine){
            try {
                sleep(1000);
            }catch(Exception e){
                System.out.println("probleme attendre terminer");
                e.printStackTrace();
            }
        }
    }

    public void sur_reception_de(ToSend toSend){
        if(toSend instanceof Confirmation){
            if(this.identity!=((Confirmation) toSend).getConf()){
                etat=etat.termine;
            }
        }
        if(toSend instanceof Requete){
            if(etat==etat.Repos || ((Requete) toSend).getSiteId()==this.identity){
                etat=etat.en_cours;
                this.identity=((Requete) toSend).getSiteId();
                envoyer_a(successor,toSend);
            } else {
                if(this.id==((Requete) toSend).getSiteId()){
                    etat=etat.termine;
                    envoyer_a(successor,new Confirmation(this.id));
                }
            }

        }
    }


    public Runnable callSRD(){
        return new Runnable() {
            @Override
            public void run() {
                while (etat!=etat.termine){
                  callSRD();
                  try {
                      sleep(100);
                  } catch(Exception e){
                      e.printStackTrace();
                  }
                }
            }
        };
    }

    public void envoyer_a(ObjectOutputStream outOSuccesseur, ToSend content){
        try {
            outOSuccesseur.writeObject(content);
            if(content instanceof Requete) {
                System.out.println("J'envoie la requete");
            } else {
                System.out.println("J'envoie la confirmation ");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeElection(){

    }


}
