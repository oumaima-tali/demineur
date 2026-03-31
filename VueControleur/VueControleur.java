package VueControleur;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import modele.jeu.Difficulte;
import modele.jeu.Jeu;
import modele.plateau.Case;
import modele.plateau.Plateau;


public class VueControleur extends JPanel implements Observer {

    private Plateau    plateau;
    private Jeu        jeu;
    private Runnable   actionRejouer;
    private Runnable   actionMenu;
    private Difficulte difficulte;

    // dimensions calculees selon la difficulte et le type de grille
    private final int tailleCase;
    private final int rayonHex;
    private final int hexLargeur;
    private final int hexHauteur;
    private final int hexDecalCol;
    private final int hexDecalLigne;
    // le tableau de labels qui representent les cases a l'ecran
    private JLabel[][] cellules;

    // icones du jeu
    private ImageIcon icoDrapeau;
    private ImageIcon icoBombe;
    private ImageIcon[] icoChiffres;
    private ImageIcon icoSmileyNormal;
    private ImageIcon icoSmileyClic;
    private ImageIcon icoSmileyPerdu;
    private ImageIcon icoSmileyGagne;

    // composants de la barre du haut
    private JLabel  labelMines;
    private JLabel  labelTemps;
    private JButton boutonSmiley;

    private Timer timer;

    // etats possibles du smiley
    public enum EtatSmiley { NORMAL, CLIC, PERDU, GAGNE }

    // Cellule hexagonale personnalisee
    // Elle dessine un hexagone au lieu d'un rectangle.
    private static class CelluleHex extends JLabel {

        private Polygon forme;
        private boolean enfoncee = false;

        public CelluleHex(int larg, int haut) {
            super("", SwingConstants.CENTER);
            forme = construireHexagone(larg, haut);
            setPreferredSize(new Dimension(larg, haut));
            setOpaque(false);
            setBackground(new Color(192, 192, 192));
        }

        public void setEnfoncee(boolean val) {
            enfoncee = val;
            repaint();
        }

        // on redefinit contains pour que les clics sur les coins ne comptent pas
        @Override
        public boolean contains(int x, int y) {
            return forme.contains(x, y);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillPolygon(forme);

            // clip pour que l'icone reste dans l'hexagone
            Shape ancienClip = g2.getClip();
            g2.setClip(forme);
            super.paintComponent(g2);
            g2.setClip(ancienClip);

            // effet bevel (relief 3D) - change selon enfonce ou non
            Color clair = enfoncee ? Color.DARK_GRAY : Color.WHITE;
            Color fonce = enfoncee ? Color.WHITE     : Color.DARK_GRAY;

            g2.setColor(clair);
            g2.drawLine(forme.xpoints[0], forme.ypoints[0], forme.xpoints[1], forme.ypoints[1]);
            g2.drawLine(forme.xpoints[1], forme.ypoints[1], forme.xpoints[2], forme.ypoints[2]);
            g2.drawLine(forme.xpoints[2], forme.ypoints[2], forme.xpoints[3], forme.ypoints[3]);

            g2.setColor(fonce);
            g2.drawLine(forme.xpoints[3], forme.ypoints[3], forme.xpoints[4], forme.ypoints[4]);
            g2.drawLine(forme.xpoints[4], forme.ypoints[4], forme.xpoints[5], forme.ypoints[5]);
            g2.drawLine(forme.xpoints[5], forme.ypoints[5], forme.xpoints[0], forme.ypoints[0]);

            g2.dispose();
        }

        private static Polygon construireHexagone(int larg, int haut) {
            int g = 1;
            int[] px = { larg/2, larg-g, larg-g, larg/2, g,        g          };
            int[] py = { g,      haut/4, (3*haut)/4, haut-g, (3*haut)/4, haut/4 };
            return new Polygon(px, py, 6);
        }
    }

    // Constructeur
    public VueControleur(Jeu jeu, Difficulte difficulte, Runnable actionRejouer, Runnable actionMenu) {
        this.jeu           = jeu;
        this.difficulte    = difficulte;
        this.actionRejouer = actionRejouer;
        this.actionMenu    = actionMenu;
        this.plateau       = jeu.getPlateau();

        // calculer les tailles selon la difficulte et le type de grille
        tailleCase    = getTailleCase(difficulte);
        rayonHex      = getRayonHex(difficulte);
        hexLargeur    = (int) Math.round(Math.sqrt(3) * rayonHex);
        hexHauteur    = 2 * rayonHex;
        hexDecalCol   = hexLargeur;
        hexDecalLigne = (int) Math.round(hexHauteur * 0.75);

        chargerImages();
        construireInterface();
        demarrerTimer();
        plateau.addObserver(this); // enregistrement via java.util.Observable
        mettreAJour();
    }

    // taille d'une cellule carree selon la difficulte
    private int getTailleCase(Difficulte d) {
        if (d == Difficulte.FACILE) return 40;
        if (d == Difficulte.MOYEN)  return 34;
        return 28; // DIFFICILE
    }

    // rayon de l'hexagone selon la difficulte
    private int getRayonHex(Difficulte d) {
        if (d == Difficulte.FACILE) return 26;
        if (d == Difficulte.MOYEN)  return 22;
        return 19; // DIFFICILE
    }

    // Chargement des images
    private void chargerImages() {
        icoDrapeau = chargerIcone("images/flag.png");
        icoBombe   = chargerIcone("images/bomb.png");

        icoSmileyNormal = chargerIcone("images/smiley-neutral.png", 34);
        icoSmileyClic   = chargerIcone("images/smiley-onclick.png", 34);
        icoSmileyPerdu  = chargerIcone("images/smiley-lose.png",    34);
        icoSmileyGagne  = chargerIcone("images/smiley-win.png",     34);

        icoChiffres = new ImageIcon[9];
        for (int i = 0; i <= 8; i++) {
            icoChiffres[i] = chargerIcone("images/" + i + ".png");
        }
    }

    private ImageIcon chargerIcone(String chemin) {
        URL url = getClass().getClassLoader().getResource(chemin);
        if (url == null) {
            System.err.println("image introuvable : " + chemin);
            return null;
        }
        int taille = plateau.isHexagonal()
                ? Math.max(12, Math.min(hexLargeur, hexHauteur) - 8)
                : Math.max(12, tailleCase - 10);

        Image img = new ImageIcon(url).getImage().getScaledInstance(taille, taille, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private ImageIcon chargerIcone(String chemin, int taille) {
        URL url = getClass().getClassLoader().getResource(chemin);
        if (url == null) return null;
        Image img = new ImageIcon(url).getImage().getScaledInstance(taille, taille, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void changerSmiley(EtatSmiley etat) {
        if (boutonSmiley == null) return;
        switch (etat) {
            case CLIC:  boutonSmiley.setIcon(icoSmileyClic);  break;
            case PERDU: boutonSmiley.setIcon(icoSmileyPerdu); break;
            case GAGNE: boutonSmiley.setIcon(icoSmileyGagne); break;
            default:    boutonSmiley.setIcon(icoSmileyNormal); break;
        }
    }

    // Construction de l'interface
    private void construireInterface() {
        setLayout(new BorderLayout());
        setBackground(new Color(192, 192, 192));
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        add(creerBarreHaut(), BorderLayout.NORTH);
        add(creerGrilleWrapper(), BorderLayout.CENTER);
        add(creerBarreBas(), BorderLayout.SOUTH);
    }

    private JPanel creerGrilleWrapper() {
        JPanel grille = plateau.isHexagonal() ? creerGrilleHex() : creerGrilleCarre();

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setBackground(new Color(192, 192, 192));
        wrapper.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        wrapper.add(grille);
        return wrapper;
    }

    private JPanel creerGrilleCarre() {
        int lx = plateau.getSizeX();
        int ly = plateau.getSizeY();

        JPanel grille = new JPanel(new GridLayout(ly, lx));
        grille.setBackground(new Color(192, 192, 192));
        grille.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        grille.setPreferredSize(new Dimension(lx * tailleCase, ly * tailleCase));

        cellules = new JLabel[lx][ly];
        for (int y = 0; y < ly; y++) {
            for (int x = 0; x < lx; x++) {
                JLabel lab = new JLabel("", SwingConstants.CENTER);
                lab.setPreferredSize(new Dimension(tailleCase, tailleCase));
                lab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                lab.setOpaque(true);
                lab.setBackground(new Color(192, 192, 192));
                cellules[x][y] = lab;
                ajouterEcouteurs(lab, x, y);
                grille.add(lab);
            }
        }
        return grille;
    }

    private JPanel creerGrilleHex() {
        int lx = plateau.getSizeX();
        int ly = plateau.getSizeY();

        JPanel grille = new JPanel(null); // layout null pour positionner les hex manuellement
        grille.setBackground(new Color(192, 192, 192));
        grille.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        int decalImpair = (int) Math.round(hexLargeur / 2.0);
        cellules = new JLabel[lx][ly];

        for (int y = 0; y < ly; y++) {
            int decalLigne = (y % 2 == 1) ? decalImpair : 0;
            for (int x = 0; x < lx; x++) {
                CelluleHex cell = new CelluleHex(hexLargeur, hexHauteur);
                cell.setBounds(x * hexDecalCol + decalLigne, y * hexDecalLigne, hexLargeur, hexHauteur);
                cellules[x][y] = cell;
                ajouterEcouteurs(cell, x, y);
                grille.add(cell);
            }
        }

        int larg = lx * hexDecalCol + decalImpair + 2;
        int haut = (ly - 1) * hexDecalLigne + hexHauteur + 2;
        grille.setPreferredSize(new Dimension(larg, haut));
        return grille;
    }

    // ajoute les ecouteurs de souris sur une cellule
    private void ajouterEcouteurs(JLabel lab, int x, int y) {
        lab.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    jeu.clicDroit(x, y);
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    jeu.clicGauche(x, y);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Case c = plateau.getCases()[x][y];
                if (SwingUtilities.isLeftMouseButton(e) && !c.isVisible() && !c.isFlagged() && jeu.isEnCours()) {
                    // feedback visuel immediat
                    if (lab instanceof CelluleHex) ((CelluleHex) lab).setEnfoncee(true);
                    else lab.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                    changerSmiley(EtatSmiley.CLIC);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Case c = plateau.getCases()[x][y];
                if (!c.isVisible()) {
                    if (lab instanceof CelluleHex) ((CelluleHex) lab).setEnfoncee(false);
                    else lab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                }
                if (jeu.isEnCours()) changerSmiley(EtatSmiley.NORMAL);
            }
        });
    }

    // Barre du haut (compteur mines, smiley, chrono)
    private JPanel creerBarreHaut() {
        JPanel barre = new JPanel(new BorderLayout());
        barre.setBackground(new Color(192, 192, 192));
        barre.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(6, 6, 6, 6),
            BorderFactory.createBevelBorder(BevelBorder.LOWERED)
        ));

        labelMines = creerLabelLCD("000");
        JPanel panelG = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panelG.setBackground(new Color(192, 192, 192));
        panelG.add(labelMines);

        boutonSmiley = new JButton();
        boutonSmiley.setPreferredSize(new Dimension(40, 40));
        boutonSmiley.setFocusPainted(false);
        boutonSmiley.setContentAreaFilled(false);
        boutonSmiley.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        boutonSmiley.setIcon(icoSmileyNormal);
        boutonSmiley.addActionListener(e -> {
            arreterTimer();
            actionRejouer.run();
        });
        JPanel panelC = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        panelC.setBackground(new Color(192, 192, 192));
        panelC.add(boutonSmiley);

        labelTemps = creerLabelLCD("000");
        JPanel panelD = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        panelD.setBackground(new Color(192, 192, 192));
        panelD.add(labelTemps);

        barre.add(panelG, BorderLayout.WEST);
        barre.add(panelC, BorderLayout.CENTER);
        barre.add(panelD, BorderLayout.EAST);
        return barre;
    }

    private JLabel creerLabelLCD(String texte) {
        JLabel l = new JLabel(texte, SwingConstants.RIGHT);
        l.setFont(new Font("Monospaced", Font.BOLD, 22));
        l.setForeground(Color.RED);
        l.setBackground(Color.BLACK);
        l.setOpaque(true);
        l.setPreferredSize(new Dimension(56, 30));
        l.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        return l;
    }

    private String formatLCD(int val) {
        val = Math.max(-99, Math.min(999, val));
        if (val < 0) return String.format("-%02d", Math.abs(val));
        return String.format("%03d", val);
    }

    // Barre du bas (indice + menu)
    private JPanel creerBarreBas() {
        JPanel barre = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        barre.setBackground(new Color(192, 192, 192));

        JButton btnIndice = new JButton("Indice");
        btnIndice.addActionListener(e -> {
            jeu.demanderIndice();
            if (jeu.getCaseIndice() == null) {
                JOptionPane.showMessageDialog(this,
                    "Aucun indice disponible pour l'instant.\nEssaie de poser des drapeaux d'abord !",
                    "Indice", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // effacer l'indice apres 2 secondes
            Timer effacement = new Timer(2000, ev -> jeu.effacerIndice());
            effacement.setRepeats(false);
            effacement.start();
        });

        JButton btnMenu = new JButton("Menu principal");
        btnMenu.addActionListener(e -> {
            arreterTimer();
            actionMenu.run();
        });

        barre.add(btnIndice);
        barre.add(btnMenu);
        return barre;
    }

    // Timer Swing (incremente le temps de jeu chaque seconde)
    private void demarrerTimer() {
        labelTemps.setText(formatLCD(0));
        timer = new Timer(1000, e -> {
            jeu.incrementerTemps();
            labelTemps.setText(formatLCD(jeu.getTemps()));
        });
        timer.start();
    }

    private void arreterTimer() {
        if (timer != null) timer.stop();
    }

    // update() est appelee par le plateau quand quelque chose change (java.util.Observer)
    @Override
    public void update(Observable o, Object arg) {
        mettreAJour();

        if (jeu.isPerdu()) {
            arreterTimer();
            changerSmiley(EtatSmiley.PERDU);

            Timer delai = new Timer(300, e -> {
                int choix = JOptionPane.showOptionDialog(
                    this,
                    "Vous avez perdu !",
                    "Partie terminee",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Rejouer", "Retour au menu"},
                    "Rejouer"
                );
                if (choix == JOptionPane.YES_OPTION) {
                    arreterTimer();
                    actionRejouer.run();
                } else {
                    arreterTimer();
                    actionMenu.run();
                }
            });
            delai.setRepeats(false);
            delai.start();

        } else if (jeu.isGagne()) {
            arreterTimer();
            changerSmiley(EtatSmiley.GAGNE);

            String message = "Vous avez gagne en " + jeu.getTemps() + "s !";

            Timer delai = new Timer(300, e -> {
                int choix = JOptionPane.showOptionDialog(
                    this,
                    message,
                    "Victoire !",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Rejouer", "Retour au menu"},
                    "Rejouer"
                );
                if (choix == JOptionPane.YES_OPTION) {
                    arreterTimer();
                    actionRejouer.run();
                } else {
                    arreterTimer();
                    actionMenu.run();
                }
            });
            delai.setRepeats(false);
            delai.start();
        }
    }

    // mise a jour de l'affichage
    private void mettreAJour() {
        int lx = plateau.getSizeX();
        int ly = plateau.getSizeY();
        Case[][] cases = plateau.getCases();

        for (int x = 0; x < lx; x++) {
            for (int y = 0; y < ly; y++) {
                Case c   = cases[x][y];
                JLabel l = cellules[x][y];

                if (c.isVisible()) {
                    // case ouverte
                    if (l instanceof CelluleHex) ((CelluleHex) l).setEnfoncee(true);
                    else l.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

                    l.setText("");
                    if (c.getValeur() == -1) {
                        // mine
                        l.setBackground(new Color(255, 80, 80));
                        l.setIcon(icoBombe);
                    } else {
                        l.setBackground(new Color(192, 192, 192));
                        l.setIcon(icoChiffres[c.getValeur()]);
                    }

                } else if (c.isFlagged()) {
                    // drapeau
                    if (l instanceof CelluleHex) ((CelluleHex) l).setEnfoncee(false);
                    else l.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                    l.setBackground(new Color(192, 192, 192));
                    l.setText("");
                    l.setIcon(icoDrapeau);

                } else {
                    // case cachee normale
                    if (l instanceof CelluleHex) ((CelluleHex) l).setEnfoncee(false);
                    else l.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                    l.setBackground(new Color(192, 192, 192));
                    l.setText("");
                    l.setIcon(null);
                }
            }
        }

        // mise a jour du compteur de mines
        labelMines.setText(formatLCD(plateau.getNbMines() - plateau.getNbDrapeaux()));

        // affichage de l'indice si present
        Case indice = jeu.getCaseIndice();
        if (indice != null) {
            for (int x = 0; x < lx; x++) {
                for (int y = 0; y < ly; y++) {
                    if (cases[x][y] == indice) {
                        Color couleur = jeu.isIndiceEstSure()
                            ? new Color(100, 220, 100)  // vert = sure
                            : new Color(255, 100, 100); // rouge = mine probable
                        cellules[x][y].setBackground(couleur);
                    }
                }
            }
        }
    }
}