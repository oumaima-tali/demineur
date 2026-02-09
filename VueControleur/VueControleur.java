package demineur.VueControleur.VueControleur;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;


import demineur.Modele.jeu.Jeu;
import demineur.Modele.plateau.Case;
import demineur.Modele.plateau.Plateau;


public class VueControleur extends JFrame implements Observer {
    private Plateau plateau;
    private Jeu jeu;
    private final int sizeX;
    private final int sizeY;
    private static final int pxCase = 90;

    //private ImageIcon ;


    private Case caseClic1;
    private Case caseClic2;


    private JLabel[][] tabJLabel;

    private JPanel infoPanel;
    private JLabel scoreLabel;
    private JLabel messageLabel;





    public VueControleur(Jeu _jeu) {
        jeu = _jeu;
        plateau = jeu.getPlateau();
        sizeX = plateau.SIZE_X;
        sizeY = plateau.SIZE_Y;
        chargerLesIcones();
        placerLesComposantsGraphiques();
        plateau.addObserver(this);
        mettreAJourAffichage();

    }


    private void chargerLesIcones() {
        //icoRoiBlanc = chargerIcone("Images/wK.png");
    }
    private ImageIcon chargerIcone(String urlIcone) {

        //ImageIcon icon = new ImageIcon(urlIcone);

        //Image img = icon.getImage().getScaledInstance(pxCase, pxCase, Image.SCALE_SMOOTH);
        //ImageIcon resizedIcon = new ImageIcon(img);

        //return resizedIcon;
    }



    //private ArrayList<Case> casesMarquees = new ArrayList<>();


            }
        }
    }


    private void placerLesComposantsGraphiques() {
        setTitle("Jeu de demineur");
        setResizable(false);
        setSize(sizeX * pxCase + 200, sizeY * pxCase);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());


        JPanel grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX));
        tabJLabel = new JLabel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();
                tabJLabel[x][y] = jlab;

                final int xx = x;
                final int yy = y;

                jlab.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (caseClic1 == null) {
                            caseClic1 = plateau.getCases()[xx][yy];
                grilleJLabels.add(jlab);
            }
        }

        getContentPane().add(grilleJLabels, BorderLayout.CENTER);

        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(200, sizeY * pxCase));

        getContentPane().add(infoPanel, BorderLayout.EAST);




        infoPanel.add(scrollPane);

    }



    private void mettreAJourAffichage() {

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {

                Case c = plateau.getCases()[x][y];

                if (c != null) {


                    } else {
                        tabJLabel[x][y].setIcon(null);
                    }


                }

            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        mettreAJourAffichage();


    }



    private void afficherFinPartie(String messageFinal) {
        JOptionPane.showMessageDialog(
            this,
            messageFinal + "\nRetour au menu.",
            "Fin de partie",
            JOptionPane.INFORMATION_MESSAGE
        );

        jeu.stopJeu();
        dispose();
        new MenuPrincipal().setVisible(true);
    }




}
