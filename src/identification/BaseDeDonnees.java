package identification;

import java.io.FileInputStream;   // pour lire un fichier
import java.io.IOException;       // l'alarme quand un fichier ne peut pas être lu
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;      // pour lire un fichier clé=valeur

/**
 * Ouvre les connexions à la base MariaDB.
 * Les réglages (adresse, utilisateur, mot de passe) sont lus dans le fichier db.properties,
 * pour qu'aucun mot de passe ne soit écrit dans le code.
 */
public class BaseDeDonnees {

    // Le nom du fichier de configuration, à la racine du projet
    private static final String FICHIER_CONFIG = "db.properties";

    /**
     * Ouvre une nouvelle connexion à la base, avec les réglages de db.properties.
     *
     * @return une connexion ouverte
     * @throws SQLException si le fichier est illisible ou si la connexion échoue
     */
    public static Connection getConnexion() throws SQLException {

        // On lit le fichier de configuration
        Properties config = new Properties();

        try (FileInputStream fichier = new FileInputStream(FICHIER_CONFIG)) {
            config.load(fichier);
        } catch (IOException e) {
            // Fichier absent ou illisible : on transforme l'alarme en erreur de base de données
            throw new SQLException("Impossible de lire " + FICHIER_CONFIG + " : " + e.getMessage());
        }

        // On récupère chaque valeur grâce à sa clé
        String url = config.getProperty("db.url");
        String utilisateur = config.getProperty("db.utilisateur");
        String motDePasse = config.getProperty("db.motDePasse");

        return DriverManager.getConnection(url, utilisateur, motDePasse);
    }

}