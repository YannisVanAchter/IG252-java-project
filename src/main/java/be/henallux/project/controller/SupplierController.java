package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.SupplierManager;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * @see SupplierManager
 * @see ClientSupplierController
 */
public class SupplierController extends ClientSupplierController {

    private SupplierManager supplierManager;

    public SupplierController() {
        this.supplierManager = new SupplierManager();
    }

    /**
     * Returns all suppliers.
     * @return list of all {@link ClientSupplier} of type supplier
     * @see SupplierManager#getAllSuppliers()
     */
    public ArrayList<ClientSupplier> getAllSuppliers() throws BusinessException {
        return new ArrayList<>(supplierManager.getAllSuppliers());
    }

    /**
     * Returns all products offered by a supplier.
     * @param supplierID the supplier ID
     * @return list of {@link Product} from the given supplier
     * @see SupplierManager#getAllProducts(int)
     */
    public ArrayList<Product> getAllProduct(int supplierID) throws BusinessException {
        return new ArrayList<>(supplierManager.getAllProducts(supplierID));
    }

    /**
     * Returns the supplier associated with a given product.
     * @param productID the product ID
     * @return the matching {@link ClientSupplier}
     * @see SupplierManager#getSupplierByProduct(int)
     */
    public ClientSupplier getSupplierByProduct(int productID) throws BusinessException {
        return supplierManager.getSupplierByProduct(productID);
    }

    /**
     * Changes the VAT number of a supplier.
     * @param supplierID    the supplier ID
     * @param newVATNumber  the new VAT number
     * @see SupplierManager#changeVATNumber(int, String)
     */
    public void changeVATNumber(int supplierID, String newVATNumber) throws BusinessException {
        supplierManager.changeVATNumber(supplierID, newVATNumber);
    }

    /**
     * //TODO quoi faire ?
     * @param clientSupplierID the supplier ID
     * @param products         list of {@link Product} to order
     * @throws BusinessException not thrown
     */
    @Override
    public void placeOrder(int clientSupplierID, List<Product> products) throws BusinessException {

    }
}