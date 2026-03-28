package modele.jeu;

import modele.plateau.Case;
/*
 * Contient uniquement les donnees d'etat d'une partie en cours.
 * Separee de Jeu pour que la logique de coordination reste claire.
 */
public class EtatJeu {

    private boolean enCours = true;
    private boolean perdu   = false;
    private boolean gagne   = false;
    private int     temps   = 0;

    // pour le systeme d'indice
    private Case    caseIndice    = null;
    private boolean indiceEstSure = false;

    // getters

    public boolean isEnCours()       { return enCours;       }
    public boolean isPerdu()         { return perdu;         }
    public boolean isGagne()         { return gagne;         }
    public int     getTemps()        { return temps;         }
    public Case    getCaseIndice()   { return caseIndice;    }
    public boolean isIndiceEstSure() { return indiceEstSure; }

    // setters

    public void setEnCours(boolean v)       { this.enCours = v;       }
    public void setPerdu(boolean v)         { this.perdu = v;         }
    public void setGagne(boolean v)         { this.gagne = v;         }
    public void setCaseIndice(Case c)       { this.caseIndice = c;    }
    public void setIndiceEstSure(boolean v) { this.indiceEstSure = v; }

    public void incrementerTemps() {
        if (enCours && temps < 999) temps++;
    }
}