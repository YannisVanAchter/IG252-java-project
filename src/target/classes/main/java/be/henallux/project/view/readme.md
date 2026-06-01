# Dossier View (Vues)

Ce dossier contient les classes de vues utilisées dans l'application. Les vues sont responsables de l'interface
utilisateur et de la présentation des données à l'utilisateur.

## But d'une vue dans une architecture MVC et 3-tier

Dans une architecture 3-tier (présentation, métier, données), les vues font partie de la couche présentation. Elles sont
responsables de l'interface graphique ou textuelle, sans contenir de logiques métiers ou d'accès aux données. Elles
communiquent exclusivement avec les contrôleurs pour recevoir des données (via les modèles) et envoyer des événements
utilisateur.

## Utilisation dans ce projet

Dans le cadre de ce projet, la vue est développée en Java en utilisant les composants Swing. Swing fournit une
bibliothèque riche de composants graphiques pour créer des interfaces utilisateur desktop :

- **Fenêtres principales** : `JFrame` pour les fenêtres de l'application.
- **Composants interactifs** : `JButton`, `JTextField`, `JTable`, `JComboBox`, etc.
- **Layouts** : Gestionnaires de mise en page comme `BorderLayout`, `FlowLayout`, `GridLayout`.
- **Événements** : Gestion des actions utilisateur via des `ActionListener` et autres listeners.

Les vues Swing sont construites de manière modulaire, avec des panels (`JPanel`) pour organiser les composants, et
utilisent le pattern Observer pour réagir aux changements de données.

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

# Architecture

## Layouts utilisés

| Layout               | Description                                                       | Utilisation typique dans ce projet                  |
|----------------------|-------------------------------------------------------------------|-----------------------------------------------------|
| `BorderLayout`       | Divise le conteneur en 5 zones : North, South, East, West, Center | Layout racine de la plupart des vues principales    |
| `BoxLayout (Y_AXIS)` | Empile les composants verticalement                               | Colonnes de formulaires, listes de panneaux         |
| `BoxLayout (X_AXIS)` | Aligne les composants horizontalement                             | Rangées de boutons, champs côte à côte              |
| `FlowLayout`         | Dispose les composants en ligne, de gauche à droite               | Barres de boutons, zones de filtres                 |
| `GridLayout`         | Grille fixe lignes × colonnes                                     | Sections de formulaires à deux colonnes             |
| `CardLayout`         | Affiche un seul panneau à la fois                                 | Navigation entre toutes les pages dans `MainWindow` |

 
---

## Types de fenêtres et composants de haut niveau

| Type           | Classe(s)                                                 | Description                                                                                    |
|----------------|-----------------------------------------------------------|------------------------------------------------------------------------------------------------|
| `JFrame`       | `MainWindow`                                              | Fenêtre principale de l'application, avec barre de menu et `CardLayout`                        |
| `JDialog`      | `ReceiptClientInfoDialog`                                 | Boîte de dialogue modale associé au `Frame` parent. _ex: pour associer un client à une caisse_ |
| `JWindow`      | `ToastWindow`                                             | Fenêtre flottante sans décoration pour les notifications toast                                 |
| `JPanel`       | Toutes les autres vues                                    | Conteneurs de page affichés via le `CardLayout` de `MainWindow`                                |
| `JScrollPane`  | `HelpPanel`, `DocumentTable`, `ClientSupplierTable`, etc. | Enveloppe les composants pour permettre le défilement verticales.                              |
| `JSplitPane`   | `StockAlertView`, `ReceiptCreateView`                     | Divise l'espace en deux panneaux redimensionnables                                             |
| `JLayeredPane` | `MainWindow` (via `NotifBellButton`)                      | Permet de superposer le `NotificationDropdown` par-dessus le contenu principal                 |

---

## Pages de l'application par groupe

### Convention de nommage des classes

Les classes de vues suivent une convention de nommage préfixée par leur domaine métier, et suffixée par leur rôle :

| Suffixe      | Rôle                                                                             | CRUD     | Exemple                    |
|--------------|----------------------------------------------------------------------------------|----------|----------------------------|
| `Table`      | Panneau principal avec tableau de recherche et filtres                           | `R`, `D` | `ClientSupplierTable`      |
| `Form`       | Formulaire de création ou de modification                                        | `C`, `U` | `ClientSupplierForm`       |
| `View`       | Fiche de détail en lecture seule                                                 | `R`      | `ProductSearchView`        |
| `TableModel` | Modèle de données associé à un `JTable` souvent utilisé depuis la classe `Table` | Aucun    | `ClientSupplierTableModel` |

---
`MainWindow` utilise le `CardLayout` pour afficher les pages les unes sur les autres.\
Les pages sont enregistrées dans `MainWindow` via `addPage(panel, "KEY")` et activées via `setPage("KEY")`.

### File

| Classe      | Key CardLayout   | Description                                                                   |
|-------------|------------------|-------------------------------------------------------------------------------|
| `HomePanel` | `"MAIN"`         | Tableau de bord, page d'accueil avec cartes de navigation vers chaque section |
| `HelpPanel` | *(dans JDialog)* | Page d'aide : raccourcis clavier, guide des sections, informations du projet  |

### Management — Documents

| Classe          | Key CardLayout    | Description                                                                                   |
|-----------------|-------------------|-----------------------------------------------------------------------------------------------|
| `DocumentTable` | `"DOCUMENT"`      | Tableau de recherche et filtrage des documents (ID, type, date) avec boutons Éditer/Supprimer |
| `DocumentForm`  | `"DOCUMENT_FORM"` | Formulaire de création et de modification d'un document.                                      |

**TableModel associé :**

| TableModel           | Colonnes                                       | Actions                          |
|----------------------|------------------------------------------------|----------------------------------|
| `DocumentTableModel` | ID, Workflow, Creation date, Send/Receipt date | Edit (`col 4`), Delete (`col 5`) |

### Management — Clients & Fournisseurs

| Classe                | Key CardLayout           | Description                                                                                                       |
|-----------------------|--------------------------|-------------------------------------------------------------------------------------------------------------------|
| `ClientSupplierTable` | `"CLIENT_SUPPLIER"`      | Tableau de recherche des clients/fournisseurs filtrables par ID, nom, prénom et type                              |
| `ClientSupplierForm`  | `"CLIENT_SUPPLIER_FORM"` | Formulaire de création/modification d'un client ou fournisseur. Ouvrable aussi en `JDialog` depuis `DocumentForm` |

**TableModel associé :**

| TableModel                 | Colonnes                                         | Actions                          |
|----------------------------|--------------------------------------------------|----------------------------------|
| `ClientSupplierTableModel` | id, Name & First Name, Email, TVA, Type, Loyalty | Edit (`col 6`), Delete (`col 7`) |

### Search — Clients

| Classe              | Key CardLayout  | Description                                                            |
|---------------------|-----------------|------------------------------------------------------------------------|
| `ClientSearchTable` | `"CLIENT"`      | Recherche de clients par nom, email, numéro de carte de fidélité       |
| `ClientSearchView`  | `"CLIENT_VIEW"` | Fiche détaillée d'un client (informations, adresse, carte de fidélité) |

**TableModel associé :**

| TableModel               | Colonnes                                                                       | Actions       |
|--------------------------|--------------------------------------------------------------------------------|---------------|
| `ClientSearchTableModel` | Name & First Name, Email, Phone, Client since, Points, Card, City, Postal code | See (`col 8`) |

### Search — Produits

| Classe               | Key CardLayout   | Description                                                         |
|----------------------|------------------|---------------------------------------------------------------------|
| `ProductSearchTable` | `"PRODUCT"`      | Recherche de produits par nom, catégorie et statut de promotion     |
| `ProductSearchView`  | `"PRODUCT_VIEW"` | Fiche détaillée d'un produit (stock, prix, TVA, promotion en cours) |

**TableModel associé :**

| TableModel                | Colonnes                                                                      | Actions       |
|---------------------------|-------------------------------------------------------------------------------|---------------|
| `ProductSearchTableModel` | Name, Category, Fidelity pts, Price, VAT, Stock, Promo, Discount, Promo start | See (`col 9`) |

### Search — Recettes (Recipe)

| Classe              | Key CardLayout  | Description                                                        |
|---------------------|-----------------|--------------------------------------------------------------------|
| `RecipeSearchTable` | `"RECIPE"`      | Recherche de recettes par nom et filtrage par ingrédients          |
| `RecipeSearchView`  | `"RECIPE_VIEW"` | Fiche détaillée d'une recette (composition, étapes de préparation) |

**TableModel associé :**

| TableModel               | Colonnes                   | Actions              |
|--------------------------|----------------------------|----------------------|
| `RecipeSearchTableModel` | Name, Document ID, Product | See Recipe (`col 3`) |

### Business — Caisse (Receipt)

| Classe                    | Key CardLayout           | Description                                                                                          |
|---------------------------|--------------------------|------------------------------------------------------------------------------------------------------|
| `ReceiptCreateView`       | `"RECEIPT"`              | Interface de caisse en double panneau (`JSplitPane`) : navigation produits à gauche, panier à droite |
| `ReceiptClientInfoDialog` | *(JDialog modal)*        | Étape intermédiaire de caisse : sélection/création d'un client, scan de carte de fidélité            |
| `ReceiptPayment`          | *(chargé dynamiquement)* | Récapitulatif final : produits, remises, TVA, total, sélection du mode de paiement                   |

**TableModels associés :**

| TableModel                 | Utilisé dans                                 | Colonnes              | Actions       |
|----------------------------|----------------------------------------------|-----------------------|---------------|
| `ReceiptProductTableModel` | `ReceiptCreateView` (panneau gauche)         | Name, Price           | Add (`col 2`) |
| `ReceiptTableModel`        | `ReceiptCreateView` (panneau droit — panier) | Name, Quantity, Total | —             |

### Business — Stock

_Accessible également depuis un click sur la notification provenant du `thread`_

| Classe               | Key CardLayout     | Description                                                                                       |
|----------------------|--------------------|---------------------------------------------------------------------------------------------------|
| `StockAlertView`     | `"STOCK"`          | Vue en double panneau : liste des fournisseurs à gauche, produits à réapprovisionner à droite     |
| `StockOrderCreation` | `"ORDER_CREATION"` | Confirmation d'une commande fournisseur : détails du fournisseur, quantités éditables via spinner |

**TableModels associés :**

| TableModel             | Utilisé dans         | Colonnes                                                                           | Actions            |
|------------------------|----------------------|------------------------------------------------------------------------------------|--------------------|
| `StockAlertTableModel` | `StockAlertView`     | (chekbox), Name, Current stock level, Minimum threshold, Quantity to order, Status | CheckBox (`col 0`) |
| `StockOrderTableModel` | `StockOrderCreation` | Name, Suggested Quantity, Quantity Ordered                                         | Spinner (`col 2`)  |

 
---

### Composants utilitaires

| Classe                 | Type                                 | Description                                                                                     |
|------------------------|--------------------------------------|-------------------------------------------------------------------------------------------------|
| `ViewUtils`            | Classe utilitaire (non instanciable) | Helpers Swing réutilisables : création de panneaux, champs de date, formatage, etc.             |
| `ComboBoxItem<T>`      | Wrapper générique                    | Associe un objet métier à un label pour les `JComboBox`                                         |
| `ButtonRenderer`       | `TableCellRenderer`                  | Affiche un `JButton` dans une cellule de `JTable`                                               |
| `SpinnerEditor`        | `TableCellEditor`                    | Éditeur de cellule avec `JSpinner` pour tableau, utilisé dans `StockOrderCreation`              |
| `RowColorRenderer`     | `DefaultTableCellRenderer`           | Colore les lignes d'un tableau. _ex: selon le niveau de stock (critique = rouge, bas = orange)_ |
| `NotifBellButton`      | `JButton`                            | Bouton cloche dans la `JMenuBar` avec badge de compteur de notifications                        |
| `NotificationDropdown` | `JPanel`                             | Panneau déroulant de notifications ancré avec `JLayeredPane` de `MainWindow`                    |
| `ToastWindow`          | `JWindow`                            | Notification flottante auto-fermante (4 s) positionnée en bas à droite, empilable               |

---

## Architecture générale

```
MainWindow (JFrame)
├── MenuWindow (JMenuBar)
│   └── NotifBellButton → NotificationDropdown (JLayeredPane)
├── HelpPanel (JDialog)
└── container (JPanel avec CardLayout)
    ├── HomePanel
    ├── DocumentTable
    │   └── DocumentForm
    ├── ClientSupplierTable
    │   └── ClientSupplierForm
    ├── ClientSearchTable
    │   └── ClientSearchView
    ├── ProductSearchTable
    │   └── ProductSearchView
    ├── RecipeSearchTable
    │   └── RecipeSearchView
    ├── ReceiptCreateView
    │   └── ReceiptClientInfoDialog (JDialog)
    │       └── ReceiptPayment
    └── StockAlertView
        └── StockOrderCreation
 
Fenêtres indépendantes :
└── ToastWindow (JWindow)
```