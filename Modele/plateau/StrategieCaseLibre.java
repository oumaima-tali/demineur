package modele.plateau;

public class StrategieCaseLibre extends Strategie {

    @Override
    public void decouvrir(Case c, Plateau plateau) {
        // si la case vaut 0, on decouvre automatiquement tous ses voisins
        if (c.getValeur() == 0) {
            for (Case voisin : plateau.getVoisins(c)) {
                if (voisin != null) {
                    plateau.decouvrirCase(voisin);
                }
            }
        }
    }
}