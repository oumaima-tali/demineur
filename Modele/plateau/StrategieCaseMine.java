package modele.plateau;
public class StrategieCaseMine extends Strategie {

    @Override
    public void decouvrir(Case c, Plateau plateau) {
        plateau.decouvrirToutesLesMines();
        if (plateau.getJeu() != null) {
            plateau.getJeu().perdre();
        }
    }
}