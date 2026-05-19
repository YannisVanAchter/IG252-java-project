package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Product;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Controller fictif utilisé uniquement pour simuler les vues de l'application.
 *
 * TODO: clean & implement class.
 */
public class SupplierController {

    /**
     * Retourne tous les fournisseurs disponibles (mock data)
     *
     * @return liste de fournisseurs
     */
    public ArrayList<ClientSupplier> getAllSuppliers() throws DataValidationException {
        ArrayList<ClientSupplier> suppliers = new ArrayList<>();

        Address address = new Address("Rue", 48, "Namur", 5000);
        suppliers.add(new ClientSupplier(
                101,
                "Lefevre",
                "Paul",
                "paul.lefevre@supplier.com",
                "32470001001",
                address,
                false,
                true,
                false,
                "BE1000000001",
                LocalDate.now()
        ));

        suppliers.add(new ClientSupplier(
                102,
                "Vermeulen",
                "Anna",
                "anna.vermeulen@supplier.com",
                "32470001002",
                address,
                false,
                true,
                false,
                "BE1000000002",
                LocalDate.now()
        ));

        suppliers.add(new ClientSupplier(
                103,
                "Dubois",
                "Marc",
                "marc.dubois@supplier.com",
                "32470001003",
                address,
                false,
                true,
                false,
                "BE1000000003",
                LocalDate.now()
        ));

        return suppliers;
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