package fr.SAR.projet.producteurConsommateur;
import java.util.*;
import java.net.*;
import java.io.*;

public class Consommateur {
    private int N;
    String[] T;
    int inc;
    int outc;
    int NbmessC;

    public Consommateur(int N){
        this.N=N;
        T=new String[N];
        inc=0;
        outc=0;
        NbmessC=0;
    }

    public static void main(String[] args){
        int port=4020;
        ServerSocket se;
        Socket ssv=null;
        final PrintWriter out;
        BufferedReader in;
        final Consommateur consommateur=new Consommateur(10);

        try{
            InetAddress test = InetAddress.getByName("25.46.150.102");// ne fait pas partie du tp
            InetSocketAddress val = new InetSocketAddress(test,port);//ne fait pas partie du TP
            se = new ServerSocket();
            se.bind(val); // ne fait pas partie du tP.
            System.out.println("Le serveur est à l'écoute " + "dans l'InetAdress suivante : " + se.getInetAddress());
            ssv =se.accept();
            System.out.println("Demande de connexion acceptée");
            in = new BufferedReader(new InputStreamReader(ssv.getInputStream()));
            out = new PrintWriter(ssv.getOutputStream(),true);

            Thread consommer=new Thread() {
                @Override
                public void run() {
                    while(consommateur.NbmessC>0){
                        System.out.println("Je reçois le message: "+consommateur.T[consommateur.outc]);
                        consommateur.outc=(consommateur.outc+1)%consommateur.N;
                        consommateur.NbmessC--;
                        out.println("Acq");
                    }
                }
            };
            consommer.start();
            while (true) {

                String req = in.readLine();
                System.out.println(req);
                consommateur.sur_Reception_De(ssv.getInetAddress(),req);
            }


        }catch (IOException e){
            System.err.println("Erreur : " +e);
        }
    }

    public boolean sur_Reception_De(InetAddress inetAddress,String message){
        try {
            T[inc] = message;
            inc = (inc + 1) % N;
            NbmessC++;
        } catch(Exception e){
            System.out.println("Erreur SRD Conso");
            e.printStackTrace();
        }
        return true;
    }
}
