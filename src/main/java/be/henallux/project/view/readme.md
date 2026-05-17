# Dossier View (Vues)

Ce dossier contient les classes de vues utilisées dans l'application. Les vues sont responsables de l'interface utilisateur et de la présentation des données à l'utilisateur.

## But d'une vue dans une architecture MVC et 3-tier
Dans le pattern MVC (Modèle-Vue-Contrôleur), les vues sont chargées de présenter les données aux utilisateurs et de recueillir leurs interactions. Elles affichent l'état des modèles et transmettent les actions utilisateur aux contrôleurs.

Dans une architecture 3-tier (présentation, métier, données), les vues font partie de la couche présentation. Elles sont responsables de l'interface graphique ou textuelle, sans contenir de logique métier ou d'accès aux données. Elles communiquent exclusivement avec les contrôleurs pour recevoir des données (via les modèles) et envoyer des événements utilisateur.

## Utilisation dans ce projet
Dans le cadre de ce projet, la vue est développée en Java en utilisant les composants Swing. Swing fournit une bibliothèque riche de composants graphiques pour créer des interfaces utilisateur desktop :
- **Fenêtres principales** : `JFrame` pour les fenêtres de l'application.
- **Composants interactifs** : `JButton`, `JTextField`, `JTable`, `JComboBox`, etc.
- **Layouts** : Gestionnaires de mise en page comme `BorderLayout`, `FlowLayout`, `GridLayout`.
- **Événements** : Gestion des actions utilisateur via des `ActionListener` et autres listeners.

Les vues Swing sont construites de manière modulaire, avec des panels (`JPanel`) pour organiser les composants, et utilisent le pattern Observer pour réagir aux changements de données.

## Objectifs principaux
- **Présenter les données** : Afficher les informations des modèles de manière claire et intuitive.
- **Recueillir les interactions** : Capturer les saisies et actions de l'utilisateur.
- **Assurer l'expérience utilisateur** : Fournir une interface ergonomique et responsive.
- **Maintenir la séparation des préoccupations** : Ne pas mélanger logique métier ou persistance.
- **Faciliter la maintenance** : Permettre des changements d'UI sans impacter les autres couches.

## Architecture
- Classes étendant `JFrame` ou `JPanel` pour les vues principales.
- Utilisation de modèles (DTO) pour recevoir et afficher des données.
- Communication avec les contrôleurs via des méthodes publiques ou des événements.
- Pas de logique métier : les calculs et validations sont délégués aux contrôleurs et à la couche business.
- Possibilité d'utiliser des design patterns comme MVP (Model-View-Presenter) pour une meilleure séparation.

## Responsabilités
- Création et configuration des composants Swing.
- Gestion de l'affichage des données (remplissage de champs, listes, tableaux).
- Capture des événements utilisateur (clics, saisies) et notification des contrôleurs.
- Mise à jour de l'interface en réponse aux changements de données.
- Gestion de l'état visuel (activation/désactivation de composants, messages d'erreur).
