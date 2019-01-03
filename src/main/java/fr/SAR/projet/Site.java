package fr.SAR.projet;

import fr.SAR.projet.message.ToSend;
import fr.SAR.projet.serveurclient.Client;
import fr.SAR.projet.serveurclient.Serveur;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class Site {
    boolean consumer = true;
    private int id;
    private Socket successor;
    private Client predecesseur;
    private Serveur serveur;
    private OutputStream outSuccessor;
    private InputStream inPredecessor;
    private InputStream inSuccesor;
    private OutputStream outPrecessor;
    private int idPred;

    private ObjectInputStream oInPredecessor;
    private ObjectOutputStream oOutSucessor;
    private ObjectOutputStream oOutPredecessor;
    private ObjectInputStream oInSucessor;



    /**
     * Permet de creer un site qui heberge un serveur pour un successeur et qui se connecte à predecesseur.
     *
     * @param id
     */
    public Site(int id) {

        this.id = id;
        //open server
        Serveur serveur = new Serveur(Context.getAddress(id), Context.getPort(id));
        this.serveur = serveur;
        //connecting to client
        int idPred = Context.idPredecesseur(id);
        predecesseur = new Client(Context.getAddress(idPred), Context.getPort(idPred));
        predecesseur.start();

        //waiting from successor;
        System.out.println("Waiting two side connection... ");
        successor = serveur.ajoutClient();
        while (!predecesseur.connect()) {
            try {
                System.out.println("Waiting predecessor...");
                sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            outSuccessor = successor.getOutputStream();
            outPrecessor = predecesseur.getOutputStream();
            inSuccesor = successor.getInputStream();
            inPredecessor = predecesseur.getInputStream();
            oOutSucessor = new ObjectOutputStream(outSuccessor);
            oInPredecessor = new ObjectInputStream(inPredecessor);
            oOutPredecessor = new ObjectOutputStream(outPrecessor);
            oInSucessor = new ObjectInputStream(inSuccesor);




        } catch (IOException e) {
            System.err.println("Error connecting the Input and outpur Stream");
        }



        System.out.println("*** Well DONE connection established ***");
    }

    /**
     * Permet d'avoir l'inetAddress du site.
     *
     * @return
     */
    public InetAddress getAdress() {
        return Context.getAddress(id);
    }

    public Socket getSuccessor() {
        return successor;
    }

    public Socket getPredecesseur() {
        return predecesseur.getSserv();
    }


    public void setConsumer(boolean consumer) { //à appeler pour les producteurs
        this.consumer = consumer;
    }

    /**
     * give the id of the site
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Give the input stream of the predecessor.
     * <p>
     * It used for the token and for the election
     *
     * @return
     */
    public InputStream getInPredecessor() {
        return inPredecessor;
    }

    /**
     * Give the output stream of the successor.
     * <p>
     * It used for the token and the election.
     *
     * @return
     */
    public OutputStream getOutSuccessor() {
        return outSuccessor;
    }

    /**
     * Get the input stream of the successor it's will be usefull for bidirectional election.
     *
     * @return
     */
    public InputStream getInSuccesor() {
        return inSuccesor;
    }

    /**
     * return true is the site is a consumer.
     * @return
     */
    public boolean isConsumer() {
        return consumer;
    }

    /**
     * Close the current site.
     */
    public void close() {
        try {
            successor.close();
            predecesseur.close();
            serveur.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public int getIdPred() {
		return idPred;
	}

	public void setIdPred(int idPred) {
		this.idPred = idPred;
	}
    public ObjectInputStream getoInPredecessor() {
        return oInPredecessor;
    }

    public ObjectOutputStream getoOutSucessor() {
        return oOutSucessor;
    }

    public ObjectInputStream getoInSucessor() {
        return oInSucessor;
    }

    public ObjectOutputStream getoOutPredecessor() {
        return oOutPredecessor;
    }
}
