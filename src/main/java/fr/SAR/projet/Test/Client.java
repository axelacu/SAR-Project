package fr.SAR.projet.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {
    public Socket sserv;
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
                this.sserv = new Socket(addr, port);
            } catch (IOException e) {
                this.sserv = null;
                try {
                    sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }while (sserv == null);
    }

    public boolean connect(){
        return (sserv != null);
    }

    public Socket getSserv() {
        return sserv;
    }
}
