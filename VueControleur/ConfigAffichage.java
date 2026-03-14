package VueControleur;

import modele.jeu.Difficulte;

/**
 * Paramètres visuels (taille des cellules, rayon hexagonal, marges) associés
 * à une difficulté et un type de grille.
 * Aucun de ces paramètres ne doit figurer dans le modèle.
 */
public class ConfigAffichage {

    private static final int GRID_PADDING = 14;

    // Taille de cellule (px) pour la grille carrée, par niveau de difficulté
    private static final int[] SQUARE_CELL_SIZES = { 40, 34, 28 };

    // Rayon des hexagones (px) pour la grille hexagonale, par niveau de difficulté
    private static final int[] HEX_RADII = { 24, 21, 18 };

    private final int cellSize;
    private final int hexRadius;
    private final int gridPadding;

    public ConfigAffichage(Difficulte difficulte, boolean hexagonal) {
        this.gridPadding = GRID_PADDING;
        int idx = difficulte.ordinal();
        this.hexRadius = HEX_RADII[idx];
        this.cellSize  = hexagonal ? hexRadius * 2 : SQUARE_CELL_SIZES[idx];
    }

    public int getCellSize()    { return cellSize; }
    public int getHexRadius()   { return hexRadius; }
    public int getGridPadding() { return gridPadding; }
}
