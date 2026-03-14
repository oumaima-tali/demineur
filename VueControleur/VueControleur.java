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


    private JLabel compteurMinesLabel;
    private JLabel timerLabel;
    private SmileyButton smileButton;


    private Timer timer;
    private int secondes;


   
    public enum EtatSmiley { NORMAL, CLIC, PERDU, GAGNE }


    
    private static class SmileyButton extends JButton {


        private EtatSmiley etat = EtatSmiley.NORMAL;
        private static final int TAILLE = 36;


        public SmileyButton() {
            setPreferredSize(new Dimension(TAILLE + 4, TAILLE + 4));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        }


        public void setEtat(EtatSmiley e) {
            etat = e;
            repaint();
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


            int w = getWidth();
            int h = getHeight();
            int d = Math.min(w, h) - 6;
            int ox = (w - d) / 2;
            int oy = (h - d) / 2;


            g2.setColor(new Color(255, 220, 0));
            g2.fillOval(ox, oy, d, d);


            g2.setColor(new Color(160, 120, 0));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(ox, oy, d, d);


            int eyeY = oy + d / 3;
            int eyeSize = Math.max(2, d / 8);
            int leftEyeX  = ox + d * 3 / 8 - eyeSize / 2;
            int rightEyeX = ox + d * 5 / 8 - eyeSize / 2;


            if (etat == EtatSmiley.PERDU) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1.5f));
                dessinerCroix(g2, leftEyeX,  eyeY, eyeSize);
                dessinerCroix(g2, rightEyeX, eyeY, eyeSize);
            } else if (etat == EtatSmiley.CLIC) {
                g2.setColor(Color.BLACK);
                g2.drawOval(leftEyeX,  eyeY, eyeSize, eyeSize);
                g2.drawOval(rightEyeX, eyeY, eyeSize, eyeSize);
            } else {
                g2.setColor(Color.BLACK);
                g2.fillOval(leftEyeX,  eyeY, eyeSize, eyeSize);
                g2.fillOval(rightEyeX, eyeY, eyeSize, eyeSize);
            }


            if (etat == EtatSmiley.GAGNE) {
                g2.setColor(new Color(0, 0, 0, 180));
                int gSize = eyeSize + 4;
                g2.fillRoundRect(leftEyeX  - 2, eyeY - 1, gSize, gSize - 1, 3, 3);
                g2.fillRoundRect(rightEyeX - 2, eyeY - 1, gSize, gSize - 1, 3, 3);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(leftEyeX + gSize - 2, eyeY + gSize / 2,
                            rightEyeX - 2,        eyeY + gSize / 2);
            }


            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.8f));
            int boucheY = oy + d * 3 / 5;
            int boucheW = d * 2 / 5;
            int boucheX = ox + (d - boucheW) / 2;
            int boucheH = d / 5;


            if (etat == EtatSmiley.PERDU) {
                g2.drawArc(boucheX, boucheY, boucheW, boucheH, 0, 180);
            } else if (etat == EtatSmiley.CLIC) {
                int r = d / 10;
                g2.drawOval(ox + d / 2 - r, boucheY, r * 2, r * 2);
            } else {
                g2.drawArc(boucheX, boucheY - boucheH / 2, boucheW, boucheH, 180, 180);
            }


            g2.dispose();
        }


        private void dessinerCroix(Graphics2D g2, int x, int y, int size) {
            g2.drawLine(x, y, x + size, y + size);
            g2.drawLine(x + size, y, x, y + size);
        }
    }


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
            Color fonce = lowered ? Color.WHITE : Color.DARK_GRAY;
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
            int[] x = { width / 2, width - 1, width - 1, width / 2, 0, 0 };
            int[] y = { 0, height / 4, (3 * height) / 4, height - 1, (3 * height) / 4, height / 4 };
            return new Polygon(x, y, 6);
        }
    }



    public VueControleur(Jeu _jeu, Difficulte _difficulte, Runnable _restartAction, Runnable _menuAction) {
        jeu           = _jeu;
        restartAction = _restartAction;
        menuAction    = _menuAction;
        plateau       = jeu.getPlateau();
        sizeX         = plateau.getSizeX();
        sizeY         = plateau.getSizeY();
        ConfigAffichage config = new ConfigAffichage(_difficulte, plateau.isHexagonal());
        hexRadius     = config.getHexRadius();
        gridPadding   = config.getGridPadding();
        pxCase        = config.getCellSize();
        hexCellWidth  = (int) Math.round(Math.sqrt(3) * hexRadius);
        hexCellHeight = 2 * hexRadius;
        hexColStep    = hexCellWidth;
        hexRowStep    = (int) Math.round(1.5 * hexRadius);


        chargerImages();
        placerLesComposantsGraphiques();
        demarrerTimer();
        plateau.addObserver(this);
        mettreAJourAffichage();
    }


   
    private void chargerImages() {
        flagIcon    = chargerIcone("images/flag.png");
        bombIcon    = chargerIcone("images/bomb.png");
        numberIcons = new ImageIcon[9];
        for (int i = 0; i <= 8; i++) {
            numberIcons[i] = chargerIcone("images/" + i + ".png");
        }
    }


    private ImageIcon chargerIcone(String chemin) {
        URL url = getClass().getClassLoader().getResource(chemin);
        if (url == null) {
            System.err.println("[WARN] Image introuvable : " + chemin);
            return null;
        }
        ImageIcon icon = new ImageIcon(url);
        int iconSize = plateau.isHexagonal()
                ? Math.max(12, Math.min(hexCellWidth, hexCellHeight) - 8)
                : Math.max(12, pxCase - 10);
        Image scaled = icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }


    
    private void placerLesComposantsGraphiques() {
        setLayout(new BorderLayout());
        setBackground(new Color(192, 192, 192));
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));


        add(creerBarreHaut(), BorderLayout.NORTH);


        JPanel grillePanel = plateau.isHexagonal() ? creerGrilleHexagonale() : creerGrilleCarree();


        JPanel grilleWrapper = new JPanel(new BorderLayout());
        grilleWrapper.setBackground(new Color(192, 192, 192));
        grilleWrapper.setBorder(BorderFactory.createEmptyBorder(gridPadding, gridPadding, gridPadding, gridPadding));
        grilleWrapper.add(grillePanel, BorderLayout.CENTER);
        add(grilleWrapper, BorderLayout.CENTER);

        add(creerBarreBas(), BorderLayout.SOUTH);
    }

    private JPanel creerBarreBas() {
        JPanel barre = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
        barre.setBackground(new Color(192, 192, 192));

        JButton menuButton = new JButton("Menu principal");
        menuButton.addActionListener(e -> {
            arreterTimer();
            menuAction.run();
        });
        barre.add(menuButton);
        return barre;
    }


    private JPanel creerGrilleCarree() {
        JPanel grillePanel = new JPanel(new GridLayout(sizeY, sizeX));
        grillePanel.setBackground(new Color(192, 192, 192));
        grillePanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

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

        for (int y = 0; y < sizeY; y++) {
            int decalageLigne = (y % 2 == 1) ? (hexCellWidth / 2) : 0;
            for (int x = 0; x < sizeX; x++) {
                HexCellLabel jlab = new HexCellLabel(hexCellWidth, hexCellHeight);
                jlab.setBounds(x * hexColStep + decalageLigne, y * hexRowStep, hexCellWidth, hexCellHeight);
                tabJLabel[x][y] = jlab;
                ajouterEcouteSourisCase(jlab, x, y);
                grillePanel.add(jlab);
            }
        }

        int largeur = sizeX * hexColStep + (hexCellWidth / 2) + 2;
        int hauteur = (sizeY - 1) * hexRowStep + hexCellHeight + 2;
        grillePanel.setPreferredSize(new Dimension(largeur, hauteur));
        return grillePanel;
    }


    private void ajouterEcouteSourisCase(JLabel jlab, int x, int y) {
        jlab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!jeu.isEnCours()) {
                    return;
                }

                Case c = plateau.getCases()[x][y];
                if (SwingUtilities.isRightMouseButton(e)) {
                    c.toggleFlag();
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    if (c.isVisible()) {
                        plateau.decouvrirCasesAdjacentes(c);
                    } else {
                        plateau.decouvrirCase(c);
                    }
                }

                plateau.notifierObservateurs();
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
                    smileButton.setEtat(EtatSmiley.CLIC);
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
                if (jeu.isEnCours()) {
                    smileButton.setEtat(EtatSmiley.NORMAL);
                }
            }
        });
    }


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


        smileButton = new SmileyButton();
        smileButton.addActionListener(e -> {
            arreterTimer(); 
            restartAction.run();
        });


        JPanel panelCentre = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        panelCentre.setBackground(new Color(192, 192, 192));
        panelCentre.add(smileButton);


        timerLabel = creerLabelLCD("000");
        JPanel panelDroit = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        panelDroit.setBackground(new Color(192, 192, 192));
        panelDroit.add(timerLabel);


        barre.add(panelGauche,  BorderLayout.WEST);
        barre.add(panelCentre,  BorderLayout.CENTER);
        barre.add(panelDroit,   BorderLayout.EAST);


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
        if (valeur < 0) {
            return String.format("-%02d", Math.abs(valeur));
        }
        return String.format("%03d", valeur);
    }


    private void demarrerTimer() {
        secondes = 0;
        timerLabel.setText(formaterLCD(0));
        timer = new Timer(1000, e -> {
            secondes++;
            timerLabel.setText(formaterLCD(Math.min(secondes, 999)));
        });
        timer.start();
    }


    private void arreterTimer() {
        if (timer != null) {
            timer.stop();
        }
    }


    
    private void mettreAJourAffichage() {
        int flagsPlaces = 0;


        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Case c     = plateau.getCases()[x][y];
                JLabel lab = tabJLabel[x][y];


                if (c.isVisible()) {
                    int valeur = c.getValeur();
                    if (lab instanceof HexCellLabel) {
                        ((HexCellLabel) lab).setLowered(true);
                    } else {
                        lab.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                    }
                    lab.setIcon(null);
                    lab.setText("");


                    if (valeur == -1) {
                        lab.setBackground(new Color(255, 80, 80));
                        lab.setIcon(bombIcon);
                    } else {
                        lab.setBackground(new Color(192, 192, 192));
                        lab.setIcon(numberIcons[valeur]);
                    }


                } else if (c.isFlagged()) {
                    if (lab instanceof HexCellLabel) {
                        ((HexCellLabel) lab).setLowered(false);
                    } else {
                        lab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                    }
                    lab.setBackground(new Color(192, 192, 192));
                    lab.setText("");
                    lab.setIcon(flagIcon);
                    flagsPlaces++;


                } else {
                    if (lab instanceof HexCellLabel) {
                        ((HexCellLabel) lab).setLowered(false);
                    } else {
                        lab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                    }
                    lab.setBackground(new Color(192, 192, 192));
                    lab.setText("");
                    lab.setIcon(null);
                }
            }
        }


        compteurMinesLabel.setText(formaterLCD(plateau.getNbMines() - flagsPlaces));
    }


    
    @Override
    public void update(Observable o, Object arg) {
        mettreAJourAffichage();


        if (jeu.isPerdu()) {
            arreterTimer();
            smileButton.setEtat(EtatSmiley.PERDU);
        } else if (jeu.isGagne()) {
            arreterTimer();
            smileButton.setEtat(EtatSmiley.GAGNE);
        }
    }
}