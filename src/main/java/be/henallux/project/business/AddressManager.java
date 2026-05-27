package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.AddressDA;
import main.java.be.henallux.project.data.LocalityDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.Locality;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.util.List;

public class AddressManager {
    
    private final AddressDA addressDA;
    private final LocalityDA localityDA;

    public AddressManager() {
        this.addressDA = AddressDA.getInstance();
        this.localityDA = LocalityDA.getInstance();
    }

    public List<Address> getAllAddresses() throws BusinessException, DataValidationException {
        try {
            return addressDA.getAll();
        } catch (DataBaseException e) {
            throw new BusinessException("Error cannot retrieve addresses.", e);
        }
    }

    public boolean createLocality(Locality locality) throws BusinessException, DataValidationException {
        // Validation
        if (locality == null) {
            throw new BusinessException("locality is null.");
        }
        if (locality.getCity() == null || locality.getCity().isEmpty()) {
            throw new BusinessException("locality.city is null.");
        }
        try {
            if (!localityDA.checkExist(locality)) {
                localityDA.insert(locality);
                return true;
            }
        }
        catch (DataBaseException e) {
            throw new BusinessException("Error cannot insert locality.", e);
        }
        return false;
    }

    public boolean createAddress(Address address, Locality locality) throws BusinessException, DataValidationException {
        // Validation
        if (address == null) {
            throw new BusinessException("Error: Address cannot be null.");
        }
        if (address.getStreetName() == null || address.getStreetName().isBlank()) {
            throw new BusinessException("Error: Street name is required.");
        }
        try {
            if (localityDA.checkExist(locality)) {
                addressDA.insert(address);
                return true;
            }
        } catch (DataBaseException e) {
            throw new BusinessException("Error creating address.", e);
        }
        return false;
    }

    public boolean delete(Address address) throws BusinessException, DataValidationException {
        if (address == null) {
            throw new BusinessException("Error: Address cannot be null.");
        }
        try {
            // Règle métier — vérifier que l'adresse existe avant de la supprimer
            if ((addressDA.checkExist(address))) {
                addressDA.delete(address);
                return true;
            }
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la suppression de l'adresse.", e);
        }
        return false;
    }
}
