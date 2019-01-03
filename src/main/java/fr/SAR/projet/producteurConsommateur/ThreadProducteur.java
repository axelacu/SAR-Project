package fr.SAR.projet.producteurConsommateur;

import fr.SAR.projet.message.Message;

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
            this.in = new ObjectInputStream(se.getInputStream());
            this.out = new ObjectOutputStream(se.getOutputStream());
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
                            if(object instanceof Message) {
                                Message message = (Message) object;
                                consommateur.Sur_Reception_De(message);
                            }
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
