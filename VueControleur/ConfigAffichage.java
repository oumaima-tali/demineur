package VueControleur;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import modele.jeu.Difficulte;

public class ConfigAffichage {

    private static final int GRID_PADDING = 14;

    // Taille minimale et maximale d'une cellule carrée (en pixels)
    private static final int CELL_MIN = 24;
    private static final int CELL_MAX = 70;

    // Marges réservées pour la barre du haut (timer/smiley) + barre du bas + bords
    private static final int RESERVE_HAUTEUR = 160;
    private static final int RESERVE_LARGEUR  = 40;

    // Rayon des hexagones (px) par niveau de difficulté
    private static final int[] HEX_RADII = { 26, 22, 19 };

    private final int cellSize;
    private final int hexRadius;
    private final int gridPadding;

    public ConfigAffichage(Difficulte difficulte, boolean hexagonal) {
        this.gridPadding = GRID_PADDING;
        int idx = difficulte.ordinal();
        this.hexRadius = HEX_RADII[idx];

        if (hexagonal) {
            this.cellSize = hexRadius * 2;
        } else {
            this.cellSize = calculerTailleCellule(difficulte);
        }
    }

    /**
     * Calcule la taille de cellule optimale pour que la grille tienne dans l'écran.
     * On prend le min entre ce que permet la largeur et ce que permet la hauteur.
     */
    private int calculerTailleCellule(Difficulte difficulte) {
        // Taille de l'écran disponible (retire la barre des tâches macOS/Windows)
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension ecran = toolkit.getScreenSize();
        Insets insets = toolkit.getScreenInsets(
            java.awt.GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
        );

        int largeurDispo = ecran.width  - insets.left - insets.right  - RESERVE_LARGEUR;
        int hauteurDispo = ecran.height - insets.top  - insets.bottom - RESERVE_HAUTEUR;

        int colonnes = difficulte.getSquareSizeX();
        int lignes   = difficulte.getSquareSizeY();

        // Taille max pour tenir en largeur, et en hauteur
        int tailleParLargeur = largeurDispo / colonnes;
        int tailleParHauteur = hauteurDispo  / lignes;

        // On prend le plus contraignant, puis on borne entre min et max
        int taille = Math.min(tailleParLargeur, tailleParHauteur);
        taille = Math.max(CELL_MIN, Math.min(CELL_MAX, taille));

        return taille;
    }

    public int getCellSize()    { return cellSize;    }
    public int getHexRadius()   { return hexRadius;   }
    public int getGridPadding() { return gridPadding; }
}