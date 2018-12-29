package fr.SAR.projet.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {
    public Socket predecesseur;
    private InetAddress addr;
    private int port;
    public Client(InetAddress addr, int port){
      this.addr = addr;
      this.port = port;
    }

    @Override
    public void run() {
        do {
            try {
                this.predecesseur = new Socket(addr, port);
            } catch (IOException e) {
                this.predecesseur = null;
                try {
                    sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }while (predecesseur == null);
    }

    public boolean connect(){
        return (predecesseur != null);
    }
}
