# Couche Data (Accès aux données)

Cette couche est responsable de l'accès et de la gestion des données persistantes de l'application.
Elle fournit une abstraction pour interagir avec les sources de données (bases de données, fichiers, APIs externes, etc.).

## Objectifs principaux :
- Abstraire les détails techniques de la persistance des données
- Fournir une interface uniforme pour l'accès aux données
- Gérer les connexions et les transactions de base de données
- Optimiser les performances des requêtes de données
- Assurer la sécurité et l'intégrité des données stockées
- Faciliter les changements de système de stockage sans impacter les couches supérieures

## Architecture :
- Positionnée comme la couche la plus basse dans l'architecture 3-tier
- Fait partie d'une architecture hybride MVC + 3-tier
- Communique uniquement avec la couche business (métier)
- Utilise des patterns comme DAO (Data Access Object) ou Repository
- Peut inclure des ORM (Object-Relational Mapping) pour simplifier l'accès

## Responsabilités :
- Data Access Objects (DAO) pour chaque entité métier
- Gestion des connexions à la base de données
- Exécution des requêtes SQL/CRUD (Create, Read, Update, Delete)
- Mapping entre les objets métier et les structures de données
- Gestion des transactions de base de données
- Cache de données et optimisation des performances
