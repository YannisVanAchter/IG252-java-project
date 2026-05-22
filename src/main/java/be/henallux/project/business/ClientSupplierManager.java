package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.*;
import java.util.List;

public abstract class ClientSupplierManager {

    private final ClientSupplierData clientSupplierData;

    public ClientSupplierManager(ClientSupplierData clientSupplierData) {
        this.clientSupplierData = clientSupplierData;
    }
    
    public List<ClientSupplier> getAllClientSuppliers() throws BusinessException {
        try {
            return clientSupplierData.getAllClientSuppliers();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des fournisseurs clients.", e);
        }
    }

    public ClientSupplier getClientSupplier(int id) throws BusinessException {        
        if (id <= 0) {
            throw new BusinessException("L'identifiant du fournisseur client doit être un nombre positif.");
        }
        
        try {
            ClientSupplier result = clientSupplierData.getClientSupplier(id);
            if (result == null) {
                throw new BusinessException("Le fournisseur client n'existe pas.");
            }
            return result;
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération du fournisseur client.", e);
        }
    }

    public void createClientSupplier(ClientSupplier clientSupplier) throws BusinessException {
        if (clientSupplier == null) {
            throw new BusinessException("Le fournisseur client ne peut pas être nul.");
        }        
        if (clientSupplier.getName() == null || clientSupplier.getName().isBlank()) {
            throw new BusinessException("Le nom du fournisseur client est obligatoire.");
        }
        try {
            clientSupplierData.createClientSupplier(clientSupplier);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la création du fournisseur client.", e);
        }
    }

    public void changeAddress(int id, Address address) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("L'identifiant du fournisseur client doit être un nombre positif.");
        }
        if (address == null) {
            throw new BusinessException("L'adresse ne peut pas être nulle.");
        }
        try {
            clientSupplierData.changeAddress(id, address);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement d'adresse.", e);
        }
    }

    public void changePhoneNumber(int id, int phoneNumber) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("L'identifiant du fournisseur client doit être un nombre positif.");
        }
        if (phoneNumber <= 0) {
            throw new BusinessException("Le numéro de téléphone doit être un nombre positif.");
        }
        if (String.valueOf(phoneNumber).length() < 7 || String.valueOf(phoneNumber).length() > 15) {
            throw new BusinessException("Le numéro de téléphone doit comporter entre 7 et 15 chiffres.");
        }
        try {
            clientSupplierData.changePhoneNumber(id, phoneNumber);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement de numéro de téléphone.", e);
        }
    }

    public void changeEmail(int id, String email) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("L'identifiant du fournisseur client doit être un nombre positif.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessException("L'e-mail ne peut pas être nul ou vide.");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BusinessException("L'e-mail n'est pas dans un format valide.");
        }
        try {
            clientSupplierData.changeEmail(id, email);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement d'e-mail.", e);
        }
    }

    public void deleteClientSupplier(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("L'identifiant du fournisseur client doit être un nombre positif.");
        }
        try {
            clientSupplierData.deleteClientSupplier(id);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la suppression du fournisseur client.", e);
        }
    }

    public abstract void placeOrder(int clientSupplierId, List<Product> products) throws BusinessException;
}
