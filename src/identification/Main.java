package identification;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner clavier = new Scanner(System.in);

        // Le classeur : une liste vide qui contiendra toutes les fiches
        ArrayList<Utilisateur> utilisateurs = new ArrayList<>();

        // Compte de test, pour ne pas tout retaper à chaque essai a virer plus tard
        utilisateurs.add(new Utilisateur("test", "test", "Test", "Compte",
                LocalDate.of(2000, 1, 1), "Rue du Test 1, 4000 Liège", "test@exemple.be"," 0470 34 56 87"));

        // Interrupteur : passe à true quand l'utilisateur choisit "Quitter"
        boolean quitter = false;

        // La grande boucle : le menu revient tant qu'on n'a pas quitté
        while (!quitter) {

            afficherMenu();
            String choix = clavier.nextLine();
            System.out.println();

            // Selon le choix, on part vers la bonne action
            switch (choix) {
                case "1" -> {
                    // inscrire pose les questions et renvoie une fiche neuve
                    Utilisateur nouveau = inscrire(clavier);

                    // rangement de la fiche dans le classeur
                    utilisateurs.add(nouveau);

                    System.out.println();
                    System.out.println("Compte créé pour " + nouveau.getPrenom()
                            + ". Nombre de comptes : " + utilisateurs.size());
                }
                case "2" -> seConnecter(clavier, utilisateurs);
                case "3" -> afficherComptes(utilisateurs);
                case "4" -> {
                    System.out.println("Au revoir !");
                    quitter = true;   // fait sortir de la grande boucle
                }
                default -> System.out.println("Choix invalide, tape 1, 2, 3 ou 4.");
            }

            System.out.println(); // ligne vide pour aérer avant le menu suivant
        }

        clavier.close();
    }

    /**
     * Demande toutes les informations+création new utilisateur
     *
     * @param clavier le lecteur de clavier du programme
     * @return la fiche utilisateur remplie
     */
    public static Utilisateur inscrire(Scanner clavier) {

        // Le format de date attendu : jour/mois/année (MM en majuscules = mois)
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.println("=== Création de compte ===");

        System.out.print("Identifiant : ");
        String identifiant = clavier.nextLine();

        // --- Mot de passe, demandé deux fois ---
        //  AVANT la boucle, pour pouvoir s'en servir APRES
        String motDePasse = "";
        boolean identiques = false;

        // Tant que les deux mots de passe ne sont pas identiques, on redemande
        while (!identiques) {

            System.out.print("Mot de passe : ");
            motDePasse = clavier.nextLine();   // pas de "String" : la variable existe déjà

            System.out.print("Confirme le mot de passe : ");
            String confirmation = clavier.nextLine();

            if (motDePasse.equals(confirmation)) {
                identiques = true;   // fait sortir de la boucle
            } else {
                System.out.println("Les mots de passe ne correspondent pas, recommence.");
            }
        }

        System.out.print("Nom : ");
        String nom = clavier.nextLine();

        System.out.print("Prénom : ");
        String prenom = clavier.nextLine();

        System.out.print("Date de naissance (jj/mm/aaaa) : ");
        String dateTexte = clavier.nextLine();
        // On transforme le texte tapé en date
        LocalDate dateNaissance = LocalDate.parse(dateTexte, formatDate);

        System.out.print("Adresse : ");
        String adresse = clavier.nextLine();

        // --- E-mail, redemandé tant qu'il n'est pas valide ---
        // AVANT la boucle, pour pouvoir s'en servir APRÈS
        String email = "";
        boolean emailOk = false;

        // Tant que l'adresse n'est pas valide, on redemande
        while (!emailOk) {

            System.out.print("E-mail : ");
            email = clavier.nextLine();   // pas de "String" : la variable existe déjà

            if (emailValide(email)) {
                emailOk = true;   // fait sortir de la boucle
            } else {
                System.out.println("Adresse e-mail invalide (exemple : nom@domaine.be), recommence.");
            }
            
        
        }
        System.out.print("Téléphone : ");
        String telephone = clavier.nextLine();
        // On fabrique la fiche avec tout ce qu'on a récupéré, et on la renvoie
        return new Utilisateur(identifiant, motDePasse, nom, prenom, dateNaissance, adresse, email, telephone);
    }

    /**
     * Cherche dans la liste la fiche qui correspond à l'identifiant et au mot de passe.
     *
     * @param utilisateurs la liste de toutes les fiches
     * @param identifiant  l'identifiant tapé
     * @param motDePasse   le mot de passe tapé
     * @return la fiche trouvée, ou null si aucune ne correspond
     */
    public static Utilisateur trouverUtilisateur(ArrayList<Utilisateur> utilisateurs,
                                                 String identifiant, String motDePasse) {

        // On feuillette le classeur : pour chaque fiche u de la liste...
        for (Utilisateur u : utilisateurs) {

            // ...vérification de correspondance 
            if (u.verifierConnexion(identifiant, motDePasse)) {
                return u; // trouvée : on la renvoie et on arrête de chercher
            }
        }

        //
        return null;
    }

    /**
     * Affiche le profil complet d'un utilisateur connecté.
     *
     * @param utilisateur la fiche à afficher
     */
    public static void afficherProfil(Utilisateur utilisateur) {
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.println();
        System.out.println("Connexion réussie. Bienvenue " + utilisateur.getPrenom() + " !");
        System.out.println("--- Ton profil ---");
        System.out.println("Nom : " + utilisateur.getPrenom() + " " + utilisateur.getNom());
        System.out.println("Né(e) le : " + utilisateur.getDateNaissance().format(formatDate)
                + " (" + utilisateur.calculerAge() + " ans)");
        System.out.println("Adresse : " + utilisateur.getAdresse());
        System.out.println("E-mail : " + utilisateur.getEmail());
        System.out.println("Téléphone : " + utilisateur.getTelephone());
    }

    /**
     
     * @param utilisateurs la liste de toutes les fiches
     */
    public static void afficherComptes(ArrayList<Utilisateur> utilisateurs) {

        // Le titre ne s'affiche qu'une fois : AVANT la boucle
        System.out.println("Comptes enregistrés :");

        // Pour chaque fiche u du classeur...
        for (Utilisateur u : utilisateurs) {
            // ... on affiche une ligne avec ses informations
            System.out.println("- " + u.getIdentifiant() + " (" + u.getPrenom() + " " + u.getNom() + ")");
        }

        System.out.println(); // une ligne vide APRÈS la boucle, pour aérer
    }
    
    /**
     * Affiche le menu principal.
     */
    public static void afficherMenu() {
        System.out.println("===== MENU =====");
        System.out.println("1. Créer un compte");
        System.out.println("2. Se connecter");
        System.out.println("3. Voir les comptes");
        System.out.println("4. Quitter");
        System.out.print("Ton choix : ");
    }

    /**
     * Gère la connexion : demande l'identifiant et le mot de passe,
     * avec un nombre d'essais limité.
     *
     * @param clavier      le lecteur de clavier du programme
     * @param utilisateurs la liste de toutes les fiches
     */
    public static void seConnecter(Scanner clavier, ArrayList<Utilisateur> utilisateurs) {

        int essais = 0;            // compteur d'erreurs : commence à 0 et augmente
        int essaisMax = 5;         // limite : ne change jamais
        boolean connecte = false;  // interrupteur : passe à true quand la connexion réussit

        afficherTitre();

        // Tant qu'il reste des essais ET que l'utilisateur n'est pas connecté, on redemande
        while (essais < essaisMax && !connecte) {

            System.out.print("Identifiant : ");
            String identifiantSaisi = clavier.nextLine();

            System.out.print("Mot de passe : ");
            String mdpSaisi = clavier.nextLine();

            // On cherche la fiche correspondante dans le classeur
            Utilisateur trouve = trouverUtilisateur(utilisateurs, identifiantSaisi, mdpSaisi);

            if (trouve != null) {
                connecte = true;
                afficherProfil(trouve);
            } else {
                essais++;         // une erreur de plus
                System.out.println("Incorrect. Essais restants : " + (essaisMax - essais));
            }
        }

        // Sorti de la boucle sans être connecté = essais épuisés
        if (!connecte) {
            System.out.println("Trop d'essais. Accès bloqué.");
        }
    }
    /**
     * Vérifie qu'une adresse e-mail a une forme correcte (nom@domaine.be).
     *
     * @param email l'adresse à vérifier
     * @return true si la forme est correcte, false sinon
     */
    public static boolean emailValide(String email) {

        // On repère les positions importantes
        int arobase = email.indexOf("@");         // position du premier @
        int dernierArobase = email.lastIndexOf("@"); // position du dernier @
        int point = email.lastIndexOf(".");       // position du dernier point

        // Règle 1 : pas d'espace
        boolean sansEspace = !email.contains(" ");

        // Règle 2 : un seul @ (premier = dernier), et pas tout au début (position > 0)
        boolean unSeulArobase = arobase > 0 && arobase == dernierArobase;

        // Règle 3 : un point après le @, avec au moins un caractère entre les deux
        boolean pointApresArobase = point > arobase + 1;

        // Règle 4 : le point n'est pas le dernier caractère
        boolean pasFiniParPoint = point < email.length() - 1;

        // L'adresse est valide seulement si TOUTES les règles sont respectées
        return sansEspace && unSeulArobase && pointApresArobase && pasFiniParPoint;
    }
    
    /**
     * Affiche le titre de l'écran d'identification.
     */
    public static void afficherTitre() {
        System.out.println("=== Écran d'identification ===");
    }

}