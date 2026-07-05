package be.henallux.project.business;

import be.henallux.project.data.AddressDA;
import be.henallux.project.data.LocalityDA;
import be.henallux.project.data.exception.DataBaseException;
import be.henallux.project.business.exception.BusinessException;
import be.henallux.project.model.Address;
import be.henallux.project.model.Locality;
import be.henallux.project.model.exception.DataValidationException;

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

    public Address getAddressById(int id) throws BusinessException, DataValidationException {
        try {
            return addressDA.getById(id);
        } catch (DataBaseException e) {
            throw new BusinessException("Error cannot retrieve address.", e);
        }
    }

    public Locality getLocalityById(int postalCode) throws BusinessException, DataValidationException {
        try {
            return localityDA.getById(postalCode);
        } catch (DataBaseException e) {
            throw new BusinessException("Error cannot retrieve locality.", e);
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

    public Address createAddress(Address address, Locality locality) throws BusinessException, DataValidationException {
        // Validation
        if (address == null) {
            throw new BusinessException("Error: Address cannot be null.");
        }
        if (address.getStreetName() == null || address.getStreetName().isBlank()) {
            throw new BusinessException("Error: Street name is required.");
        }
        try {
            createLocality(locality);

            List<Address> existing = addressDA.getByLocality(locality);

            Address found = null;

            for (Address a : existing) {
                if (found == null
                        && a.getStreetName().equalsIgnoreCase(address.getStreetName())
                        && a.getStreetNumber() == address.getStreetNumber()) {
                    found = a;
                }
            }

            if (found == null) {
                addressDA.insert(address);
            } else {
                address.setAddressId(found.getAddressId());
            }
        } catch (DataBaseException e) {
            throw new BusinessException("Error creating address.", e);
        }
        return address;
    }

    public boolean delete(Address address) throws BusinessException, DataValidationException {
        if (address == null) {
            throw new BusinessException("Error: Address cannot be null.");
        }
        try {
            // Business rule - address must exist
            if ((addressDA.checkExist(address))) {
                addressDA.delete(address);
                return true;
            }
        } catch (DataBaseException e) {
            throw new BusinessException("Error when deleting address.", e);
        }
        return false;
    }
}
