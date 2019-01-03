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
    Object forWait=new Object();

    public Election(int id, OutputStream successor, InputStream predecessor,boolean initiator){
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
        System.out.println(etat);
        try {
            if (etat == Etat.Repos) {
                etat = Etat.en_cours;
                this.identity = this.id;
                Requete requete = new Requete(this.id);
                envoyer_a(successor, requete);
                Thread thread =new Thread(attendre_terminer());
                thread.start();

            }
        }catch(Exception e) {
            System.out.println("probleme leader");
            e.printStackTrace();
        }

    }

    public Runnable attendre_terminer(){
        return new Runnable() {
            @Override
            public void run() {
                synchronized (forWait) {
                    while (etat != Etat.termine) {
                        try {
                            forWait.wait();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

        };

    }

    public void sur_reception_de(ToSend toSend){
        synchronized (forWait) {
            try {
                sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (toSend instanceof Confirmation) {
                System.out.println("Je recois une confirmation" + this.identity);
                if (this.id != ((Confirmation) toSend).getConf()) {

                    envoyer_a(successor, toSend);
                    this.etat = etat.termine;
                    forWait.notify();
                    System.out.println(etat);

                } else {
                    return;
                }
            }
            if (toSend instanceof Requete) {

                System.out.println("Je recois une requete");
                if (etat == etat.Repos || ((Requete) toSend).getSiteId() < this.identity) {
                    System.out.println("Je suis dedans, mon etat va changer");
                    System.out.println(etat);
                    System.out.println("Mon identite est " + this.identity);
                    System.out.println("Le numero de la requete est" + ((Requete) toSend).getSiteId());
                    this.etat = etat.en_cours;
                    this.identity = ((Requete) toSend).getSiteId();
                    envoyer_a(successor, toSend);
                } else {
                    if (this.id == ((Requete) toSend).getSiteId()) {
                        System.out.println("JE suis ici mon etat va se terminer");
                        etat = etat.termine;
                        forWait.notify();
                        System.out.println(etat);
                        envoyer_a(successor, new Confirmation(this.id));
                    }
                }
            }
        }

    }




    public Runnable callSRD(){
        return new Runnable() {
            @Override
            public void run() {
                try {

                       do {
                            System.out.println("Je recois un message");
                            Object object = predecessor.readObject();
                            if (object != null) {
                                ToSend message = (ToSend) object;
                                sur_reception_de(message);
                                sleep(1000);
                            }
                            sleep(1000);
                        } while (etat!= etat.termine);
                    if(id==identity) {
                            System.out.println("Je sort de srd avec un etat: " + etat);
                            Object object = predecessor.readObject();
                            if (object != null) {
                                ToSend message = (ToSend) object;
                                sur_reception_de(message);
                                sleep(1000);
                            }
                        }
                    sleep(1000);

                }catch (Exception e){
                    System.err.println(e);
                    e.printStackTrace();
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
                if(content instanceof Confirmation) {
                    System.out.println("J'envoie la confirmation "+((Confirmation) content).getConf());
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int initializeElection() {
        Thread srd = new Thread(callSRD());
        if (this.initiator) {
            Leader();
        }
        srd.start();

        try {
            srd.join();
            System.out.println("Je sors de l'election");
        } catch (Exception e) {
            System.out.println("Probleme Leader");
            e.printStackTrace();
        }

        return this.identity;

    }

}
