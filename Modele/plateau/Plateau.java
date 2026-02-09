package demineur.Modele.plateau;

import java.awt.Point;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class Plateau extends Observable {
    public static final int SIZE_X = 8 ;
    public static final int SIZE_Y = 13 ;
    public static final int NB_MINES = 20 ;

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

    public getVoisins(Case c) {
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



    public void notifierObservateurs() {
        setChanged();
        notifyObservers();
    }

}