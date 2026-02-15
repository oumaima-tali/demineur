package Modele.jeu;

import Modele.plateau.Case;
import Modele.plateau.Plateau;

public class Jeu extends Thread{
    private Plateau plateau;

    private boolean enCours = true;

    public Jeu() {
        plateau = new Plateau();
        plateau.setJeu(this);

        plateau.placerPieces();

        start();

    }

    public void stopJeu() {
        enCours = false;
        synchronized (this) {
            notify();
        }
    }
    public Plateau getPlateau() {
        return plateau;
    }

      
}