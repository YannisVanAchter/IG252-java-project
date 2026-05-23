package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.*;
import java.util.List;
public class ClientManager extends ClientSupplierManager {

    private final FidelityCardDA fidelityCardDA;
    private final CheckoutDA checkoutDA;
    private final DocumentDA documentDA;
    
    public ClientManager(ClientSupplierDA clientSupplierDA, AddressManager addressManager, FidelityCardDA fidelityCardDA, CheckoutDA checkoutDA, DocumentDA documentDA) {
        super(clientSupplierDA, addressManager);
        this.fidelityCardDA = fidelityCardDA;
        this.checkoutDA = checkoutDA;
        this.documentDA = documentDA;
    }

    public List<ClientSupplier> getAllClients() throws BusinessException {
        return getAllClientSuppliers();
    }

    public void createFidelityCard(int clientId) throws BusinessException {
        // Validation
        if (clientId <= 0) {
            throw new BusinessException("L'identifiant client est invalide.");
        }
        try {
            // Règle métier — un client ne peut avoir qu'une seule carte
            if (fidelityCardDA.hasFidelityCard(clientId)) {
                throw new BusinessException("Ce client possède déjà une carte de fidélité.");
            }
            fidelityCardDA.createFidelityCard(clientId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la création de la carte de fidélité.", e);
        }
    }

    public boolean validateFidelityCardOwnership(int clientId, int cardId) throws BusinessException {
        // Validation
        if (clientId <= 0 || cardId <= 0) {
            throw new BusinessException("Les identifiants fournis sont invalides.");
        }
        try {
            return fidelityCardDA.validateFidelityCardOwnership(clientId, cardId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la validation de la carte de fidélité.", e);
        }
    }

    public void addCheckout(List<Product> products) throws BusinessException {
        // Validation
        if (products == null || products.isEmpty()) {
            throw new BusinessException("La liste de produits ne peut pas être vide.");
        }
        try {
            checkoutDA.addCheckout(products);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'enregistrement du checkout.", e);
        }
    }

    public void addCheckout(List<Product> products, int clientId) throws BusinessException {
        // Validation
        if (products == null || products.isEmpty()) {
            throw new BusinessException("La liste de produits ne peut pas être vide.");
        }
        if (clientId <= 0) {
            throw new BusinessException("L'identifiant client est invalide.");
        }
        try {
            // Règle métier — vérifier que le client existe
            super.getClientSupplier(clientId);
            checkoutDA.addCheckout(products, clientId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'enregistrement du checkout.", e);
        }
    }

    public void addCheckout(List<Product> products, int clientId,
                            FidelityCard fidelityCard, boolean useFidelityPoint) throws BusinessException {
        if (products == null || products.isEmpty()) {
            throw new BusinessException("La liste de produits ne peut pas être vide.");
        }
        if (clientId <= 0) {
            throw new BusinessException("L'identifiant client est invalide.");
        }
        if (fidelityCard == null) {
            throw new BusinessException("La carte de fidélité est invalide.");
        }
        try {
            // Règle métier — vérifier que la carte appartient bien au client
            if (!fidelityCardDA.validateFidelityCardOwnership(clientId, fidelityCard.getId())) {
                throw new BusinessException("Cette carte de fidélité n'appartient pas à ce client.");
            }
            checkoutDA.addCheckout(products, clientId, fidelityCard, useFidelityPoint);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'enregistrement du checkout.", e);
        }
    }

    public void addCheckout(List<Product> products, int clientId,
                            FidelityCard fidelityCard) throws BusinessException {
        // Délègue à la version complète sans utiliser les points
        addCheckout(products, clientId, fidelityCard, false);
    }

    public void deleteClientAccount(int clientId) throws BusinessException {
        if (clientId <= 0) {
            throw new BusinessException("L'identifiant client est invalide.");
        }
        try {
            // Orchestration — supprimer la carte de fidélité avant le compte
            if (fidelityCardDA.hasFidelityCard(clientId)) {
                fidelityCardDA.deleteFidelityCard(clientId);
            }
            super.deleteClientAccount(clientId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la suppression du compte client.", e);
        }
    }

    public void deleteClientAccount(int clientId, int cardId) throws BusinessException {
        if (clientId <= 0 || cardId <= 0) {
            throw new BusinessException("Les identifiants fournis sont invalides.");
        }
        try {
            // Règle métier — vérifier que la carte appartient bien au client avant suppression
            if (!fidelityCardDA.validateFidelityCardOwnership(clientId, cardId)) {
                throw new BusinessException("Cette carte de fidélité n'appartient pas à ce client.");
            }
            fidelityCardDA.deleteFidelityCard(clientId);
            super.deleteClientAccount(clientId, cardId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la suppression du compte client.", e);
        }
    }

    @Override
    public void placeOrder(int clientSupplierId, List<Product> products) throws BusinessException {
        if (clientSupplierId <= 0) {
            throw new BusinessException("L'identifiant client est invalide.");
        }
        if (products == null || products.isEmpty()) {
            throw new BusinessException("La liste de produits ne peut pas être vide.");
        }
        try {
            // Règle métier — vérifier que le client existe avant de passer commande
            super.getClientSupplier(clientSupplierId);
            checkoutDA.addCheckout(products, clientSupplierId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la passation de commande.", e);
        }
    }
}