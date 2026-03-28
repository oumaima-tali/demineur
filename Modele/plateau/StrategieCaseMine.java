package modele.plateau;

public class StrategieCaseMine extends Strategie {

    @Override
    public void decouvrir(Case c, Plateau plateau) {
        // on signale l'explosion au plateau
        // le plateau va reveler toutes les mines et mettre le flag mineTouchee
        plateau.signalerExplosion();
    }
}