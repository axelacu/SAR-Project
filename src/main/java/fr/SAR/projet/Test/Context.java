package fr.SAR.projet.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Context {
    //the --> site1;site2
    private static String[] context;
    private static int indexIp = 0;
    private static int indexPort = 1;
    static String regex = ":";
    static int portConsumer=4010;


    public static int getportConsumer(){
        return portConsumer;
    }

    public static void setContext(String[] sites){
        context = sites;
    }
    public static void setContext(String[] sites, String reg){
        context = sites;
        regex = reg;
    }

    public static String[] getContext() {
        return context;
    }

    public static InetAddress getAddress(int id){
        try {
            String site = context[id];
            String[] info = site.split(regex);

            return  InetAddress.getByName(info[indexIp]);
        } catch (UnknownHostException e) {
            System.err.println("*** Impossible to find host ***");
            return null;
        }
    }
    public static int getPort(int id){
        String site = context[id];
        String[] info = site.split(regex);
        return Integer.parseInt(info[indexPort]);
    }
    public static int idSuccesseur(int id){
        int size = context.length;
        return ((size - 1) == id ?  0 : id + 1);
    }
    public static int idPredecesseur(int id ){
        int size = context.length;
        return ( 0 == id ?  size - 1 : id - 1);
    }
}
