package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.model.Product;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller fictif pour simuler la recherche de produits via bd.
 * TODO: remplacer par une vraie requête vers db package
 */
public class ProductSearchController {

    public ArrayList<Product> searchProducts(String name, String category, Boolean promotion) {
        ArrayList<Product> results = new ArrayList<>();

        for (Product p : getAllFakeProducts()) {
            boolean match = true;

            if (name != null && !p.getName().toLowerCase().contains(name.toLowerCase()))
                match = false;

            if (category != null && !category.equals("All")
                    && (p.getCategory() == null
                    || !p.getCategory().getName().equalsIgnoreCase(category)))
                match = false;

            if (promotion != null && promotion && !p.isInPromotion())
                match = false;

            if (match) results.add(p);
        }

        return results;
    }

    private List<Product> getAllFakeProducts() {
        return new ProductController().getAllProduct();
    }
}