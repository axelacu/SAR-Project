package fr.SAR.projet.electionLelan;


import fr.SAR.projet.election.Etat;
import fr.SAR.projet.message.Confirmation;
import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.message.Requete;
import fr.SAR.projet.message.ToSend;

import java.io.*;
import java.util.Vector;

import static java.lang.Thread.sleep;

public class ElectionLelann {
    ObjectOutputStream successor;
    ObjectInputStream predecessor;
    Etat etat;
    int chef;
    Vector<Integer> liste;
    int id;
    public ElectionLelann(int id, ObjectOutputStream out, ObjectInputStream in){
        successor = out;
        predecessor = in;
        etat = Etat.Repos;
        this.id = id;
        liste = new Vector<>();
    }

    public int leader(){
        liste = new Vector<>();
        if(etat == Etat.Repos){
            etat = Etat.en_cours;
            liste.addElement(id);
            envoyer_a(new Requete(id));
        }
        attendreLeader();
        return chef;
    }

    public void attendreLeader(){
        while(etat != Etat.termine){
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void envoyer_a(ToSend message){
        try {
            successor.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sur_reception_de(Requete req){
        if(etat == Etat.Repos){
            envoyer_a(req);
        }
        else if(id!=req.getSiteId()){
            //adding k in the list
            liste.addElement(req.getSiteId());
            envoyer_a(req);
        }
        else{
            if(id == getMin()){
                etat = Etat.termine;
                chef = id;
                envoyer_a(new Confirmation(chef));
            }
        }
    }
    public int getMin(){
        int res = liste.elementAt(0);
        for(int i = 1;i<liste.size();i++){
            res = (res>liste.get(i)? liste.get(i) : res);

        }
        return res;
    }

    public void sur_reception_de(Confirmation conf){
        if(id!=conf.getConf()){
            envoyer_a(conf);
            etat = Etat.termine;
            chef = conf.getConf();
        }
    }

    Runnable callSRD(){
        return new Runnable() {
            @Override
            public void run() {
                try {
                    while(etat != Etat.termine){
                        Object object = predecessor.readObject();
                        if(object != null){
                            if(object instanceof Confirmation){
                                Confirmation conf = (Confirmation) object;
                                sur_reception_de(conf);
                            }
                            else if(object instanceof Requete){
                                Requete req = (Requete) object;
                                sur_reception_de(req);
                            }
                        }
                        sleep(1000);
                    }
                }catch (Exception e){
                    System.err.println(e);
                }

            }
        };
    }


    public int initialize(boolean participate){
        Thread thSrd = new Thread(callSRD());
        thSrd.start();
        if(participate){
            id = leader();
        }

        try {
            thSrd.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return chef;
    }

    public void close(){
        predecessor = null;
        successor = null;
    }
}
