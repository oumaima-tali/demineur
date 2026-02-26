package VueControleur;

import java.awt.*;
import javax.swing.*;
import modele.jeu.Jeu;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("Démineur ");
        setSize(500, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        setContentPane(mainPanel);

        JLabel titreLabel = new JLabel("JEU DE DEMINEUR");
        titreLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titreLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton startCarreButton = new JButton("Grille Carrée");
        startCarreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startCarreButton.addActionListener(e -> lancerJeu("carre"));
        mainPanel.add(startCarreButton);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton startHexButton = new JButton("Grille Hexagonale");
        startHexButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startHexButton.addActionListener(e -> lancerJeu("hexagonal"));
        mainPanel.add(startHexButton);

        setVisible(true);
    }

    private void lancerJeu(String typeGrille) {
        Jeu jeu = new Jeu(typeGrille);
        setContentPane(new VueControleur(jeu, () -> lancerJeu(typeGrille)));
        revalidate();
        repaint();
    }
}