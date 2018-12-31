package fr.SAR.projet.producteurConsommateur.consommateur;

import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.message.Message;
import fr.SAR.projet.message.ToSend;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ThreadProducteur extends Thread{

    Socket se;
    String name;
    ObjectInputStream in;
    ObjectOutputStream out;
    Consommateur consommateur;

    public ThreadProducteur(Socket socket,String name,Consommateur consommateur){
        try {
            this.se = socket;
            this.name = name;
            this.consommateur=consommateur;
            in = new ObjectInputStream(se.getInputStream());
            out = new ObjectOutputStream(se.getOutputStream());
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public Runnable receiveMessage(){
        return new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        Object object = in.readObject();
                        if(object!=null){
                            Message message = (Message) object;
                            System.out.println("**** Un message a ete recu ****");
                            consommateur.Sur_Reception_De(message);
                        }
                        sleep(1000);
                    }

                } catch(Exception e){
                    System.out.println("Erreur dans l'attente d'un message");
                    e.printStackTrace();
                }
            }
        };
    }




    @Override
    public void run() {
        try {
            System.out.println("un producteur se lance ");
            ArrayList<Thread> threads=new ArrayList<>();



            Thread srdMessage=new Thread(receiveMessage());
            threads.add(srdMessage);
            srdMessage.start();

            for(Thread th:threads){
                th.join();
            }


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
