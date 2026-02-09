package demineur.Modele.jeu;

import demineur.Modele.plateau.Case;
import demineur.Modele.plateau.Plateau;

import java.util.ArrayList;
import java.util.HashMap;


import java.awt.Point;

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