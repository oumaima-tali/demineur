package modele.plateau;


//SCL
 
public class StrategieCaseLibre extends Strategie {

    @Override
    public void decouvrir(Case c, Plateau plateau) {
        if (c.getValeur() == 0) {
            for (Case voisin : plateau.getVoisins(c)) {
                if (voisin != null) {
                    plateau.decouvrirCase(voisin);
                }
            }
        }
    }
}