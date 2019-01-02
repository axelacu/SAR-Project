package fr.SAR.projet.serveurclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {
    public Socket sserv;
    InputStream in;
    OutputStream out;
    private InetAddress addr;
    private int port;

    /**
     * Create a client thread that will try to connect to server given in parameter.
     *
     * @param addr
     * @param port
     */
    public Client(InetAddress addr, int port) {
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
                    sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        } while (sserv == null);

        try {
            in = sserv.getInputStream();
            out = sserv.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * return true is the thread is connect to the server
     *
     * @return
     */
    public boolean connect() {
        return (sserv != null);
    }

    /**
     * Return the socket connect to the server.
     *
     * @return
     */
    public Socket getSserv() {
        return sserv;
    }

    public InputStream getInputStream() {
        return in;
    }

    public OutputStream getOutputStream() {
        return out;
    }

    /**
     * close
     */
    public void close() {
        try {
            sserv.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
