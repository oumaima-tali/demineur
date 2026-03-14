package modele.jeu;

import modele.plateau.Plateau;
import modele.plateau.PlateauC;
import modele.plateau.PlateauH;

public class Jeu extends Thread{
    private Plateau plateau;

    private boolean enCours = true;
    private boolean perdu = false;
    private boolean gagne = false;

    public Jeu() {
        plateau = new PlateauC();
        plateau.setJeu(this);
        start();

    }
    public Jeu(String typeGrille) {
        if (typeGrille.equals("hexagonal")) {
            plateau = new PlateauH();
        } else {
            plateau = new PlateauC();
        }
        plateau.setJeu(this);
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

    public boolean isGagne() {
        return gagne;
    }

    public void gagner() {
        if (!enCours) {
            return;
        }
        gagne = true;
        stopJeu();
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