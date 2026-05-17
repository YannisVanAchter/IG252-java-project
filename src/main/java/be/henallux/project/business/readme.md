# Couche Business (Métier)

Cette couche est responsable de l'implémentation de la logique métier de l'application.
Elle contient tous les traitements, calculs et règles de gestion spécifiques au domaine.

Objectifs principaux :
- Encapsuler la logique métier indépendamment de la présentation et de la persistance
- Valider les données selon les règles de gestion applicables
- Orchestrer les opérations complexes impliquant plusieurs entités
- Assurer la cohérence et l'intégrité des données métier
- Faciliter la réutilisabilité du code métier entre différentes interfaces (web, mobile, API)

Architecture :
- Positionnée entre la couche Présentation (MVC - Contrôleurs/Vues) et la couche Données (DAO)
- Fait partie d'une architecture hybride MVC + 3-tier
- Communique avec la couche Présentation via les contrôleurs
- Communique avec la couche Données via les DAOs (Data Access Objects)

Responsabilités :
- Services/Managers contenant les cas d'usage métier
- Validation métier avancée
- Transformation et calculs sur les données
- Gestion des transactions métier
