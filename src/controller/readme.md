# Couche contrôleur

Cette couche est responsable de la gestion des interactions utilisateur et de l'orchestration des requêtes dans l'application.
Elle agit comme un intermédiaire entre la couche présentation (vues) et la couche métier (business logic).

## Objectifs principaux :
- Recevoir et traiter les requêtes utilisateur (HTTP, événements UI, etc.)
- Valider les entrées utilisateur de base
- Coordonner les appels vers la couche business pour exécuter la logique métier
- Sélectionner et préparer les données pour les vues
- Gérer les erreurs et les réponses appropriées
- Assurer la séparation des préoccupations entre présentation et logique

## Architecture :
- Positionnée dans la couche présentation de l'architecture 3-tier
- Fait partie du pattern MVC (Modèle-Vue-Contrôleur)
- Reçoit les requêtes de l'interface utilisateur
- Délègue la logique métier à la couche business
- Communique avec la couche données via la couche business

## Responsabilités :
- Contrôleurs (Controllers) gérant les routes et les actions
- Gestion des sessions et de l'authentification utilisateur
- Validation des paramètres d'entrée
- Coordination des workflows utilisateur
- Préparation des modèles de données pour les vues
- Gestion des redirections et des réponses HTTP
