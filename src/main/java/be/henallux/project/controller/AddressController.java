package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.AddressManager;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.data.AddressDA;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.Locality;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.util.List;

public class AddressController {

    private final AddressManager addressManager;

    public AddressController() {
        this.addressManager = new AddressManager();
    }

    /**
     * Returns all addresses.
     * @return list of all {@link Address}
     * @see AddressManager#getAllAddresses()
     */
    public List<Address> getAllAddresses() throws BusinessException, DataValidationException {
        return addressManager.getAllAddresses();
    }

    /**
     * Creates a new address.
     * @param address the {@link Address} to create
     * @see AddressManager#createAddress(Address, Locality)
     */
    public void createAddress(Address address, Locality locality) throws BusinessException, DataValidationException {
        addressManager.createAddress(address, locality);
    }

    /**
     * Creates a new locality.
     * @param locality the {@link Locality} to create
     * @see AddressManager#createLocality(Locality) (Locality)
     */
    public void createLocality(Locality locality) throws BusinessException, DataValidationException {
        addressManager.createLocality(locality);
    }


    /**
     * Deletes an existing address.
     * @param address the {@link Address} to delete
     * @see AddressManager#delete(Address)
     */
    public void deleteAddress(Address address) throws BusinessException, DataValidationException {
        addressManager.delete(address);
    }
}