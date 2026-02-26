package modele.plateau;

import java.awt.Point;

/**
 * Plateau carré : chaque case a jusqu'à 8 voisins "8 directions".
 */
public class PlateauC extends Plateau {

    public static final int SIZE_X = 8;
    public static final int SIZE_Y = 13;

    public PlateauC() {
        super(SIZE_X, SIZE_Y);
    }

    @Override
    public Case[] getVoisins(Case c) {
        Point p = getPositionCase(c);
        int x = p.x;
        int y = p.y;

        Case[] voisins = new Case[8];
        int i = 0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < sizeX && ny >= 0 && ny < sizeY) {
                    voisins[i++] = grilleCases[nx][ny];
                }
            }
        }
        return voisins;
    }
}