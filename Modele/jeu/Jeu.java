package modele.jeu;

import modele.plateau.Plateau;
import modele.plateau.PlateauC;
import modele.plateau.PlateauH;

public class Jeu extends Thread {

    private Plateau plateau;

    private boolean enCours = true;
    private boolean perdu   = false;
    private boolean gagne   = false;

    // Temps de jeu 
    private int temps = 0;

    public Jeu() {
        this("carre", Difficulte.FACILE);
    }

    public Jeu(String typeGrille) {
        this(typeGrille, Difficulte.FACILE);
    }

    public Jeu(String typeGrille, Difficulte difficulte) {
        if (typeGrille.equals("hexagonal")) {
            plateau = new PlateauH(difficulte);
        } else {
            plateau = new PlateauC(difficulte);
        }
        plateau.setJeu(this);
        start();
    }

    // Gestion du jeu 

    public void stopJeu() {
        enCours = false;
        synchronized (this) {
            notify();
        }
    }

    public boolean isEnCours() { return enCours; }
    public boolean isPerdu()   { return perdu;   }
    public boolean isGagne()   { return gagne;   }

    public void gagner() {
        if (!enCours) return;
        gagne = true;
        stopJeu();
    }

    public void perdre() {
        if (!enCours) return;
        perdu = true;
        stopJeu();
    }

    // Temps 

    public int getTemps() { return temps; }

    public void incrementerTemps() {
        if (enCours) {
            temps = Math.min(temps + 1, 999);
        }
    }

    // Actions de clic 

    /**
     * Clic gauche sur la case (x, y).
     * Si la case est déjà visible → découverte des cases adjacentes.
     * Sinon → découverte normale.
     */
    public void clicGauche(int x, int y) {
        if (!enCours) return;
        modele.plateau.Case c = plateau.getCases()[x][y];
        if (c.isVisible()) {
            plateau.decouvrirCasesAdjacentes(c);
        } else {
            plateau.decouvrirCase(c);
        }
        plateau.notifierObservateurs();
    }

    /**
     * Clic droit sur la case (x, y) → bascule le drapeau.
     */
    public void clicDroit(int x, int y) {
        if (!enCours) return;
        plateau.getCases()[x][y].toggleFlag();
        plateau.notifierObservateurs();
    }


    
    public Plateau getPlateau() { return plateau; }
}