package modele.plateau;

public class Case {

    private int     valeur;
    private boolean visible  = false;
    private boolean flagged  = false;
    private boolean mine     = false;

    // la strategie change quand on pose une mine (setMine)
    private Strategie strategie = new StrategieCaseLibre();

    // reference vers le plateau, necessaire pour que la strategie puisse agir
    protected Plateau plateau;

    public Case(Plateau plateau) {
        this.plateau = plateau;
    }

    // decouvre la case si elle n'est pas marquee
    public void decouvrir() {
        if (flagged) return;
        visible = true;
        strategie.decouvrir(this, plateau);
    }

    // decouverte forcee quand on perd (pour afficher les mines)
    public void decouvrirForce() {
        visible = true;
        flagged = false;
    }

    public void toggleFlag() {
        if (!visible) {
            flagged = !flagged;
        }
    }

    // quand on place une mine sur cette case, on change de strategie
    public void setMine(boolean mine) {
        this.mine = mine;
        if (mine) {
            strategie = new StrategieCaseMine();
        } else {
            strategie = new StrategieCaseLibre();
        }
    }

    public void    setValeur(int v) { this.valeur = v;  }
    public int     getValeur()      { return valeur;    }
    public boolean isVisible()      { return visible;   }
    public boolean isFlagged()      { return flagged;   }
    public boolean isMine()         { return mine;      }
}