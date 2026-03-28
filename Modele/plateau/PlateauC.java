package modele.plateau;

import java.awt.Point;
import modele.jeu.Difficulte;

// plateau de jeu avec une grille carree (8 voisins par case)
public class PlateauC extends Plateau {

    // les 8 directions possibles pour une grille carree
    private static final int[][] DIRECTIONS = {
        {-1, -1}, {0, -1}, {1, -1},
        {-1,  0},           {1,  0},
        {-1,  1}, {0,  1}, {1,  1}
    };

    public PlateauC(Difficulte d) {
        super(d.getLargeurCarre(), d.getHauteurCarre(), d.getMinesCarre());
    }

    public PlateauC(int largeur, int hauteur, int nbMines) {
        super(largeur, hauteur, nbMines);
    }

    @Override
    public Case[] getVoisins(Case c) {
        Point p = getPosition(c);
        Case[] voisins = new Case[DIRECTIONS.length];
        int i = 0;

        for (int[] dir : DIRECTIONS) {
            int nx = p.x + dir[0];
            int ny = p.y + dir[1];
            if (nx >= 0 && nx < largeur && ny >= 0 && ny < hauteur) {
                voisins[i] = grille[nx][ny];
            }
            i++;
        }
        return voisins;
    }
}