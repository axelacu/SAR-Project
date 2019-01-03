package fr.SAR.projet;

import fr.SAR.projet.ElectionUnidirectionelle.Election;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Define the context of th current problem
 */
public class Context {
    //Example --> {site1;site2;site3;site4}
    private static String[] context;
    private static int indexIp = 0;
    private static int indexPort = 1;
    static String regex = ":";
    static int portConsumer = 4010;

    /**
     * Return the port that will be use for the server of consumer.
     *
     * @return
     */
    public static int getportConsumer() {
        return portConsumer;
    }



    public static void setContext(String[] sites) {
        context = sites;
    }

    public static void setContext(String[] sites, String reg) {
        context = sites;
        regex = reg;
    }

    public static String[] getContext() {
        return context;
    }

    /**
     * Return the site given in parameter.
     *
     * @param id
     * @return
     */
    public static InetAddress getAddress(int id) {
        try {
            String site = context[id];
            String[] info = site.split(regex);

            return InetAddress.getByName(info[indexIp]);
        } catch (UnknownHostException e) {
            System.err.println("*** Impossible to find host ***");
            return null;
        }
    }

    /**
     * Return the port of the site given in parameter.
     *
     * @param id
     * @return
     */
    public static int getPort(int id) {
        String site = context[id];
        String[] info = site.split(regex);
        return Integer.parseInt(info[indexPort]);
    }

    /**
     * return the id of the successor.
     *
     * @param id
     * @return
     */
    public static int idSuccesseur(int id) {
        int size = context.length;
        return ((size - 1) == id ? 0 : id + 1);
    }

    /**
     * return the id for the predecessor.
     *
     * @param id
     * @return
     */
    public static int idPredecesseur(int id) {
        int size = context.length;
        return (0 == id ? size - 1 : id - 1);
    }
}
