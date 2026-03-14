package modele.plateau;

import java.awt.Point;

/**
 * Plateau carré : chaque case a jusqu'à 8 voisins.
 * Utilise l'enum Direction pour parcourir les 8 directions.
 */
public class PlateauC extends Plateau {

    public static final int SIZE_X = 8;
    public static final int SIZE_Y = 13;

    public PlateauC() {
        super(SIZE_X, SIZE_Y);
    }

    @Override
    public int getSquareCellSize() {
        return 40;
    }

    @Override
    public Case[] getVoisins(Case c) {
        Point p = getPositionCase(c);
        int x = p.x;
        int y = p.y;

        Case[] voisins = new Case[Direction.values().length];
        int i = 0;

        for (Plateau.Direction dir : Plateau.Direction.values()) {
            int nx = x + dir.dx;
            int ny = y + dir.dy;
            if (nx >= 0 && nx < sizeX && ny >= 0 && ny < sizeY) {
                voisins[i++] = grilleCases[nx][ny];
            }
        }
        return voisins;
    }
}