package fr.SAR.projet.Test;

import java.io.IOException;
import java.net.*;

public class Serveur extends Thread {
    int port;
    InetAddress hote;
    ServerSocket se;

    public Serveur(InetAddress addr, int port){
        //Host
        InetSocketAddress address = new InetSocketAddress(addr, port);
        //Open server
        try {
            se = new ServerSocket();
            se.bind(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("** Le Serveur est à l'écoute. **");
    }

    public Socket ajoutClient(){
        Socket ssv = null;
        try {
            ssv = se.accept();
        } catch (IOException e) {
            System.err.println(" *** Failing to adding client *** ");
        }
        return ssv;
    }
}
