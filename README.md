# Formulaire d'inscription — Java & MariaDB

Petite application Java en console : création de comptes, connexion avec un nombre d'essais limité, et enregistrement des utilisateurs dans une base **MariaDB**.

Ce guide explique comment installer la base de données et connecter le projet **sous Windows**, avec **MariaDB** et **DBeaver**.

---

## Prérequis

| Outil | Rôle | Où le trouver |
|---|---|---|
| **JDK 25** (ou plus récent) | Faire tourner le Java | [adoptium.net](https://adoptium.net) ou [bell-sw.com](https://bell-sw.com) |
| **Eclipse IDE for Java Developers** | Ouvrir et lancer le projet | [eclipse.org/downloads](https://www.eclipse.org/downloads/) |
| **MariaDB Server** | La base de données | [mariadb.org/download](https://mariadb.org/download/) |
| **DBeaver** | Voir et gérer la base | [dbeaver.io](https://dbeaver.io/download/) |

---

## Étape 1 — Installer MariaDB

Si MariaDB est déjà installé, passe directement à l'étape 2.

1. Télécharge l'installeur **MSI** pour Windows sur [mariadb.org/download](https://mariadb.org/download/).
2. Pendant l'installation :
   - Coche **Modify password for database user 'root'** et choisis un mot de passe administrateur. **Note-le**, il servira à l'étape 2.
   - Laisse **décoché** *Enable access from remote machines for 'root' user* (plus sûr).
   - Coche **Use UTF8 as default server's character set** (pour les accents).
   - Laisse coché **Install as service**, avec le nom de service `MariaDB`.
   - Laisse le port **3306**.
3. Termine l'installation.

**Vérifier que MariaDB tourne :** touche Windows → tape `services.msc` → Entrée. Dans la liste, la ligne **MariaDB** doit être sur **En cours d'exécution**. Si ce n'est pas le cas : clic droit → **Démarrer**.

---

## Étape 2 — Créer la base et l'utilisateur (dans DBeaver, en `root`)

L'application ne se connecte **jamais** avec le compte administrateur `root`. On lui crée un utilisateur dédié, qui n'a accès qu'à sa propre base.

### 2.1 Se connecter en administrateur

1. Ouvre **DBeaver** → **Base de données → Nouvelle connexion** → **MariaDB** → **Suivant**.
2. Remplis :
   - *Hôte* : `localhost`
   - *Port* : `3306`
   - *Base de données* : laisse **vide**
   - *Nom d'utilisateur* : `root`
   - *Mot de passe* : celui choisi pendant l'installation
3. **Tester la connexion…** → accepte le téléchargement du pilote si DBeaver le propose → tu dois voir **Connecté**.
4. **Terminer**.

### 2.2 Exécuter le script de création

Clic droit sur la connexion `localhost` → **Éditeur SQL → Ouvrir un script SQL**, colle ce script en remplaçant `ChoisisUnMotDePasse` par un mot de passe de ton choix (**note-le**, il servira à l'étape 5) :

```sql
CREATE DATABASE formulaire CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER 'formulaire_app'@'localhost' IDENTIFIED BY 'ChoisisUnMotDePasse';

GRANT ALL PRIVILEGES ON formulaire.* TO 'formulaire_app'@'localhost';
```

Exécute **tout le script** avec **Alt + X** (et pas Ctrl + Entrée, qui n'exécute qu'une seule instruction).

Chaque ligne doit se terminer sans erreur.

> **Ce que fait ce script :**
> - `utf8mb4` : stocke correctement les accents (`Liège`, `é`, `è`…).
> - `'formulaire_app'@'localhost'` : un utilisateur qui ne peut se connecter que depuis ton propre ordinateur.
> - `formulaire.*` : il a tous les droits, mais **uniquement** sur la base `formulaire`.

---

## Étape 3 — Créer la table (dans DBeaver, avec l'utilisateur de l'application)

### 3.1 Nouvelle connexion avec `formulaire_app`

**Base de données → Nouvelle connexion** → **MariaDB** :
- *Hôte* : `localhost` · *Port* : `3306`
- *Base de données* : `formulaire`
- *Nom d'utilisateur* : `formulaire_app`
- *Mot de passe* : celui choisi à l'étape 2.2

**Tester la connexion…** → **Connecté** → **Terminer**.

### 3.2 Créer la table `utilisateurs`

Clic droit sur cette nouvelle connexion → **Éditeur SQL → Ouvrir un script SQL**, colle ceci et exécute avec **Ctrl + Entrée** :

```sql
CREATE TABLE utilisateurs (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    identifiant    VARCHAR(50)  NOT NULL UNIQUE,
    mot_de_passe   VARCHAR(255) NOT NULL,
    nom            VARCHAR(100) NOT NULL,
    prenom         VARCHAR(100) NOT NULL,
    date_naissance DATE         NOT NULL,
    adresse        VARCHAR(255),
    email          VARCHAR(255) NOT NULL,
    telephone      VARCHAR(30)
);
```

**Vérifier :** à gauche, déplie **Bases de données → formulaire → Tables** (appuie sur **F5** si besoin) → **utilisateurs → Colonnes**. Tu dois voir les **9 colonnes**.

> Si tu vois l'erreur *Table 'utilisateurs' already exists*, pas de panique : la table a déjà été créée, tout va bien.

---

## Étape 4 — Ouvrir le projet dans Eclipse

1. Récupère le projet (clone Git ou téléchargement ZIP depuis GitHub, puis décompresse-le).
2. Dans Eclipse : **File → Import… → General → Existing Projects into Workspace** → **Next**.
3. *Select root directory* → **Browse…** → choisis le dossier `FormulaireInscription`.
4. Coche le projet → **Finish**.

### Vérifier le pilote MariaDB

Le pilote JDBC (le fichier qui permet à Java de parler à MariaDB) est dans le dossier `lib` du projet. Dans le *Package Explorer*, tu dois voir **Referenced Libraries → mariadb-java-client-3.5.10.jar**.

Si ce n'est pas le cas :
1. Télécharge le pilote : [mariadb-java-client-3.5.10.jar](https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/3.5.10/mariadb-java-client-3.5.10.jar)
2. Mets-le dans le dossier `lib` du projet.
3. Dans Eclipse : clic droit sur le `.jar` → **Build Path → Add to Build Path**.

---

## Étape 5 — Le fichier de configuration `db.properties`

Pour des raisons de sécurité, **aucun mot de passe n'est écrit dans le code**. Les réglages de connexion sont lus dans un fichier `db.properties`, qui **n'est pas publié sur GitHub** (il est listé dans `.gitignore`). Chacun doit donc créer le sien.

1. Dans Eclipse, à la **racine du projet** (au même niveau que `src` et `lib`), fais un clic droit sur `db.properties.exemple` → **Copy**, puis clic droit sur le projet → **Paste**.
2. Eclipse demande un nouveau nom : tape exactement `db.properties` → **OK**.
3. Ouvre `db.properties` et remplace `votre_mot_de_passe` par le mot de passe de `formulaire_app` :

```properties
db.url=jdbc:mariadb://localhost:3306/formulaire
db.utilisateur=formulaire_app
db.motDePasse=votre_mot_de_passe
```

4. **Ctrl + S**.

> **Piège Windows :** si tu crées ce fichier avec le Bloc-notes ou l'Explorateur, Windows peut l'enregistrer en cachette sous le nom `db.properties.txt`, et le programme ne le trouvera pas. Crée-le **depuis Eclipse** comme ci-dessus, ou active l'affichage des extensions dans l'Explorateur (**Affichage → Afficher → Extensions de noms de fichiers**) pour vérifier.

---

## Étape 6 — Tester la connexion

Dans Eclipse : ouvre `src/identification/TestConnexion.java` → clic droit dans le code → **Run As → Java Application**.

La console doit afficher :

```
Connexion réussie à MariaDB !
```

Tu peux ensuite lancer le programme principal de la même façon, depuis `Main.java`.

---

## En cas de problème

| Message affiché | Cause probable | Solution |
|---|---|---|
| `Impossible de lire db.properties` | Fichier absent, mal placé ou nommé `db.properties.txt` | Refais l'étape 5. Le fichier doit être à la racine du projet, pas dans `src`. |
| `Access denied for user 'formulaire_app'@'localhost'` | Mauvais mot de passe ou utilisateur inexistant | Vérifie `db.properties`. Au besoin, refais l'étape 2.2 (ou change le mot de passe avec `ALTER USER`, voir ci-dessous). |
| `Unknown database 'formulaire'` | La base n'a pas été créée | Refais l'étape 2.2. |
| `Could not connect` / `Connection refused` / `Communications link failure` | Le service MariaDB est arrêté, ou le port n'est pas 3306 | `services.msc` → **MariaDB** → **Démarrer**. Vérifie le port dans `db.properties`. |
| `No suitable driver found` | Le pilote n'est pas dans le Build Path | Refais la partie « Vérifier le pilote MariaDB » de l'étape 4. |
| `Table 'utilisateurs' already exists` | La table existe déjà | Rien à faire, tout va bien. |

**Changer le mot de passe de l'utilisateur de l'application** (en `root`, dans DBeaver) :

```sql
ALTER USER 'formulaire_app'@'localhost' IDENTIFIED BY 'NouveauMotDePasse';
```

Puis mets le même mot de passe dans `db.properties`.

---

## Sécurité

- **Ne publie jamais `db.properties`** : il contient ton mot de passe. Il est listé dans `.gitignore` pour que Git l'ignore automatiquement.
- Si un mot de passe finit par erreur sur GitHub, le supprimer du fichier ne suffit pas : il reste dans l'historique. **Change-le immédiatement** avec `ALTER USER`.
- L'application utilise un utilisateur dédié (`formulaire_app`) avec des droits limités à sa propre base, et jamais le compte `root`.
