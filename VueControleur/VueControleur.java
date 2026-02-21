package VueControleur;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.util.Observable;
import java.util.Observer;
import Modele.jeu.Jeu;
import Modele.plateau.Case;
import Modele.plateau.Plateau;

public class VueControleur extends JPanel implements Observer {
    private Plateau plateau;
    private Jeu jeu;
    private final Runnable restartAction;
    private final int sizeX;
    private final int sizeY;
    private static final int pxCase = 40;
    private JLabel[][] tabJLabel;
    private ImageIcon flagIcon;
    private ImageIcon bombIcon;
    private JButton restartButton;
    private JLabel statusLabel;

    public VueControleur(Jeu _jeu, Runnable _restartAction) {
        jeu = _jeu;
        restartAction = _restartAction;
        plateau = jeu.getPlateau();
        sizeX = Plateau.SIZE_X;
        sizeY = Plateau.SIZE_Y;
        
        // Charger l'image du drapeau
        flagIcon = new ImageIcon("images/flag.png");
        Image img = flagIcon.getImage();
        Image scaledImg = img.getScaledInstance(pxCase - 10, pxCase - 10, Image.SCALE_SMOOTH);
        flagIcon = new ImageIcon(scaledImg);
        
        placerLesComposantsGraphiques();
        plateau.addObserver(this);
        mettreAJourAffichage();
    }

    private void placerLesComposantsGraphiques() {
        setLayout(new BorderLayout());

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
                            // Clic droit : toggle flag
                            c.toggleFlag();
                        } else if (SwingUtilities.isLeftMouseButton(e)) {
                            // Clic gauche : découvrir avec propagation si valeur = 0
                            plateau.decouvrirCase(c);
                        }
                        
                        plateau.notifierObservateurs();
                    }
                });
                grilleJLabels.add(jlab);
            }
        }

        add(grilleJLabels, BorderLayout.CENTER);

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
        bombIcon = new ImageIcon("images/bomb.png");
        Image img = bombIcon.getImage();
        Image scaledImg = img.getScaledInstance(pxCase - 10, pxCase - 10, Image.SCALE_SMOOTH);
        bombIcon = new ImageIcon(scaledImg);

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Case c = plateau.getCases()[x][y];
                JLabel label = tabJLabel[x][y];
                
                if (c.isVisible()) {
                    int valeur = c.getValeur();
                    label.setIcon(null);
                    if (valeur == -1) {
                        label.setIcon(bombIcon);
                    } else {
                        label.setText(String.valueOf(valeur));
                    }
                } else if (c.isFlagged()) {
                    // Afficher le drapeau
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
        } else {
            statusLabel.setText(" ");
            restartButton.setVisible(false);
        }
    }
}