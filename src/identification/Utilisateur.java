package identification;

// Outil de Java pour manipuler les dates
import java.time.LocalDate;
// Outil pour calculer un écart entre deux dates
import java.time.Period;

/**
 * Représente un utilisateur avec ses informations de connexion et de profil.
 * C'est le modèle de fiche : chaque utilisateur créé aura ses propres valeurs.
 */
public class Utilisateur {

    // --- Informations de connexion ---
    // private = case verrouillée, seule cette classe peut la lire ou la modifier
    private String identifiant;
    private String motDePasse;

    // --- Informations personnelles ---
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String adresse;
    private String email;
    private String telephone; // NOUVEAU (1) : la case

    /**
     * Constructeur : remplit toute la fiche au moment de sa création.
     * Attention : les valeurs doivent être données dans CET ordre.
     */
    public Utilisateur(String identifiant, String motDePasse, String nom, String prenom,
                       LocalDate dateNaissance, String adresse, String email,
                       String telephone) { // NOUVEAU (2) : le constructeur reçoit la valeur
        // this.xxx = la case de la fiche ; xxx = la valeur reçue
        this.identifiant = identifiant;
        this.motDePasse = motDePasse;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.email = email;
        this.telephone = telephone; // NOUVEAU (3) : le constructeur range la valeur
    }

    /**
     * Calcule l'âge de l'utilisateur à partir de sa date de naissance.
     *
     * @return l'âge en années
     */
    public int calculerAge() {
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    /**
     * Vérifie si l'identifiant et le mot de passe saisis correspondent à cette fiche.
     * Le mot de passe ne sort jamais de la fiche : c'est elle qui compare.
     *
     * @param identifiantSaisi l'identifiant tapé
     * @param motDePasseSaisi  le mot de passe tapé
     * @return true si les deux correspondent, false sinon
     */
    public boolean verifierConnexion(String identifiantSaisi, String motDePasseSaisi) {
        // L'identifiant ignore les majuscules, le mot de passe non (sécurité)
        return identifiant.equalsIgnoreCase(identifiantSaisi) && motDePasse.equals(motDePasseSaisi);
    }

    // --- Guichets de lecture (getters) ---
    // Volontairement, il n'y a PAS de guichet pour le mot de passe

    public String getIdentifiant() {
        return identifiant;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getEmail() {
        return email;
    }

    // NOUVEAU (4) : le guichet du téléphone
    public String getTelephone() {
        return telephone;
    }

}