package modele.jeu;

import modele.plateau.Case;
import modele.plateau.Plateau;
import modele.plateau.PlateauC;
import modele.plateau.PlateauH;

/*
 * Classe principale qui gere l'etat de la partie.
 * Elle fait le lien entre les actions du joueur (clic) et le plateau.
 * L'etat est stocke dans EtatJeu, la logique d'indice dans JeuIA.
 */
public class Jeu {

    private final Plateau plateau;
    private final EtatJeu etat = new EtatJeu();

    public Jeu(String typeGrille, Difficulte difficulte) {
        if (typeGrille.equals("hexagonal")) {
            plateau = new PlateauH(difficulte);
        } else {
            plateau = new PlateauC(difficulte);
        }
    }

    // clic gauche : decouvrir une case ou ses voisins si deja visible
    public void clicGauche(int x, int y) {
        if (!etat.isEnCours()) return;

        Case c = plateau.getCases()[x][y];

        if (c.isVisible()) {
            plateau.decouvrirVoisins(c);
        } else {
            plateau.decouvrirCase(c);
        }

        // verifier si le joueur a perdu (mine touchee pendant la decouverte)
        if (plateau.isMineTouchee()) {
            perdre();
        }
        // verifier la victoire : toutes les cases non-mines sont decouvertes
        else if (plateau.getCasesDecouvertes() == plateau.getSizeX() * plateau.getSizeY() - plateau.getNbMines()) {
            gagner();
        }

        plateau.notifierObservateurs();
    }

    // clic droit : poser ou enlever un drapeau
    public void clicDroit(int x, int y) {
        if (!etat.isEnCours()) return;
        plateau.getCases()[x][y].toggleFlag();
        plateau.notifierObservateurs();
    }

    // demande un indice en delegant la recherche a JeuIA
    public void demanderIndice() {
        if (!etat.isEnCours()) return;

        etat.setCaseIndice(null);
        etat.setIndiceEstSure(false);

        Case[] resultat = JeuIA.chercherIndice(plateau);

        if (resultat[0] != null) {
            // case sure : on peut cliquer dessus sans risque
            etat.setCaseIndice(resultat[0]);
            etat.setIndiceEstSure(true);
        } else if (resultat[1] != null) {
            // mine probable : il faut y mettre un drapeau
            etat.setCaseIndice(resultat[1]);
            etat.setIndiceEstSure(false);
        }

        plateau.notifierObservateurs();
    }

    public void effacerIndice() {
        etat.setCaseIndice(null);
        plateau.notifierObservateurs();
    }

    public void perdre() {
        if (!etat.isEnCours()) return;
        etat.setPerdu(true);
        etat.setEnCours(false);
    }

    public void gagner() {
        if (!etat.isEnCours()) return;
        etat.setGagne(true);
        etat.setEnCours(false);
    }

    public void incrementerTemps() {
        etat.incrementerTemps();
    }

    // getters - deleguent a etat ou plateau

    public Plateau getPlateau()      { return plateau;              }
    public boolean isEnCours()       { return etat.isEnCours();     }
    public boolean isPerdu()         { return etat.isPerdu();       }
    public boolean isGagne()         { return etat.isGagne();       }
    public int     getTemps()        { return etat.getTemps();      }
    public Case    getCaseIndice()   { return etat.getCaseIndice(); }
    public boolean isIndiceEstSure() { return etat.isIndiceEstSure(); }
}