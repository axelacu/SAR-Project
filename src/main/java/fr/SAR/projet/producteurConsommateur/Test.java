package fr.SAR.projet.producteurConsommateur;

import fr.SAR.projet.message.Jeton;
import fr.SAR.projet.message.ToSend;
import fr.SAR.projet.producteurConsommateur.consommateur.ThreadProducteur;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Test {
    public static void main(String[] args){
        int port = 4020;
        try {
            InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("25.46.130.120"), port);
            ServerSocket se = new ServerSocket();
            se.bind(address);
            Socket soc=se.accept();
            InputStream in = soc.getInputStream();
            ObjectInputStream oin = new ObjectInputStream(in);
            Jeton jeton = (Jeton) oin.readObject();
            System.out.println("L'objet a été reçu  : val = " + jeton.getVal());
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
