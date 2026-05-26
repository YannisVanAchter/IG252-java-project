package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.AddressManager;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.Locality;

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
    public List<Address> getAllAddress() throws BusinessException {
        return addressManager.getAllAddresses();
    }

    /**
     * Creates a new address.
     * @param address the {@link Address} to create
     * @see AddressManager#createAddress(Address)
     */
    public void createAddress(Address address) throws BusinessException {
        addressManager.createAddress(address);
    }

    /**
     * Creates a new locality.
     * @param locality the {@link Locality} to create
     * @see AddressManager#createLocality(Locality)
     */
    public void createLocality(Locality locality) throws BusinessException {
        addressManager.createLocality(locality);
    }


    /**
     * Deletes an existing address.
     * @param address the {@link Address} to delete
     * @see AddressManager#deleteAddress(Address)
     */
    public void deleteAddress(Address address) throws BusinessException {
        addressManager.deleteAddress(address);
    }
}