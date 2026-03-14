package modele.plateau;

import java.awt.Point;
import modele.jeu.Difficulte;



public class PlateauH extends Plateau {

    private static final int[][] VOISINS_PAIRE   = { {-1,0},{1,0},{0,-1},{0,1},{-1,-1},{-1,1} };
    private static final int[][] VOISINS_IMPAIRE = { {-1,0},{1,0},{0,-1},{0,1},{ 1,-1},{ 1,1} };

    public PlateauH() {
        this(Difficulte.FACILE);
    }

    public PlateauH(Difficulte difficulte) {
        super(difficulte.getHexSizeX(), difficulte.getHexSizeY(), difficulte.getHexNbMines());
    }

    @Override
    public boolean isHexagonal() {
        return true;
    }

    @Override
    public Case[] getVoisins(Case c) {
        Point p = getPositionCase(c);
        int x = p.x;
        int y = p.y;

        int[][] directions = (y % 2 == 0) ? VOISINS_PAIRE : VOISINS_IMPAIRE;
        Case[] voisins = new Case[6];
        int i = 0;

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < sizeX && ny >= 0 && ny < sizeY) {
                voisins[i++] = grilleCases[nx][ny];
            }
        }
        return voisins;
    }
}