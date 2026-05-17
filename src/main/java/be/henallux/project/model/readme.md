# Dossier Model (Modèles)

Ce dossier contient les classes de modèles utilisées dans l'application. Les modèles représentent les données et servent exclusivement à passer des informations d'une couche de l'application à l'autre dans une architecture hybride MVC et 3-tier.

## But d'un modèle dans une architecture MVC et 3-tier combinée
Dans le pattern MVC (Modèle-Vue-Contrôleur), les modèles représentent les données de l'application et encapsulent la logique métier liée à ces données. Ils sont responsables de la gestion de l'état des entités métier et de la validation des données.

Dans une architecture 3-tier (présentation, métier, données), les modèles agissent comme des objets de transfert de données (DTO - Data Transfer Objects) qui permettent une communication propre et découplée entre les couches :
- **Couche présentation** (vues et contrôleurs) : Utilise les modèles pour afficher et collecter des données.
- **Couche métier** (business) : Traite les modèles pour appliquer la logique métier.
- **Couche données** (data access) : Persiste et récupère les modèles depuis la base de données.

## Utilisation exclusive pour le transfert de données
Les modèles sont utilisés exclusivement pour passer des données entre les couches de l'application :
- Ils ne contiennent pas de logique métier complexe (réservée à la couche business).
- Ils ne gèrent pas l'accès aux données (réservé à la couche data).
- Ils ne définissent pas l'interface utilisateur (réservé à la couche présentation).
- Ils servent de contrats entre les couches, assurant la cohérence des données transférées.

## Objectifs principaux
- **Représenter les entités métier** : Classes Java simples (POJO) avec getters/setters.
- **Faciliter le transfert de données** : Structures légères et sérialisables.
- **Assurer la cohérence** : Validation de base et contraintes sur les données.
- **Promouvoir le découplage** : Chaque couche manipule les mêmes objets sans dépendances directes.
- **Améliorer la maintenabilité** : Changements dans une couche n'affectent pas les autres via les modèles.

## Architecture
- Classes de modèles (Entity, DTO) avec annotations JPA si nécessaire.
- Héritage possible pour spécialiser les modèles (ex. : UserDTO, ProductDTO).
- Utilisation dans les contrôleurs pour recevoir/envoyer des données.
- Passage aux services métier pour traitement.
- Mapping vers les DAO pour la persistance.

## Responsabilités
- Définition des attributs des entités (champs, types).
- Méthodes d'accès (getters/setters) et utilitaires (toString, equals, hashCode).
- Annotations pour la validation (@NotNull, @Size) et la sérialisation.
- Éventuellement, des constructeurs pour faciliter la création d'instances.
