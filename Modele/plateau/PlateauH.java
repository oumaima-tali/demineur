package modele.plateau;

import java.awt.Point;


/**
 * Plateau hexagonal : chaque case a jusqu'à 6 voisins.
 *
 *   Les lignes paires et impaires ont des décalages différents.
 *
 *   Voisins pour une ligne PAIRE (y pair) :
 *     (-1,0), (1,0), (0,-1), (0,1), (-1,-1), (-1,1)
 *
 *   Voisins pour une ligne IMPAIRE (y impair) :
 *     (-1,0), (1,0), (0,-1), (0,1), (1,-1), (1,1)
 */
public class PlateauH extends Plateau {

    public static final int SIZE_X = 9;
    public static final int SIZE_Y = 11;

    // Décalages des 6 voisins selon la parité de la ligne (y)
    private static final int[][] VOISINS_PAIRE   = { {-1,0},{1,0},{0,-1},{0,1},{-1,-1},{-1,1} };
    private static final int[][] VOISINS_IMPAIRE = { {-1,0},{1,0},{0,-1},{0,1},{ 1,-1},{ 1,1} };

    public PlateauH() {
        super(SIZE_X, SIZE_Y);
    }

    @Override
    public boolean isHexagonal() {
        return true;
    }

    @Override
    public int getHexRadius() {
        return 24;
    }

    @Override
    public int getGridPadding() {
        return 14;
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