package modele.plateau;

// pattern Strategy : chaque case a un comportement different quand on la decouvre
public abstract class Strategie {
    public abstract void decouvrir(Case c, Plateau plateau);
}