package VueControleur;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import modele.jeu.Jeu;
import modele.plateau.Case;
import modele.plateau.Plateau;

public class VueControleur extends JPanel implements Observer {
    private Plateau plateau;
    private Jeu jeu;
    private final Runnable restartAction;
    private final int sizeX;
    private final int sizeY;
    private final int pxCase;
    private final int hexRadius;
    private final int gridPadding;
    private JLabel[][] tabJLabel;
    private JPanel grillePanel;
    private HexGridPanel hexGridPanel;
    private final boolean isHexagonal;
    private ImageIcon flagIcon;
    private ImageIcon bombIcon;
    private ImageIcon[] numberIcons;
    private JButton restartButton;
    private JLabel statusLabel;

    public VueControleur(Jeu _jeu, Runnable _restartAction) {
        jeu = _jeu;
        restartAction = _restartAction;
        plateau = jeu.getPlateau();
        sizeX = plateau.getSizeX();
        sizeY = plateau.getSizeY();
        isHexagonal = plateau.isHexagonal();
        pxCase = plateau.getSquareCellSize();
        hexRadius = plateau.getHexRadius();
        gridPadding = plateau.getGridPadding();

        chargerIcônes();
        
        placerLesComposantsGraphiques();
        plateau.addObserver(this);
        mettreAJourAffichage();
    }

    private void chargerIcônes() {
        int iconSize = isHexagonal ? (int) (hexRadius * 1.3) : (pxCase - 10);

        flagIcon = chargerEtRedimensionner("images/flag.png", iconSize);
        bombIcon = chargerEtRedimensionner("images/bomb.png", iconSize);

        numberIcons = new ImageIcon[9];
        for (int i = 0; i <= 8; i++) {
            numberIcons[i] = chargerEtRedimensionner("images/" + i + ".png", iconSize);
        }
    }

    private ImageIcon chargerEtRedimensionner(String chemin, int taille) {
        ImageIcon icon = new ImageIcon(chemin);
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(taille, taille, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    private void placerLesComposantsGraphiques() {
        setLayout(new BorderLayout());

        if (isHexagonal) {
            hexGridPanel = new HexGridPanel();
            grillePanel = new JPanel(new GridBagLayout());
            grillePanel.add(hexGridPanel);
        } else {
            JPanel grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX));
            tabJLabel = new JLabel[sizeX][sizeY];

            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    JLabel jlab = new JLabel("", SwingConstants.CENTER);
                    jlab.setPreferredSize(new Dimension(pxCase, pxCase));
                    jlab.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                    jlab.setOpaque(true);
                    jlab.setBackground(Color.LIGHT_GRAY);

                    tabJLabel[x][y] = jlab;

                    final int xx = x;
                    final int yy = y;

                    jlab.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (!jeu.isEnCours()) {
                                return;
                            }

                            Case c = plateau.getCases()[xx][yy];

                            if (SwingUtilities.isRightMouseButton(e)) {
                                c.toggleFlag();
                            } else if (SwingUtilities.isLeftMouseButton(e)) {
                                plateau.decouvrirCase(c);
                            }

                            plateau.notifierObservateurs();
                        }
                    });
                    grilleJLabels.add(jlab);
                }
            }
            grillePanel = grilleJLabels;
        }

        add(grillePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusLabel = new JLabel(" ");
        restartButton = new JButton("Recommencer");
        restartButton.setVisible(false);
        restartButton.addActionListener(e -> restartAction.run());
        bottomPanel.add(statusLabel);
        bottomPanel.add(restartButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void mettreAJourAffichage() {
        if (isHexagonal) {
            if (hexGridPanel != null) {
                hexGridPanel.repaint();
            }
            return;
        }

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Case c = plateau.getCases()[x][y];
                JLabel label = tabJLabel[x][y];
                
                if (c.isVisible()) {
                    int valeur = c.getValeur();
                    label.setIcon(null);
                    if (valeur == -1) {
                        label.setIcon(bombIcon);
                    } else if (valeur >= 0) {
                        label.setIcon(numberIcons[valeur]);
                    }
                } else if (c.isFlagged()) {
                    label.setText("");
                    label.setIcon(flagIcon);
                } else {
                    label.setText("");
                    label.setIcon(null);
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        mettreAJourAffichage();

        if (jeu.isPerdu()) {
            statusLabel.setText("Perdu !");
            restartButton.setVisible(true);
        } else if (jeu.isGagne()) {
            statusLabel.setText("Gagné !");
            restartButton.setVisible(true);
        } else {
            statusLabel.setText(" ");
            restartButton.setVisible(false);
        }
    }

    private class HexGridPanel extends JPanel {
        private final Polygon[][] hexagones;
        private final double hexWidth;
        private final double rowStep;

        HexGridPanel() {
            this.hexWidth = Math.sqrt(3) * hexRadius;
            this.rowStep = 1.5 * hexRadius;
            this.hexagones = new Polygon[sizeX][sizeY];

            calculerHexagones();
            setOpaque(true);
            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!jeu.isEnCours()) {
                        return;
                    }

                    Point point = e.getPoint();
                    int[] cell = trouverCase(point.x, point.y);
                    if (cell == null) {
                        return;
                    }

                    Case c = plateau.getCases()[cell[0]][cell[1]];

                    if (SwingUtilities.isRightMouseButton(e)) {
                        c.toggleFlag();
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        plateau.decouvrirCase(c);
                    }

                    plateau.notifierObservateurs();
                }
            });
        }

        private void calculerHexagones() {
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    double centerX = gridPadding + (hexWidth / 2.0) + (x * hexWidth) + ((y % 2) * (hexWidth / 2.0));
                    double centerY = gridPadding + hexRadius + (y * rowStep);
                    hexagones[x][y] = creerHexagone(centerX, centerY);
                }
            }
        }

        private Polygon creerHexagone(double centerX, double centerY) {
            int[] xPoints = new int[6];
            int[] yPoints = new int[6];

            for (int i = 0; i < 6; i++) {
                double angle = Math.toRadians(60 * i - 30);
                xPoints[i] = (int) Math.round(centerX + hexRadius * Math.cos(angle));
                yPoints[i] = (int) Math.round(centerY + hexRadius * Math.sin(angle));
            }
            return new Polygon(xPoints, yPoints, 6);
        }

        private int[] trouverCase(int px, int py) {
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    if (hexagones[x][y].contains(px, py)) {
                        return new int[] {x, y};
                    }
                }
            }
            return null;
        }

        @Override
        public Dimension getPreferredSize() {
            int width = (int) Math.ceil(gridPadding * 2 + (sizeX * hexWidth) + (hexWidth / 2.0));
            int height = (int) Math.ceil(gridPadding * 2 + (2 * hexRadius) + ((sizeY - 1) * rowStep));
            return new Dimension(width, height);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    Case c = plateau.getCases()[x][y];
                    Polygon hex = hexagones[x][y];

                    g2.setColor(c.isVisible() ? new Color(230, 230, 230) : Color.LIGHT_GRAY);
                    g2.fillPolygon(hex);

                    g2.setColor(Color.DARK_GRAY);
                    g2.drawPolygon(hex);

                    if (c.isVisible()) {
                        if (c.getValeur() == -1) {
                            dessinerImageCentree(g2, bombIcon.getImage(), hex.getBounds());
                        } else if (c.getValeur() >= 0) {
                            dessinerImageCentree(g2, numberIcons[c.getValeur()].getImage(), hex.getBounds());
                        }
                    } else if (c.isFlagged()) {
                        dessinerImageCentree(g2, flagIcon.getImage(), hex.getBounds());
                    }
                }
            }

            g2.dispose();
        }

        private void dessinerImageCentree(Graphics2D g2, Image image, Rectangle bounds) {
            int w = image.getWidth(null);
            int h = image.getHeight(null);
            int x = bounds.x + (bounds.width - w) / 2;
            int y = bounds.y + (bounds.height - h) / 2;
            g2.drawImage(image, x, y, null);
        }
    }
}