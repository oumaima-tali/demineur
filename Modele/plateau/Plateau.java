package demineur.Modele.plateau;

import java.awt.Point;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class Plateau extends Observable {
    public static final int SIZE_X = 8 ;
    public static final int SIZE_Y = 13 ;

    private Jeu jeu;
    public void setJeu(Jeu j) { this.jeu = j; }
    public Jeu getJeu() { return jeu; }


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

    public Point getPositionCase(Case c) {
        return map.get(c);
    }


    public void notifierObservateurs() {
        setChanged();
        notifyObservers();
    }

}