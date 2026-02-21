package Modele.jeu;

import Modele.plateau.Plateau;

public class Jeu extends Thread{
    private Plateau plateau;

    private boolean enCours = true;
    private boolean perdu = false;

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

    public boolean isEnCours() {
        return enCours;
    }

    public boolean isPerdu() {
        return perdu;
    }

    public void perdre() {
        if (!enCours) {
            return;
        }
        perdu = true;
        stopJeu();
    }

    public Plateau getPlateau() {
        return plateau;
    }

      
}