package fr.SAR.projet.producteurConsommateur;


import fr.SAR.projet.election.Etat;
import fr.SAR.projet.message.Message;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ThreadProducteur2 extends Thread{

    Socket se;
    String name;
    ObjectInputStream in;
    ObjectOutputStream out;
    Consommateur2 consommateur;

    Etat etat;
    final public Object monitorEtat = new Object();
    Thread threadReceiveMessage;

    public ThreadProducteur2(Socket socket, String name, Consommateur2 consommateur){
        try {
            this.se = socket;
            this.name = name;
            this.consommateur=consommateur;
            this.in = new ObjectInputStream(se.getInputStream());
            this.out = new ObjectOutputStream(se.getOutputStream());
            etat=Etat.en_cours;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Etat getEtat(){
        return this.etat;
    }

    public void setEtat(Etat etat) {
        System.out.println("je rentre dans set etat ");
         this.etat = etat;
            try{
                se.close();
                in.close();
                out.close();
            }catch(Exception e){
                e.printStackTrace();
            }



    }

    public Runnable receiveMessage(){
        return new Runnable() {
            @Override
            public void run() {
                try {

                        while (getEtat() == Etat.en_cours) {
                             Object object = in.readObject();
                                if (object != null) {
                                    if (object instanceof Message) {
                                        Message message = (Message) object;
                                        consommateur.Sur_Reception_De(message);
                                    }
                                }
                                sleep(1000);
                        }


                } catch(Exception e){
                    System.out.println("Vous ne pouvez plus recevoir de message car votre socket  a ete fermer");

                }
                System.err.println("Je sort de receiveMessage");
            }
        };
    }



    @Override
    public void run() {
        try {
            ArrayList<Thread> threads=new ArrayList<>();
            Thread srdMessage=new Thread(receiveMessage());
            threads.add(srdMessage);
            this.threadReceiveMessage=srdMessage;
            srdMessage.start();

            for(Thread th:threads){
                System.err.println("J'attends la fin de srdmessage");
                th.join();
            }
            System.out.println("Je ferme ma socket vers le consommateur");

        }
        catch (Exception e){
            System.err.println("Erreur : " +e);
        }
        finally{
            try{
                se.close(); in.close(); out.close();
            }
            catch (IOException e){}
        }
    }
}
