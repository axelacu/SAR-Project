package fr.SAR.projet.electionChang;

import fr.SAR.projet.ElectionUnidirectionelle.Etat;
import fr.SAR.projet.message.Confirmation;
import fr.SAR.projet.message.Requete;
import fr.SAR.projet.message.ToSend;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import static java.lang.Thread.sleep;

public class ElectionCR {
    ObjectOutputStream successor;
    ObjectInputStream predecessor;
    Etat etat;
    int chef;
    int id;
    public ElectionCR(int id, ObjectOutputStream out, ObjectInputStream in){
        successor = out;
        predecessor = in;
        etat = Etat.Repos;
        this.id = id;
    }
    public int leader(){
        if(etat == Etat.Repos){
            etat = Etat.en_cours;
            chef = id;
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
        if(etat == Etat.Repos || req.getSiteId()<chef){
            etat = Etat.en_cours;
            chef = req.getSiteId();
            envoyer_a(req);
        }
        else if(req.getSiteId() == id){
            etat = Etat.termine;
            envoyer_a(new Confirmation(id));
        }
    }
}
