# Stratégie des triggers

## Système GRH

## Système gestion des stocks

## Système gestion des procéssus achat et vente

### Document

Il existe plusieurs type de document. Certains types de document demande certains attribut qui sont opitonnel par défaut et obligatoire dans d'autre document.

#### Bon de commande

Le trigger doit vérifier la présence des attributs: ``dateEnvoiPréue``, ``dateEnvoiEffective``, ``dateRéceptionPrévue``, ``dateRéceptionEffective``, ``délaiPaiement``.

#### Bon de livraison

Le trigger doit vérifier la présence de l'``addresse de livraison`` et du ``commentaire``.

#### Ordre de préparation

Le trigger doit vérifier la présence d'un ``commentaire``.

### Workflow

On doit vérifier que l'attribut us référence à un ClientSupplier qui est bien nous, et que l'autre est une autre entité de la table.

### WorkflowType

Le trigger doit vérifier qu'un seul des trois attribut `estAchat`, `estFournisseur` ou `estOrdrePreparation` ai sa valeur à `true`.

### Lot

Le trigger doit vérifier que: Si ``produit est commestible``, ``datePéremption`` est définie dans le futur.

### ClientFournisseur

Le trigger doit vérifier que l'un des trois attribut est mis à vrai:

- ``estClient``
- ``estFournisseur``
- ``estNous``

Si `estNous` est à vrai, alors les deux autres doivent être faux. De plus, le trigger doit vérifier que `estNous` à vrai n'existe qu'une fois dans la base de donnée.

#### estClient (vrai)

Le trigger doit alors vérifier si le ``prénom`` et la ``dateDevenuClient`` doivent être complété

#### estFournisseur (vrai)

Le trigger doit alors vérifier si le ``numéro de TVA`` est complété.
