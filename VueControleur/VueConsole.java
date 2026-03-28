package VueControleur;

import java.util.Observable;
import java.util.Observer;
import modele.plateau.Case;
import modele.plateau.Plateau;

/*
 * Vue alternative en mode texte dans le terminal.
 * Elle s'enregistre comme observateur du meme plateau que VueControleur.
 * Ca prouve que le modele est independant de la technologie d'affichage.
 */
public class VueConsole implements Observer {

    private final Plateau plateau;

    public VueConsole(Plateau plateau) {
        this.plateau = plateau;
        plateau.addObserver(this); 
    }

    @Override
    public void update(Observable o, Object arg) {
        afficher();
    }

    public void afficher() {
        int lx = plateau.getSizeX();
        int ly = plateau.getSizeY();
        Case[][] cases = plateau.getCases();

        System.out.println("plateau ");
        for (int y = 0; y < ly; y++) {
            for (int x = 0; x < lx; x++) {
                System.out.print(representCase(cases[x][y]) + " ");
            }
            System.out.println();
        }
        System.out.println("mines restantes : " + (plateau.getNbMines() - plateau.getNbDrapeaux()));
    }

    private String representCase(Case c) {
        if (c.isFlagged())       return "F";
        if (!c.isVisible())      return ".";
        if (c.getValeur() == -1) return "*";
        return String.valueOf(c.getValeur());
    }
}