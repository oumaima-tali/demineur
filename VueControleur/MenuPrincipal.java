package VueControleur;

import java.awt.*;
import javax.swing.*;
import modele.jeu.Difficulte;
import modele.jeu.Jeu;

public class MenuPrincipal extends JFrame {

    private JComboBox<Difficulte> difficulteCombo;

    public MenuPrincipal() {
        setTitle("Démineur ");
        setSize(500, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        afficherMenu();
        setVisible(true);
    }

    private Difficulte getDifficulteSelectionnee() {
        Difficulte difficulte = (Difficulte) difficulteCombo.getSelectedItem();
        return difficulte != null ? difficulte : Difficulte.FACILE;
    }

    private void lancerJeu(String typeGrille, Difficulte difficulte) {
        Jeu jeu = new Jeu(typeGrille, difficulte);
        setContentPane(new VueControleur(jeu, difficulte, () -> lancerJeu(typeGrille, difficulte), this::afficherMenu));
        revalidate();
        repaint();
    }

    public void afficherMenu() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titreLabel = new JLabel("JEU DE DEMINEUR");
        titreLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titreLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel difficulteLabel = new JLabel("Difficulté");
        difficulteLabel.setFont(new Font("Serif", Font.BOLD, 20));
        difficulteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(difficulteLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        difficulteCombo = new JComboBox<>(Difficulte.values());
        difficulteCombo.setMaximumSize(new Dimension(180, 32));
        difficulteCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(difficulteCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 24)));

        JButton startCarreButton = new JButton("Grille Carrée");
        startCarreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startCarreButton.addActionListener(e -> lancerJeu("carre", getDifficulteSelectionnee()));
        mainPanel.add(startCarreButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton startHexButton = new JButton("Grille Hexagonale");
        startHexButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startHexButton.addActionListener(e -> lancerJeu("hexagonal", getDifficulteSelectionnee()));
        mainPanel.add(startHexButton);

        setContentPane(mainPanel);
        revalidate();
        repaint();
    }
}