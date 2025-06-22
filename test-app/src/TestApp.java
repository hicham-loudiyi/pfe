public class TestApp {

    public static class User {
        private String name;
        
        public String getName() {
            return name;  // Retourne null si name n'est pas initialisé
        }
    }

    public static void main(String[] args) {
        System.out.println("Démarrage de l'application de test...");
        
        // Cas 1: Méthode sur un objet null
        User user = null;
        try {
            System.out.println("Longueur du nom: " + user.getName().length()); // NPE ici
        } catch (NullPointerException e) {
            System.out.println("Exception générée (cas 1):");
            e.printStackTrace();
        }

        // Cas 2: Champ non initialisé
        User user2 = new User();
        try {
            System.out.println("Longueur du nom: " + user2.getName().length()); // NPE ici
        } catch (NullPointerException e) {
            System.out.println("\nException générée (cas 2):");
            e.printStackTrace();
        }
    }
}