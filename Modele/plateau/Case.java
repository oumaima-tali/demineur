package demineur.Modele.plateau;

import Modele.jeu.Piece;

public class Case {
    public Piece p;
    protected Plateau plateau;

    public Case(Plateau _plateau) {

        plateau = _plateau;
    }

    public Piece getPiece() {
        return p;
    }
}