package modele.jeu;

import java.util.ArrayList;
import java.util.List;
import modele.plateau.Case;
import modele.plateau.Plateau;

/*
 * Logique de suggestion d'indice, separee de Jeu pour alleger la classe principale.
 * Applique deux regles simples sur les cases visibles avec chiffre.
 */
public class JeuIA {

    /*
     * Cherche une case utile a reveler au joueur.
     * Retourne un tableau de 2 elements :
     *   [0] = case sure (aucune mine, on peut cliquer dessus)
     *   [1] = case mine probable (il faut y poser un drapeau)
     * L'un ou l'autre peut etre null si rien n'est trouve.
     */
    public static Case[] chercherIndice(Plateau plateau) {
        Case caseSure = null;
        Case caseMine = null;
        Case[][] cases = plateau.getCases();

        boucle:
        for (int x = 0; x < plateau.getSizeX(); x++) {
            for (int y = 0; y < plateau.getSizeY(); y++) {
                Case c = cases[x][y];
                if (!c.isVisible() || c.getValeur() <= 0) continue;

                int nbDrapeaux = 0;
                List<Case> cachesSansFlag = new ArrayList<>();

                for (Case v : plateau.getVoisins(c)) {
                    if (v == null)           continue;
                    if (v.isFlagged())       nbDrapeaux++;
                    else if (!v.isVisible()) cachesSansFlag.add(v);
                }

                int minesRestantes = c.getValeur() - nbDrapeaux;

                // regle 1 : toutes les mines sont flagguees -> les cases restantes sont sures
                if (minesRestantes == 0 && !cachesSansFlag.isEmpty()) {
                    caseSure = cachesSansFlag.get(0);
                    break boucle;
                }

                // regle 2 : autant de cases cachees que de mines restantes -> ce sont des mines
                if (minesRestantes > 0 && cachesSansFlag.size() == minesRestantes && caseMine == null) {
                    caseMine = cachesSansFlag.get(0);
                }
            }
        }

        return new Case[]{ caseSure, caseMine };
    }
}