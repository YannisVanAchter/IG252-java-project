package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Product;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Controller fictif utilisé uniquement pour simuler les vues de l'application.
 * <p>
 * TODO: clean & implement class.
 */
public class SupplierController {

    private final ClientSupplierController clientSupplierController = new ClientSupplierController();

    /**
     * Retourne tous les fournisseurs disponibles (mock data)
     *
     * @return liste de fournisseurs
     */
    public ArrayList<ClientSupplier> getAllSuppliers() {

        try {
            ArrayList<ClientSupplier> allClients = clientSupplierController.getAllClientSupplier();
            ArrayList<ClientSupplier> suppliers = new ArrayList<>();

            for (ClientSupplier cs : allClients) {
                if (cs.getIsSupplier()) {
                    suppliers.add(cs);
                }
            }
            return suppliers;
        } catch (DataValidationException e) {
            return new ArrayList<>();
        }
    }
    /**
     * Retourne tous les produits d'un fournisseur (mock data)
     *
     * @param supplierID id du fournisseur
     * @return liste de produits
     */
    public ArrayList<Product> getAllProduct(int supplierID) {
        ProductController product = new ProductController();
        return product.getAllProduct();
    }
}