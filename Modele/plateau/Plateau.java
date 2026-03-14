package modele.plateau;

import java.awt.Point;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Observable;
import java.util.Set;
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
    protected boolean minesInitialisees = false;

    public Plateau(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.nbCases = sizeX * sizeY;
        this.grilleCases = new Case[sizeX][sizeY];
        initPlateauVide();
    }


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

    public Jeu getJeu() {
        return jeu;
    }

    public void setJeu(Jeu jeuObj) {
        this.jeu = jeuObj;
    }

    public void placerPieces() {
        placerMinesAvecZoneInterdite(null);
        calculerValeurs();
        minesInitialisees = true;
    }

    public void placerMines() {
        placerMinesAvecZoneInterdite(null);
    }

    private void placerMinesAvecZoneInterdite(Case premiereCaseCliquee) {
        Set<Case> casesInterdites = new HashSet<>();

        if (premiereCaseCliquee != null) {
            casesInterdites.add(premiereCaseCliquee);
            for (Case voisin : getVoisins(premiereCaseCliquee)) {
                if (voisin != null) {
                    casesInterdites.add(voisin);
                }
            }

            int casesDisponibles = nbCases - casesInterdites.size();
            if (casesDisponibles < NB_MINES) {
                casesInterdites.clear();
                casesInterdites.add(premiereCaseCliquee);
            }
        }

        int minesPlacees = 0;
        while (minesPlacees < NB_MINES) {
            int x = (int) (Math.random() * sizeX);
            int y = (int) (Math.random() * sizeY);
            if (!grilleCases[x][y].isMine() && !casesInterdites.contains(grilleCases[x][y])) {
                grilleCases[x][y].setMine(true);
                grilleCases[x][y].setValeur(-1);
                minesPlacees++;
            }
        }
    }

    private void initialiserMinesAuPremierClic(Case premiereCaseCliquee) {
        if (minesInitialisees) {
            return;
        }
        placerMinesAvecZoneInterdite(premiereCaseCliquee);
        calculerValeurs();
        minesInitialisees = true;
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

    initialiserMinesAuPremierClic(c);

    c.decouvrir(); 

    if (!c.isMine()) {
        casesDecouvertes++;
        if (casesDecouvertes == nbCases - NB_MINES) {
            if (jeu != null) jeu.gagner();
        }
    }
}
    
    void decouvrirToutesLesMines() {
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

<<<<<<< HEAD
    public boolean isHexagonal() {
        return false;
    }

    public int getSquareCellSize() {
        return 40;
    }

    public int getHexRadius() {
        return 24;
    }

    public int getGridPadding() {
        return 14;
    }

    // -------------------------------------------------------
    // Méthode abstraite : chaque type de plateau définit ses voisins
    // -------------------------------------------------------

=======
    
>>>>>>> 93e5f125277e9663af1312814d4c55827ffadd25
    public abstract Case[] getVoisins(Case c);

    
    public enum Direction {

        NORD      ( 0, -1),
        NORD_EST  ( 1, -1),
        EST       ( 1,  0),
        SUD_EST   ( 1,  1),
        SUD       ( 0,  1),
        SUD_OUEST (-1,  1),
        OUEST     (-1,  0),
        NORD_OUEST(-1, -1);

        public final int dx;
        public final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }
}