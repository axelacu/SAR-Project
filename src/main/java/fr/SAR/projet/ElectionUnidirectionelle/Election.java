package fr.SAR.projet.ElectionUnidirectionelle;

import fr.SAR.projet.message.Jeton;

import java.io.OutputStream;

public class Election {
    OutputStream successor;
    Etat etat;
    int identity;


    public Election(Etat etat, OutputStream successor){
        etat= Etat.Repos;
    }

    public boolean Sur_reception_De(Jeton jeton){
        return true;



    }
}
