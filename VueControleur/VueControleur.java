package VueControleur;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import modele.jeu.Jeu;
import modele.plateau.Case;
import modele.plateau.Plateau;

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
    private ImageIcon[] numberIcons; 

    private JButton restartButton;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private Timer timer;
    private int secondes;

    public VueControleur(Jeu _jeu, Runnable _restartAction) {
        jeu = _jeu;
        restartAction = _restartAction;
        plateau = jeu.getPlateau();
        sizeX = plateau.getSizeX();
        sizeY = plateau.getSizeY();

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
        Image scaled = icon.getImage().getScaledInstance(pxCase - 10, pxCase - 10, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    
    private void placerLesComposantsGraphiques() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        timerLabel = new JLabel("Temps : 0 s");
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        topPanel.add(timerLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX));
        tabJLabel = new JLabel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel("", SwingConstants.CENTER);
                jlab.setPreferredSize(new Dimension(pxCase, pxCase));
                jlab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
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
                            // Clic gauche : decouvrir
                            plateau.decouvrirCase(c);
                        }

                        plateau.notifierObservateurs();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        Case c = plateau.getCases()[xx][yy];
                        if (SwingUtilities.isLeftMouseButton(e)
                                && !c.isVisible()
                                && !c.isFlagged()
                                && jeu.isEnCours()) {
                            jlab.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        Case c = plateau.getCases()[xx][yy];
                        if (!c.isVisible()) {
                            jlab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                        }
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

    private void demarrerTimer() {
        secondes = 0;
        timer = new Timer(1000, e -> {
            secondes++;
            timerLabel.setText("Temps : " + secondes + " s");
        });
        timer.start();
    }

    private void arreterTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

  
    private void mettreAJourAffichage() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Case c = plateau.getCases()[x][y];
                JLabel label = tabJLabel[x][y];

                if (c.isVisible()) {
                    int valeur = c.getValeur();
                    label.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                    label.setBackground(new Color(200, 200, 200));
                    label.setIcon(null);
                    label.setText("");

                    if (valeur == -1) {
                        label.setIcon(bombIcon);
                        label.setBackground(new Color(255, 80, 80));
                    } else {
                        label.setIcon(numberIcons[valeur]);
                    }

                } else if (c.isFlagged()) {
                    label.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                    label.setBackground(Color.LIGHT_GRAY);
                    label.setText("");
                    label.setIcon(flagIcon);

                } else {
                    label.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                    label.setBackground(Color.LIGHT_GRAY);
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
            arreterTimer();
            statusLabel.setText("Perdu !");
            restartButton.setVisible(true);
        } else if (jeu.isGagne()) {
            arreterTimer();
            statusLabel.setText("Gagne ! (" + secondes + " s)");
            restartButton.setVisible(true);
        } else {
            statusLabel.setText(" ");
            restartButton.setVisible(false);
        }
    }
}