package modele.plateau;

import java.awt.Point;
import modele.jeu.Difficulte;

// plateau hexagonal : chaque case a 6 voisins
// les directions changent selon si la ligne est paire ou impaire
public class PlateauH extends Plateau {

    // voisins pour une ligne paire (y % 2 == 0)
    private static final int[][] VOISINS_LIGNE_PAIRE = {
        {-1, 0}, {1, 0},   // gauche, droite
        {0, -1}, {0, 1},   // haut-centre, bas-centre
        {-1,-1}, {-1, 1}   // haut-gauche, bas-gauche
    };

    // voisins pour une ligne impaire
    private static final int[][] VOISINS_LIGNE_IMPAIRE = {
        {-1, 0}, {1, 0},
        {0, -1}, {0, 1},
        {1, -1}, {1, 1}    // haut-droite, bas-droite (decalage different)
    };

    public PlateauH(Difficulte d) {
        super(d.getLargeurHex(), d.getHauteurHex(), d.getMinesHex());
    }

    public PlateauH(int largeur, int hauteur, int nbMines) {
        super(largeur, hauteur, nbMines);
    }

    @Override
    public boolean isHexagonal() { return true; }

    @Override
    public Case[] getVoisins(Case c) {
        Point p = getPosition(c);
        int x = p.x;
        int y = p.y;

        int[][] dirs = (y % 2 == 0) ? VOISINS_LIGNE_PAIRE : VOISINS_LIGNE_IMPAIRE;
        Case[] voisins = new Case[6];
        int i = 0;

        for (int[] dir : dirs) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < largeur && ny >= 0 && ny < hauteur) {
                voisins[i] = grille[nx][ny];
            }
            i++;
        }
        return voisins;
    }
}