package identification;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Petit programme de test : vérifie que Java arrive à se connecter à MariaDB.
 */
public class TestConnexion {

    public static void main(String[] args) {
        try (Connection connexion = BaseDeDonnees.getConnexion()) {
            System.out.println("Connexion réussie à MariaDB !");
        } catch (SQLException e) {
            System.out.println("Échec de la connexion : " + e.getMessage());
        }
    }

}