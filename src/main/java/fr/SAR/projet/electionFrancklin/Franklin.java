package fr.SAR.projet.electionFrancklin;

import fr.SAR.projet.Context;
import fr.SAR.projet.ElectionUnidirectionelle.Etat;
import fr.SAR.projet.message.Confirmation;
import fr.SAR.projet.message.Requete;
import fr.SAR.projet.message.ToSend;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static java.lang.Thread.sleep;

public class Franklin {
    ObjectOutputStream outSuivant;
    ObjectInputStream inSuivant;
    ObjectInputStream inPrecedent;
    ObjectOutputStream outPrecedent;
    Etat etat;
    int nbreq;
    int conc;
    boolean dir;
    int concav;
    int chef;
    int id;
    final Object monitor = new Object();
    public Franklin(int id, ObjectOutputStream outS, ObjectInputStream inP,ObjectOutputStream outP, ObjectInputStream inS){
        this.id = id;
        outSuivant = outS;
        outPrecedent = outP;
        inSuivant = inS;
        inPrecedent = inP;
        etat = Etat.Repos;
    }

    public int leader(){
        if(etat == Etat.Repos){
            synchronized (monitor) {
                etat = Etat.en_cours;
                chef = id;
                concav = id;
            }
            do{
                nbreq = 0;
                if(concav!=id){
                    synchronized (monitor){
                        nbreq = 1;
                        conc = concav;
                        if(conc<chef){
                            chef = conc;
                        }
                        concav = id;
                    }
                }
                envoyer_a(outSuivant, new Requete(id,id));
                envoyer_a(outPrecedent,new Requete(id,id));
                attendreLeader1();
            }while(etat == Etat.en_cours);
            if(concav!=id){
                if(dir){
                    envoyer_a(outSuivant,new Requete(concav,id));
                }else{
                    envoyer_a(outPrecedent,new Requete(concav,id));
                }
            }
        }

        attendreLeader2();

        return chef;
    }
    public void attendreLeader1(){
        while(nbreq != 2){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void attendreLeader2(){
        while(etat != Etat.termine){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public synchronized void envoyer_a(ObjectOutputStream out,ToSend message){
        try {
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sur_reception_de(Requete requete){
        if(etat == Etat.Repos || requete.getSiteId()< chef){
            chef = requete.getSiteId();
        }
        if(etat == Etat.en_cours){
            if(nbreq == 0){
                conc = requete.getSiteId();
                nbreq = 1;
                dir = (requete.getSender() == Context.idPredecesseur(id));
            }
            else if((dir && (requete.getSender() == Context.idPredecesseur(id)))
                        ||(!dir && requete.getSender() != Context.idPredecesseur(id) )){
                concav = requete.getSiteId();
            }else{
                nbreq = 2;
                if(chef<id){
                    etat = Etat.attente;

                }else if(conc == id || requete.getSiteId() == conc){
                    etat = Etat.termine;
                    envoyer_a(outSuivant,new Confirmation(id));
                }

             }
        }else{
            etat = Etat.attente;
            if(requete.getSender() == Context.idPredecesseur(id)){
                envoyer_a(outSuivant,requete);
            }else{
                envoyer_a(outPrecedent,requete);
            }
        }
    }

    public synchronized void sur_reception_de(Confirmation conf){
        if(id !=conf.getConf()){
            envoyer_a(outPrecedent,conf);
            etat = Etat.termine;
        }
    }

    Runnable callSRD(final ObjectInputStream input){
        return new Runnable() {
            @Override
            public void run() {
                callingHelper(input);
            }
        };
    }

    public void callingHelper(ObjectInputStream input){
        try {
            while(etat != Etat.termine){
                Object object = input.readObject();
                if(object != null){

                    if(object instanceof Confirmation){
                        System.out.println("Confirmation recu.");
                        Confirmation conf = (Confirmation) object;
                        sur_reception_de(conf);
                    }
                    if(object instanceof Requete){
                        System.out.println("Requete recu.");
                        Requete req = (Requete) object;
                        sur_reception_de(req);
                    }
                }
            }
        }catch (Exception e){
            System.err.println(e);
        }
    }

    public int initialize(boolean participate){
        Thread thSrdS = new Thread(callSRD(inPrecedent));
        Thread thSrdP = new Thread(callSRD(inSuivant));
        thSrdP.start();
        thSrdS.start();

        if(participate){
            id = leader();
        }
        try {
            thSrdP.join();
            thSrdS.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return chef;
    }
}
