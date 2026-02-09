package demineur.Modele.plateau;

import Modele.jeu.Piece;

public class Case {

    private int valeur;
    private boolean visible = false;
    private boolean flagged = false;
    private int nbMinesAutour = 0;

    public int getValeur() {return valeur;}

    public boolean isVisible() {return visible;}

    public boolean isFlagged() {return flagged;}

    public int getNbMinesAutour() {return nbMinesAutour;}

    public void decouvrir() {visible = true;}



    protected Plateau plateau;

    public Case(Plateau _plateau) {

        plateau = _plateau;
    }

}


