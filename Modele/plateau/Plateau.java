package modele.plateau;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;

/*
 * Classe abstraite commune aux deux types de grilles.
 * Contient toute la logique de jeu : placement des mines, decouverte des cases,
 * gestion des observateurs via java.util.Observable.
 *
 * La seule methode abstraite est getVoisins() car elle depend de la geometrie.
 * PlateauC et PlateauH n'ont qu'a implementer ca.
 */
public abstract class Plateau extends Observable {

    protected int largeur;
    protected int hauteur;
    protected int nbMines;
    protected Case[][] grille;

    private boolean minesPlacees   = false;
    private boolean mineTouchee    = false;
    private int casesDecouvertes   = 0;

    // pour retrouver les coordonnees (x,y) d'une case a partir de l'objet
    private HashMap<Case, Point> positions = new HashMap<>();

    public Plateau(int largeur, int hauteur, int nbMines) {
        this.largeur  = largeur;
        this.hauteur  = hauteur;
        this.nbMines  = nbMines;
        this.grille   = new Case[largeur][hauteur];

        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                grille[x][y] = new Case(this);
                positions.put(grille[x][y], new Point(x, y));
            }
        }
    }

    // place les mines au premier clic pour eviter de perdre direct
    private void initialiserMines(Case premierClic) {
        if (minesPlacees) return;

        // cases interdites : la case cliquee + ses voisins
        HashSet<Case> interdites = new HashSet<>();
        interdites.add(premierClic);
        for (Case v : getVoisins(premierClic)) {
            if (v != null) interdites.add(v);
        }

        // si trop de mines par rapport a la taille, on relache la contrainte
        if ((largeur * hauteur) - interdites.size() < nbMines) {
            interdites.clear();
            interdites.add(premierClic);
        }

        // placement aleatoire
        int nb = 0;
        while (nb < nbMines) {
            int rx = (int)(Math.random() * largeur);
            int ry = (int)(Math.random() * hauteur);
            Case c = grille[rx][ry];
            if (!c.isMine() && !interdites.contains(c)) {
                c.setMine(true);
                c.setValeur(-1);
                nb++;
            }
        }

        calculerValeurs();
        minesPlacees = true;
    }

    // calcule le nombre de mines autour de chaque case libre
    private void calculerValeurs() {
        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                Case c = grille[x][y];
                if (c.isMine()) continue;

                int nbMinesAutour = 0;
                for (Case v : getVoisins(c)) {
                    if (v != null && v.isMine()) nbMinesAutour++;
                }
                c.setValeur(nbMinesAutour);
            }
        }
    }

    // decouvre une case (appele par Jeu ou par la cascade)
    public void decouvrirCase(Case c) {
        if (c.isVisible() || c.isFlagged()) return;

        if (!minesPlacees) {
            initialiserMines(c);
        }

        c.decouvrir(); // appelle la strategie (libre ou mine)

        if (!c.isMine()) {
            casesDecouvertes++;
        }
    }

    // clic gauche sur une case deja visible : decouvre les voisins si assez de drapeaux
    public void decouvrirVoisins(Case c) {
        if (!c.isVisible() || c.isMine()) return;

        int nbDrapeaux = 0;
        for (Case v : getVoisins(c)) {
            if (v != null && v.isFlagged()) nbDrapeaux++;
        }

        if (nbDrapeaux == c.getValeur()) {
            for (Case v : getVoisins(c)) {
                if (v != null && !v.isFlagged()) {
                    decouvrirCase(v);
                }
            }
        }
    }

    // appele par StrategieCaseMine quand le joueur clique sur une mine
    public void signalerExplosion() {
        mineTouchee = true;
        // on revele toutes les mines
        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                if (grille[x][y].isMine()) {
                    grille[x][y].decouvrirForce();
                }
            }
        }
    }

    // les sous-classes definissent leurs voisins selon leur geometrie
    public abstract Case[] getVoisins(Case c);

    // par defaut la grille n'est pas hexagonale
    public boolean isHexagonal() { return false; }

    // notification des observateurs via java.util.Observable
    // setChanged() est obligatoire sinon notifyObservers() ne fait rien
    public void notifierObservateurs() {
        setChanged();
        notifyObservers();
    }

    // getters

    public int      getLargeur()          { return largeur;          }
    public int      getHauteur()          { return hauteur;          }
    public int      getNbMines()          { return nbMines;          }
    public Case[][] getGrille()           { return grille;           }
    public boolean  isMineTouchee()       { return mineTouchee;      }
    public boolean  isMinesPlacees()      { return minesPlacees;     }
    public int      getCasesDecouvertes() { return casesDecouvertes; }
    public Point    getPosition(Case c)   { return positions.get(c); }

    // aliases pour compatibilite avec VueControleur
    public int      getSizeX()  { return largeur; }
    public int      getSizeY()  { return hauteur; }
    public Case[][] getCases()  { return grille;  }

    public int getNbDrapeaux() {
        int nb = 0;
        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                if (grille[x][y].isFlagged()) nb++;
            }
        }
        return nb;
    }
}