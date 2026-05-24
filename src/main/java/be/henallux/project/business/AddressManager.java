package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.AddressDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.Locality;
import main.java.be.henallux.project.model.exception.DataValidationException;
import java.util.List;

public class AddressManager {
    
    private final AddressDA addressDA;

    public AddressManager() {
        this.addressDA = AddressDA.getInstance();
    }

    public List<Address> getAllAddresses() throws BusinessException {
        try {
            return addressDA.getAll();
        } catch (DataBaseException e) {
            throw new BusinessException("Error cannot retrieve addresses.", e);
        }
    }

    public void createAddress(Address address) throws BusinessException {
        // Validation
        if (address == null) {
            throw new BusinessException("Error: Address cannot be null.");
        }
        if (address.getStreetName() == null || address.getStreetName().isBlank()) {
            throw new BusinessException("Error: Street name is required.");
        }
        try {
            addressDA.insert(address);
        } catch (DataBaseException e) {
            throw new BusinessException("Error creating address.", e);
        }
    }
/*/
    public void createLocality(Locality locality) throws BusinessException {
        if (locality == null) {
            throw new BusinessException("Error: Locality cannot be null.");
        }
        if (locality.getPostalCode() == null || locality.getPostalCode().isBlank()) {
            throw new BusinessException("Error: Postal code is required.");
        }
        try {
            addressDA.createLocality(locality);
        } catch (DataBaseException e) {
            throw new BusinessException("Error creating locality.", e);
        }
    }

    public void deleteAddress(Address address) throws BusinessException {
        if (address == null) {
            throw new BusinessException("Error: Address cannot be null.");
        }
        try {
            // Règle métier — vérifier que l'adresse existe avant de la supprimer
            if (!addressDA.addressExists(address)) {
                throw new BusinessException("Error: Address does not exist.");
            }
            addressDA.deleteAddress(address);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la suppression de l'adresse.", e);
        }
    }
*/
}
