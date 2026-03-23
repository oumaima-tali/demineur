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

    private Plateau plateau;
    private Jeu jeu;
    private final Runnable restartAction;
    private final Runnable menuAction;
    private final int sizeX;
    private final int sizeY;
    private final int pxCase;
    private final int hexRadius;
    private final int gridPadding;
    private final int hexCellWidth;
    private final int hexCellHeight;
    private final int hexColStep;
    private final int hexRowStep;
    private JLabel[][] tabJLabel;

    private ImageIcon flagIcon;
    private ImageIcon bombIcon;
    private ImageIcon[] numberIcons;
    private ImageIcon smileyNeutralIcon;
    private ImageIcon smileyClickIcon;
    private ImageIcon smileyLoseIcon;
    private ImageIcon smileyWinIcon;

    private JLabel compteurMinesLabel;
    private JLabel timerLabel;
    private JButton smileButton;

    private Timer swingTimer;

    public enum EtatSmiley { NORMAL, CLIC, PERDU, GAGNE }

    // Classe interne : cellule hexagonale
    private static class HexCellLabel extends JLabel {

        private final Polygon shape;
        private boolean lowered = false;

        public HexCellLabel(int width, int height) {
            super("", SwingConstants.CENTER);
            shape = creerHexagone(width, height);
            setPreferredSize(new Dimension(width, height));
            setOpaque(false);
            setBackground(new Color(192, 192, 192));
        }

        public void setLowered(boolean value) {
            lowered = value;
            repaint();
        }

        @Override
        public boolean contains(int x, int y) {
            return shape.contains(x, y);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillPolygon(shape);

            Shape ancienClip = g2.getClip();
            g2.setClip(shape);
            super.paintComponent(g2);
            g2.setClip(ancienClip);

            Color clair = lowered ? Color.DARK_GRAY : Color.WHITE;
            Color fonce = lowered ? Color.WHITE     : Color.DARK_GRAY;
            g2.setColor(clair);
            g2.drawLine(shape.xpoints[0], shape.ypoints[0], shape.xpoints[1], shape.ypoints[1]);
            g2.drawLine(shape.xpoints[1], shape.ypoints[1], shape.xpoints[2], shape.ypoints[2]);
            g2.drawLine(shape.xpoints[2], shape.ypoints[2], shape.xpoints[3], shape.ypoints[3]);
            g2.setColor(fonce);
            g2.drawLine(shape.xpoints[3], shape.ypoints[3], shape.xpoints[4], shape.ypoints[4]);
            g2.drawLine(shape.xpoints[4], shape.ypoints[4], shape.xpoints[5], shape.ypoints[5]);
            g2.drawLine(shape.xpoints[5], shape.ypoints[5], shape.xpoints[0], shape.ypoints[0]);

            g2.dispose();
        }

        private static Polygon creerHexagone(int width, int height) {
            int g = 1; 
            int[] x = { width / 2,  width - g, width - g, width / 2, g,         g          };
            int[] y = { g,           height / 4, (3 * height) / 4, height - g, (3 * height) / 4, height / 4 };
            return new Polygon(x, y, 6);
        }
    }

    
    // Constructeur
    public VueControleur(Jeu _jeu, Difficulte _difficulte, Runnable _restartAction, Runnable _menuAction) {
        jeu           = _jeu;
        restartAction = _restartAction;
        menuAction    = _menuAction;
        plateau       = jeu.getPlateau();
        sizeX         = plateau.getSizeX();
        sizeY         = plateau.getSizeY();
        ConfigAffichage config = new ConfigAffichage(_difficulte, plateau.isHexagonal());
        hexRadius    = config.getHexRadius();
        gridPadding  = config.getGridPadding();
        pxCase       = config.getCellSize();
        hexCellWidth  = (int) Math.round(Math.sqrt(3) * hexRadius);
        hexCellHeight = 2 * hexRadius;
        hexColStep    = hexCellWidth;                                
        hexRowStep    = (int) Math.round(hexCellHeight * 0.75);     
        chargerImages();
        placerLesComposantsGraphiques();
        demarrerTimer();
        plateau.addObserver(this);
        mettreAJourAffichage();
    }

    // Images
    private void chargerImages() {
        flagIcon   = chargerIcone("images/flag.png");
        bombIcon   = chargerIcone("images/bomb.png");
        numberIcons = new ImageIcon[9];
        smileyNeutralIcon = chargerIcone("images/smiley-neutral.png", 34);
        smileyClickIcon   = chargerIcone("images/smiley-onclick.png", 34);
        smileyLoseIcon    = chargerIcone("images/smiley-lose.png",    34);
        smileyWinIcon     = chargerIcone("images/smiley-win.png",     34);
        for (int i = 0; i <= 8; i++) {
            numberIcons[i] = chargerIcone("images/" + i + ".png");
        }
    }

    private ImageIcon chargerIcone(String chemin) {
        URL url = getClass().getClassLoader().getResource(chemin);
        if (url == null) { System.err.println("[WARN] Image introuvable : " + chemin); return null; }
        int iconSize = plateau.isHexagonal()
                ? Math.max(12, Math.min(hexCellWidth, hexCellHeight) - 8)
                : Math.max(12, pxCase - 10);
        Image scaled = new ImageIcon(url).getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private ImageIcon chargerIcone(String chemin, int taille) {
        URL url = getClass().getClassLoader().getResource(chemin);
        if (url == null) { System.err.println("[WARN] Image introuvable : " + chemin); return null; }
        Image scaled = new ImageIcon(url).getImage().getScaledInstance(taille, taille, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void setEtatSmiley(EtatSmiley etat) {
        if (smileButton == null) return;
        switch (etat) {
            case CLIC:  smileButton.setIcon(smileyClickIcon);   break;
            case PERDU: smileButton.setIcon(smileyLoseIcon);    break;
            case GAGNE: smileButton.setIcon(smileyWinIcon);     break;
            default:    smileButton.setIcon(smileyNeutralIcon); break;
        }
    }

    // Construction de l'interface
    private void placerLesComposantsGraphiques() {
        setLayout(new BorderLayout());
        setBackground(new Color(192, 192, 192));
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        add(creerBarreHaut(), BorderLayout.NORTH);

        JPanel grillePanel = plateau.isHexagonal() ? creerGrilleHexagonale() : creerGrilleCarree();
        JPanel grilleWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        grilleWrapper.setBackground(new Color(192, 192, 192));
        grilleWrapper.setBorder(BorderFactory.createEmptyBorder(gridPadding, gridPadding, gridPadding, gridPadding));
        grilleWrapper.add(grillePanel);
        add(grilleWrapper, BorderLayout.CENTER);

        add(creerBarreBas(), BorderLayout.SOUTH);
    }

    private JPanel creerBarreBas() {
        JPanel barre = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
        barre.setBackground(new Color(192, 192, 192));
        JButton menuButton = new JButton("Menu principal");
        menuButton.addActionListener(e -> { arreterTimer(); menuAction.run(); });
        barre.add(menuButton);
        return barre;
    }

    private JPanel creerGrilleCarree() {
        JPanel grillePanel = new JPanel(new GridLayout(sizeY, sizeX));
        grillePanel.setBackground(new Color(192, 192, 192));
        grillePanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        grillePanel.setPreferredSize(new Dimension(sizeX * pxCase, sizeY * pxCase));

        tabJLabel = new JLabel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel("", SwingConstants.CENTER);
                jlab.setPreferredSize(new Dimension(pxCase, pxCase));
                jlab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                jlab.setOpaque(true);
                jlab.setBackground(new Color(192, 192, 192));
                tabJLabel[x][y] = jlab;
                ajouterEcouteSourisCase(jlab, x, y);
                grillePanel.add(jlab);
            }
        }
        return grillePanel;
    }

    private JPanel creerGrilleHexagonale() {
        JPanel grillePanel = new JPanel(null);
        grillePanel.setBackground(new Color(192, 192, 192));
        grillePanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        tabJLabel = new JLabel[sizeX][sizeY];

        final int decalageImpair = (int) Math.round(hexCellWidth / 2.0);

        for (int y = 0; y < sizeY; y++) {
            int decalageLigne = (y % 2 == 1) ? decalageImpair : 0;
            for (int x = 0; x < sizeX; x++) {
                HexCellLabel jlab = new HexCellLabel(hexCellWidth, hexCellHeight);
                jlab.setBounds(x * hexColStep + decalageLigne, y * hexRowStep, hexCellWidth, hexCellHeight);
                tabJLabel[x][y] = jlab;
                ajouterEcouteSourisCase(jlab, x, y);
                grillePanel.add(jlab);
            }
        }

        int largeur = sizeX * hexColStep + decalageImpair + 1;
        int hauteur = (sizeY - 1) * hexRowStep + hexCellHeight + 1;
        grillePanel.setPreferredSize(new Dimension(largeur, hauteur));
        return grillePanel;
    }

    private void ajouterEcouteSourisCase(JLabel jlab, int x, int y) {
        jlab.addMouseListener(new MouseAdapter() {

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
                if (SwingUtilities.isLeftMouseButton(e)
                        && !c.isVisible()
                        && !c.isFlagged()
                        && jeu.isEnCours()) {
                    if (jlab instanceof HexCellLabel) {
                        ((HexCellLabel) jlab).setLowered(true);
                    } else {
                        jlab.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                    }
                    setEtatSmiley(EtatSmiley.CLIC);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Case c = plateau.getCases()[x][y];
                if (!c.isVisible()) {
                    if (jlab instanceof HexCellLabel) {
                        ((HexCellLabel) jlab).setLowered(false);
                    } else {
                        jlab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                    }
                }
                if (jeu.isEnCours()) setEtatSmiley(EtatSmiley.NORMAL);
            }
        });
    }

    // Barre du haut
    private JPanel creerBarreHaut() {
        JPanel barre = new JPanel(new BorderLayout());
        barre.setBackground(new Color(192, 192, 192));
        barre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(6, 6, 6, 6),
                BorderFactory.createBevelBorder(BevelBorder.LOWERED)
        ));

        compteurMinesLabel = creerLabelLCD("020");
        JPanel panelGauche = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panelGauche.setBackground(new Color(192, 192, 192));
        panelGauche.add(compteurMinesLabel);

        smileButton = new JButton();
        smileButton.setPreferredSize(new Dimension(40, 40));
        smileButton.setFocusPainted(false);
        smileButton.setContentAreaFilled(false);
        smileButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        smileButton.setIcon(smileyNeutralIcon);
        smileButton.addActionListener(e -> { arreterTimer(); restartAction.run(); });

        JPanel panelCentre = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        panelCentre.setBackground(new Color(192, 192, 192));
        panelCentre.add(smileButton);

        timerLabel = creerLabelLCD("000");
        JPanel panelDroit = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        panelDroit.setBackground(new Color(192, 192, 192));
        panelDroit.add(timerLabel);

        barre.add(panelGauche, BorderLayout.WEST);
        barre.add(panelCentre, BorderLayout.CENTER);
        barre.add(panelDroit,  BorderLayout.EAST);
        return barre;
    }

    private JLabel creerLabelLCD(String texte) {
        JLabel label = new JLabel(texte, SwingConstants.RIGHT);
        label.setFont(new Font("Monospaced", Font.BOLD, 22));
        label.setForeground(Color.RED);
        label.setBackground(Color.BLACK);
        label.setOpaque(true);
        label.setPreferredSize(new Dimension(56, 30));
        label.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        return label;
    }

    private String formaterLCD(int valeur) {
        valeur = Math.max(-99, Math.min(999, valeur));
        return (valeur < 0) ? String.format("-%02d", Math.abs(valeur)) : String.format("%03d", valeur);
    }

    // Timer Swing: il incrémente le modèle, la vue lit le modèle
    private void demarrerTimer() {
        timerLabel.setText(formaterLCD(0));
        swingTimer = new Timer(1000, e -> {
            jeu.incrementerTemps();                      // modèle mis à jour
            timerLabel.setText(formaterLCD(jeu.getTemps())); // vue lit le modèle
        });
        swingTimer.start();
    }

    private void arreterTimer() {
        if (swingTimer != null) swingTimer.stop();
    }

    // Mise à jour de l'affichage (Observer)
    private void mettreAJourAffichage() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Case c     = plateau.getCases()[x][y];
                JLabel lab = tabJLabel[x][y];

                if (c.isVisible()) {
                    if (lab instanceof HexCellLabel) ((HexCellLabel) lab).setLowered(true);
                    else lab.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                    lab.setIcon(null);
                    lab.setText("");
                    if (c.getValeur() == -1) {
                        lab.setBackground(new Color(255, 80, 80));
                        lab.setIcon(bombIcon);
                    } else {
                        lab.setBackground(new Color(192, 192, 192));
                        lab.setIcon(numberIcons[c.getValeur()]);
                    }
                } else if (c.isFlagged()) {
                    if (lab instanceof HexCellLabel) ((HexCellLabel) lab).setLowered(false);
                    else lab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                    lab.setBackground(new Color(192, 192, 192));
                    lab.setText("");
                    lab.setIcon(flagIcon);
                } else {
                    if (lab instanceof HexCellLabel) ((HexCellLabel) lab).setLowered(false);
                    else lab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                    lab.setBackground(new Color(192, 192, 192));
                    lab.setText("");
                    lab.setIcon(null);
                }
            }
        }

        //  Compteur drapeaux : la vue lit plateau.getNbFlags()
        compteurMinesLabel.setText(formaterLCD(plateau.getNbMines() - plateau.getNbFlags()));
    }

   @Override
    public void update(Observable o, Object arg) {
        mettreAJourAffichage();

        if (jeu.isPerdu()) {
            arreterTimer();
            setEtatSmiley(EtatSmiley.PERDU);

            Timer delai = new Timer(300, e -> {
                int choix = JOptionPane.showOptionDialog(
                    this,
                    "Vous avez perdu !",
                    "Partie terminée",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Rejouer", "Retour au menu "},
                    "Rejouer"
                );
                if (choix == JOptionPane.YES_OPTION) {
                    arreterTimer();
                    restartAction.run();
                } else {
                    arreterTimer();
                    menuAction.run();
                }
            });
            delai.setRepeats(false); 
            delai.start();

        } else if (jeu.isGagne()) {
            arreterTimer();
            setEtatSmiley(EtatSmiley.GAGNE);

            Timer delai = new Timer(300, e -> {
                int choix = JOptionPane.showOptionDialog(
                    this,
                    "Félicitations, vous avez gagné !",
                    "Victoire !",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Rejouer", "Retour au menu "},
                    "Rejouer"
                );
                if (choix == JOptionPane.YES_OPTION) {
                    arreterTimer();
                    restartAction.run();
                } else {
                    arreterTimer();
                    menuAction.run();
                }
            });
            delai.setRepeats(false);
            delai.start();
        }
    }
}