package VueControleur;

import java.awt.*;
import javax.swing.*;
import modele.jeu.Difficulte;
import modele.jeu.Jeu;

// fenetre principale : menu de demarrage
public class MenuPrincipal extends JFrame {

    private JComboBox<Difficulte> comboNiveau;

    public MenuPrincipal() {
        setTitle("Demineur");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        afficherMenu();
        setVisible(true);
    }

    private Difficulte getNiveauSelectionne() {
        Difficulte d = (Difficulte) comboNiveau.getSelectedItem();
        return (d != null) ? d : Difficulte.FACILE;
    }

    // cree le jeu et remplace le contenu de la fenetre
    private void lancerJeu(String typeGrille, Difficulte difficulte) {
        Jeu jeu = new Jeu(typeGrille, difficulte);

        // on cree aussi la vue console pour montrer l'independance du modele
        new VueConsole(jeu.getPlateau());

        VueControleur vue = new VueControleur(
            jeu,
            difficulte,
            new Runnable() {
                @Override
                public void run() { lancerJeu(typeGrille, difficulte); }
            },
            new Runnable() {
                @Override
                public void run() { afficherMenu(); }
            }
        );

        setContentPane(vue);
        pack();
        setLocationRelativeTo(null);
    }

    public void afficherMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // titre
        JLabel titre = new JLabel("JEU DE DEMINEUR");
        titre.setFont(new Font("Serif", Font.BOLD, 32));
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titre);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // choix du niveau
        JLabel labelNiveau = new JLabel("Niveau de difficulte :");
        labelNiveau.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(labelNiveau);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        comboNiveau = new JComboBox<Difficulte>(Difficulte.values());
        comboNiveau.setMaximumSize(new Dimension(200, 30));
        comboNiveau.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(comboNiveau);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // boutons de jeu
        JButton btnCarre = new JButton("Grille carree (classique)");
        btnCarre.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCarre.addActionListener(e -> lancerJeu("carre", getNiveauSelectionne()));
        panel.add(btnCarre);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton btnHex = new JButton("Grille hexagonale (avancee) ");
        btnHex.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnHex.addActionListener(e -> lancerJeu("hexagonal", getNiveauSelectionne()));
        panel.add(btnHex);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }
}