package modele.jeu;

public enum Difficulte {
    FACILE("Facile", 8, 13, 20, 9, 11, 20),
    MOYEN("Moyen", 10, 16, 35, 11, 13, 32),
    DIFFICILE("Difficile", 12, 18, 55, 13, 15, 45);

    private final String label;
    private final int squareSizeX;
    private final int squareSizeY;
    private final int squareNbMines;
    private final int hexSizeX;
    private final int hexSizeY;
    private final int hexNbMines;

    Difficulte(String label,
               int squareSizeX,
               int squareSizeY,
               int squareNbMines,
               int hexSizeX,
               int hexSizeY,
               int hexNbMines) {
        this.label = label;
        this.squareSizeX = squareSizeX;
        this.squareSizeY = squareSizeY;
        this.squareNbMines = squareNbMines;
        this.hexSizeX = hexSizeX;
        this.hexSizeY = hexSizeY;
        this.hexNbMines = hexNbMines;
    }

    public int getSquareSizeX() {
        return squareSizeX;
    }

    public int getSquareSizeY() {
        return squareSizeY;
    }

    public int getSquareNbMines() {
        return squareNbMines;
    }

    public int getHexSizeX() {
        return hexSizeX;
    }

    public int getHexSizeY() {
        return hexSizeY;
    }

    public int getHexNbMines() {
        return hexNbMines;
    }

    @Override
    public String toString() {
        return label;
    }
}