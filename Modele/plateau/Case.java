package Modele.plateau;


public class Case {

    private int valeur;
    private boolean visible = false;
    private boolean flagged = false;
    private int nbMinesAutour = 0;
    private boolean mine = false;

    public int getValeur() {return valeur;}

    public boolean isVisible() {return visible;}

    public boolean isFlagged() {return flagged;}

    public int getNbMinesAutour() {return nbMinesAutour;}

    public void decouvrir() {
        if (!flagged) {
            visible = true;
        }
    }
    
    public boolean isMine() {return mine;}

    public void setMine(boolean _mine) {mine = _mine;}

    public void setValeur(int _valeur) {valeur = _valeur;}

    public void toggleFlag() {
        if (!visible) {
            flagged = !flagged;
        }
    }


    protected Plateau plateau;

    public Case(Plateau _plateau) {

        plateau = _plateau;
    }

}


