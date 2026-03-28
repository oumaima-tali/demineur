package modele.jeu;

// les 3 niveaux du jeu, avec les parametres pour les 2 types de grille
public enum Difficulte {

    FACILE("Facile",    8, 13, 20,  9, 11, 20),
    MOYEN("Moyen",     10, 16, 35, 11, 13, 32),
    DIFFICILE("Difficile", 12, 18, 55, 13, 15, 45);


    private final String nom;
    private final int largeurCarre;
    private final int hauteurCarre;
    private final int minesCarre;
    private final int largeurHex;
    private final int hauteurHex;
    private final int minesHex;

    Difficulte(String nom, int lc, int hc, int mc, int lh, int hh, int mh) {
        this.nom = nom;
        this.largeurCarre = lc;
        this.hauteurCarre = hc;
        this.minesCarre   = mc;
        this.largeurHex   = lh;
        this.hauteurHex   = hh;
        this.minesHex     = mh;
    }

    public int getLargeurCarre()  { return largeurCarre; }
    public int getHauteurCarre()  { return hauteurCarre; }
    public int getMinesCarre()    { return minesCarre;   }
    public int getLargeurHex()    { return largeurHex;   }
    public int getHauteurHex()    { return hauteurHex;   }
    public int getMinesHex()      { return minesHex;     }

    @Override
    public String toString() { return nom; }
}