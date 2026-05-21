package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class AddressManager {
    
    public List<Address> getAllAddresses() throws DataBaseException {
        AddressData data = new AddressData();
        return data.getAllAddresses();
    }

    public void createAddress(Address address) throws DataBaseException {
        AddressData data = new AddressData();
        data.createAddress(address);
    }

    public void createLocality(Locality locality) throws DataBaseException {
        AddressData data = new AddressData();
        data.createLocality(locality);
    }

    public void deleteAddress(Address address) throws DataBaseException {
        AddressData data = new AddressData();
        data.deleteAddress(address);
    }
}
