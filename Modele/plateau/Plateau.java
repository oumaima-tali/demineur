package modele.plateau;

import java.awt.Point;
import java.util.HashMap;
import java.util.Observable;
import modele.jeu.Jeu;

public abstract class Plateau extends Observable {

    public static final int NB_MINES = 20;

    public int casesDecouvertes = 0;

    protected final int sizeX;
    protected final int sizeY;
    protected final int nbCases;

    protected Jeu jeu;
    protected HashMap<Case, Point> map = new HashMap<>();
    protected Case[][] grilleCases;

    public Plateau(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.nbCases = sizeX * sizeY;
        this.grilleCases = new Case[sizeX][sizeY];
        initPlateauVide();
    }

    // -------------------------------------------------------
    // Méthodes communes
    // -------------------------------------------------------

    private void initPlateauVide() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                grilleCases[x][y] = new Case(this);
                map.put(grilleCases[x][y], new Point(x, y));
            }
        }
    }

    public int getSizeX() { return sizeX; }
    public int getSizeY() { return sizeY; }

    public Case[][] getCases() {
        return grilleCases;
    }

    public Point getPositionCase(Case c) {
        return map.get(c);
    }

    public void setJeu(Jeu jeuObj) {
        this.jeu = jeuObj;
    }

    public void placerPieces() {
        placerMines();
        calculerValeurs();
    }

    public void placerMines() {
        int minesPlacees = 0;
        while (minesPlacees < NB_MINES) {
            int x = (int) (Math.random() * sizeX);
            int y = (int) (Math.random() * sizeY);
            if (!grilleCases[x][y].isMine()) {
                grilleCases[x][y].setMine(true);
                grilleCases[x][y].setValeur(-1);
                minesPlacees++;
            }
        }
    }

    private void calculerValeurs() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Case c = grilleCases[x][y];
                if (!c.isMine()) {
                    int minesAutour = 0;
                    for (Case voisin : getVoisins(c)) {
                        if (voisin != null && voisin.isMine()) {
                            minesAutour++;
                        }
                    }
                    c.setValeur(minesAutour);
                }
            }
        }
    }

    public void decouvrirCase(Case c) {
        if (jeu != null && !jeu.isEnCours()) return;
        if (c.isVisible() || c.isFlagged()) return;

        c.decouvrir();
        casesDecouvertes++;

        if (c.isMine()) {
            decouvrirToutesLesMines();
            if (jeu != null) jeu.perdre();
            return;
        }

        if (casesDecouvertes == nbCases - NB_MINES) {
            if (jeu != null) jeu.gagner();
        }

        if (c.getValeur() == 0) {
            for (Case voisin : getVoisins(c)) {
                if (voisin != null) decouvrirCase(voisin);
            }
        }
    }

    private void decouvrirToutesLesMines() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Case caseCourante = grilleCases[x][y];
                if (caseCourante.isMine()) {
                    caseCourante.decouvrirForce();
                }
            }
        }
    }

    public void notifierObservateurs() {
        setChanged();
        notifyObservers();
    }

    // -------------------------------------------------------
    // Méthode abstraite : chaque type de plateau définit ses voisins
    // -------------------------------------------------------

    public abstract Case[] getVoisins(Case c);
}