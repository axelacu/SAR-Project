package fr.SAR.projet.ElectionUnidirectionelle;

import fr.SAR.projet.message.Confirmation;
import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.message.Requete;
import fr.SAR.projet.message.ToSend;

import java.io.*;

import static java.lang.Thread.sleep;

public class Election  {
    ObjectOutputStream successor;
    ObjectInputStream predecessor;
    boolean initiator;
    Etat etat;
    int identity;
    int id;


    public Election( OutputStream successor, InputStream predecessor,boolean initiator, int id){
        try {
            etat = Etat.Repos;
            this.initiator = initiator;
            this.identity = -1;
            this.successor = new ObjectOutputStream(successor);
            this.id = id;
            this.predecessor=new ObjectInputStream(predecessor);
        }catch(Exception e){
            e.printStackTrace();
        }
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
            if(etat==etat.Repos || ((Requete) toSend).getSiteId()<=this.identity){
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
                try {
                    while(true){
                        Object object = predecessor.readObject();
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

    public int initializeElection(boolean isConsumer){
        Thread srd=new Thread(callSRD());
        srd.start();
        if (isConsumer){
            Leader();
        }
        try{
            srd.join();

        }catch (Exception e){
            System.out.println("Probleme Leader");
            e.printStackTrace();
        }finally {
            try{
                successor.close();
                predecessor.close();
            }catch (Exception e){
                System.out.println("Probleme fermeture du canal du successeur");
            }
        }
        return this.identity;


    }


}
