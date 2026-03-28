public class Main {
    public static void main(String[] args) {
        // lancement de la fenetre sur le bon thread swing
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VueControleur.MenuPrincipal();
            }
        });
    }
}