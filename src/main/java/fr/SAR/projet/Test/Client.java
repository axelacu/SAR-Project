package fr.SAR.projet.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {
    public Socket sserv;
    private InetAddress addr;
    private int port;
    InputStream in;
    OutputStream out;
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

        try{
            in = sserv.getInputStream();
            out = sserv.getOutputStream();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean connect(){
        return (sserv != null);
    }

    public Socket getSserv() {
        return sserv;
    }
    public InputStream getInputStream(){
        return in;
    }
    public OutputStream getOutputStream(){
        return out;
    }
}
