package fr.SAR.projet.serveurclient;

import java.io.IOException;
import java.net.*;

public class Serveur extends Thread {
    int port;
    InetAddress hote;
    ServerSocket se;

    /**
     * Create a server binding to the address giving in paremeter.
     *
     * @param addr
     * @param port
     */
    public Serveur(InetAddress addr, int port) {
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

    /**
     * Wait from a client to connect in the server.
     *
     * @return
     */
    public Socket ajoutClient() {
        Socket ssv = null;
        try {
            ssv = se.accept();
        } catch (IOException e) {
            System.err.println(" *** Failing to adding client *** ");
        }
        return ssv;
    }

    /**
     * close the server.
     */
    public void close() {
        try {
            se.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
