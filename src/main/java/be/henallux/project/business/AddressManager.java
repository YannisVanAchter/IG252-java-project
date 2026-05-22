package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.*;
import java.util.List;

public class AddressManager {
    
    private final AddressData addressData;

    public AddressManager(AddressData addressData) {
        this.addressData = addressData;
    }

    public List<Address> getAllAddresses() throws BusinessException {
        try {
            return addressData.getAllAddresses();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des adresses.", e);
        }
    }

    public void createAddress(Address address) throws BusinessException {
        // Validation
        if (address == null) {
            throw new BusinessException("L'adresse ne peut pas être nulle.");
        }
        if (address.getStreet() == null || address.getStreet().isBlank()) {
            throw new BusinessException("La rue de l'adresse est obligatoire.");
        }
        try {
            addressData.createAddress(address);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la création de l'adresse.", e);
        }
    }

    public void createLocality(Locality locality) throws BusinessException {
        if (locality == null) {
            throw new BusinessException("La localité ne peut pas être nulle.");
        }
        if (locality.getPostalCode() == null || locality.getPostalCode().isBlank()) {
            throw new BusinessException("Le code postal est obligatoire.");
        }
        try {
            addressData.createLocality(locality);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la création de la localité.", e);
        }
    }

    public void deleteAddress(Address address) throws BusinessException {
        if (address == null) {
            throw new BusinessException("L'adresse ne peut pas être nulle.");
        }
        try {
            // Règle métier — vérifier que l'adresse existe avant de la supprimer
            if (!addressData.addressExists(address)) {
                throw new BusinessException("L'adresse n'existe pas.");
            }
            addressData.deleteAddress(address);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la suppression de l'adresse.", e);
        }
    }
}
