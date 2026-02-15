package VueControleur;

import Modele.jeu.Jeu;
import java.awt.*;
import javax.swing.*;

public class MenuPrincipal extends JFrame {
    private boolean jeuLance = false;


    public MenuPrincipal() {
        setTitle("Démineur - Menu Principal");
        setSize(500, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.LIGHT_GRAY); // Set a background color suitable for Minesweeper
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        setContentPane(mainPanel);

        JLabel titreLabel = new JLabel("JEU DE DEMINEUR");
        titreLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titreLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton startButton = new JButton("Démarrer le jeu");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> lancerJeu());
        mainPanel.add(startButton);

        setVisible(true);
    }

    private void lancerJeu() {
        Jeu jeu = new Jeu();
        setContentPane(new VueControleur(jeu));
        revalidate();
        repaint();
    }

    public boolean estJeuLance() {
        return jeuLance;
    }
}