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
    private final int sizeX;
    private final int sizeY;
    private static final int pxCase = 40;

    private Case caseClic1;
    private JLabel[][] tabJLabel;

    public VueControleur(Jeu _jeu) {
        jeu = _jeu;
        plateau = jeu.getPlateau();
        sizeX = plateau.SIZE_X;
        sizeY = plateau.SIZE_Y;
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
                        caseClic1 = plateau.getCases()[xx][yy];
                        // Placeholder for future game logic
                    }
                });
                grilleJLabels.add(jlab);
            }
        }

        add(grilleJLabels, BorderLayout.CENTER);
    }

    private void mettreAJourAffichage() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Case c = plateau.getCases()[x][y];
                tabJLabel[x][y].setText("");
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        mettreAJourAffichage();
    }
}