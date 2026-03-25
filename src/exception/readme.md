# Dossier Exception

## But général
Le but de ce dossier est de centraliser l'ensemble des exceptions de l'application. Chaque exception provenant d'un module externe (comme SQL-connector ou Swing, par exemple) doit être gérée localement pour éviter les effets en cascade lors de potentielles mises à jour. On devra donc attraper les exceptions offertes par les différents modules et packages utilisés (Swing, SQL, etc.) afin d'avoir une exception personnalisée qui peut "voyager" au sein de l'application.

## Explication détaillée
En Java, les exceptions sont des mécanismes pour gérer les erreurs et les situations inattendues. Le dossier `exception` regroupe toutes les classes d'exceptions personnalisées, permettant de :

- **Abstraire les erreurs externes** : Au lieu de propager des exceptions techniques (comme `SQLException` ou `IOException`), on crée des exceptions métier qui masquent les détails d'implémentation.
- **Améliorer la maintenabilité** : En cas de changement de bibliothèque (par exemple, remplacer JDBC par JPA), seules les exceptions dans ce dossier changent, sans impacter le reste du code.
- **Faciliter le débogage** : Les exceptions personnalisées peuvent inclure des messages plus descriptifs et des contextes spécifiques à l'application.
- **Renforcer la séparation des couches** : Chaque couche (présentation, métier, données) peut définir ses propres exceptions sans dépendre des autres.

## Avantages d'un dossier dédié
- **Cohérence** : Toutes les exceptions sont au même endroit, facilitant la recherche et la gestion.
- **Réutilisabilité** : Les exceptions peuvent être réutilisées dans différents modules de l'application.
- **Sécurité** : Évite de révéler des détails internes (comme des traces de pile sensibles) aux utilisateurs finaux.
- **Évolutivité** : Permet d'ajouter facilement de nouvelles exceptions sans modifier le code existant.

## Exemples d'utilisation
- **Exception métier** : `InvalidUserException` pour une tentative de connexion avec des identifiants invalides.
- **Exception technique** : `DatabaseConnectionException` qui encapsule une `SQLException` pour les problèmes de base de données.
- **Exception de validation** : `DataValidationException` pour des données entrées par l'utilisateur qui ne respectent pas les règles.

## Bonnes pratiques
- Hériter de `Exception` pour les exceptions vérifiées ou `RuntimeException` pour les non-vérifiées.
- Inclure des constructeurs avec des messages détaillés et des causes (via `super(message, cause)`).
- Utiliser des noms descriptifs et suivre les conventions de nommage Java (suffixe `Exception`).
- Logger les exceptions avant de les relancer pour faciliter le débogage. 
