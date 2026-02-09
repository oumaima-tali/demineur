package VueControleur;

import java.awt.*;
import javax.swing.*;
import modele.jeu.Jeu;

public class MenuPrincipal extends JFrame {
    private boolean jeuLance = false;

    public MenuPrincipal() {
        setTitle("Demineur - Menu Principal");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Color beige = new Color(240, 217, 181);
        Color marron = new Color(125, 95, 60);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(beige);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        setContentPane(mainPanel);

        JLabel titreLabel = new JLabel(" JEU DE DEMINEUR");
        titreLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titreLabel.setForeground(marron);
        titreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(titreLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(sousTitreLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(modePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(iaOptionsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(startButton);

        setVisible(true);
    }

    private JPanel createTitledPanel(String title, Color bg, Color text) {
        JPanel panel = new JPanel();
        panel.setBackground(bg);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(text), title));
        return panel;
    }

    private void styleRadio(JRadioButton rb, Color bg, Color fg) {
        rb.setBackground(bg);
        rb.setForeground(fg);
        rb.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    private void lancerJeu() {
        Jeu jeu = new Jeu();
        jeu.configurerIA(contreIA, iaEstBlanc, niveauIA);
        VueControleur vc = new VueControleur(jeu);
        vc.setVisible(true);
        if (contreIA && iaEstBlanc) {
            try { Thread.sleep(600); } catch (InterruptedException e) {}
            jeu.jouerCoupIAInitial();
        }
    }

    public boolean estJeuLance() {
        return jeuLance;
    }
}
