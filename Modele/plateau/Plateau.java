package Modele.plateau;

import java.awt.Point;
import java.util.HashMap;
import java.util.Observable;

import Modele.jeu.Jeu;

public class Plateau extends Observable {
    public static final int SIZE_X = 8 ;
    public static final int SIZE_Y = 13 ;
    public static final int NB_MINES = 20 ;
    private Jeu jeu;

    private HashMap<Case, Point> map = new  HashMap<Case, Point>();
    private Case[][] grilleCases = new Case[SIZE_X][SIZE_Y];

    public Plateau() {
        initPlateauVide();
    }

    public Case[][] getCases() {
        return grilleCases;
    }

    private void initPlateauVide() {
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                grilleCases[x][y] = new Case(this);
                map.put(grilleCases[x][y], new Point(x, y));
            }

        }

    }

    public Case[] getVoisins(Case c) {
        Point p = map.get(c);
        int x = p.x;
        int y = p.y;
        Case[] voisins = new Case[8];
        int i = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue; // skip the case itself
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < SIZE_X && ny >= 0 && ny < SIZE_Y) {
                    voisins[i++] = grilleCases[nx][ny];
                }
            }
        }
        return voisins;
    }

    public Point getPositionCase(Case c) {
        return map.get(c);
    }

    public void placerPieces() {
        placerMines();
        calculerValeurs();
    }

    

    public void setJeu(Jeu jeuObj) {
        this.jeu = jeuObj;
    }

    public void placerMines() {
        int minesPlacees = 0;
        while (minesPlacees < NB_MINES) {
            int x = (int) (Math.random() * SIZE_X);
            int y = (int) (Math.random() * SIZE_Y);
            if (!grilleCases[x][y].isMine()) {
                grilleCases[x][y].setMine(true);
                grilleCases[x][y].setValeur(-1);
                minesPlacees++;
            }
        }
    }

    private void calculerValeurs() {
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                Case c = grilleCases[x][y];
                if (!c.isMine()) {
                    // Compter les mines adjacentes
                    int minesAutour = 0;
                    Case[] voisins = getVoisins(c);
                    for (Case voisin : voisins) {
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
        if (jeu != null && !jeu.isEnCours()) {
            return;
        }

        // Si la case est déjà visible ou a un drapeau, on ne fait rien
        if (c.isVisible() || c.isFlagged()) {
            return;
        }
        
        // Découvrir la case
        c.decouvrir();

        if (c.isMine()) {
            decouvrirToutesLesMines();
            if (jeu != null) {
                jeu.perdre();
            }
            return;
        }
        
        // Si la valeur est 0, découvrir récursivement les voisins
        if (c.getValeur() == 0) {
            Case[] voisins = getVoisins(c);
            for (Case voisin : voisins) {
                if (voisin != null) {
                    decouvrirCase(voisin);
                }
            }
        }
    }

    private void decouvrirToutesLesMines() {
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
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

}