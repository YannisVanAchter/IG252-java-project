package controller;

import model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Controller fictif pour simuler la recherche de recette via bd.
 * TODO: remplacer par une vraie requête vers db package
 */
public class RecipeSearchController {

    public ArrayList<Recipe> searchRecipes(String name, String product) {
        ArrayList<Recipe> results = new ArrayList<>();

        for (Recipe r : getAllRecipes()) {
            boolean match = true;

            if (name != null && !r.getName().toLowerCase().contains(name.toLowerCase())) {
                match = false;
            }

            if (product != null) {
                boolean found = false;
                for (RecipeComposition comp : r.getCompositions()) {
                    if (comp.getProduct().getName().toLowerCase().contains(product.toLowerCase())) {
                        found = true;
                        break;
                    }
                }
                if (!found) match = false;
            }

            if (match) results.add(r);
        }

        return results;
    }

    // =========================
    // FAKE DATA (ancien RecipeController)
    // =========================
    private ArrayList<Recipe> getAllRecipes() {

        ArrayList<Recipe> recipes = new ArrayList<>();

        // LOCATIONS
        Location refrigeratedLocation = new Location(true);
        Location normalLocation = new Location(false);

        // CATEGORIES
        ProductCategory bakingCategory = new ProductCategory("Baking");
        ProductCategory dairyCategory = new ProductCategory("Dairy");
        ProductCategory freshCategory = new ProductCategory("Fresh");

        // PRODUCTS
        Product flour = new Product(6, "Flour", 2.50, 6.0, 10, null,
                bakingCategory, new QuantityProduct(100, 1, 1, normalLocation), 10);

        Product sugar = new Product(7, "Sugar", 1.80, 6.0, 8, null,
                bakingCategory, new QuantityProduct(80, 1, 2, normalLocation), 5);

        Product milk = new Product(8, "Milk", 1.20, 6.0, 5, null,
                dairyCategory, new QuantityProduct(50, 2, 1, refrigeratedLocation), 10);

        Product egg = new Product(9, "Egg", 3.40, 6.0, 12, null,
                freshCategory, new QuantityProduct(200, 3, 1, refrigeratedLocation), 30);

        Product chocolate = new Product(10, "Chocolate", 4.90, 21.0, 20, null,
                bakingCategory, new QuantityProduct(40, 1, 3, normalLocation), 5);

        // DISCOUNTS
        Discount promo10Sugar = new Discount(2, 10,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31),
                "10% Sugar promo",
                sugar);
        sugar.setDiscount(promo10Sugar);

        Discount promo10Chocolate = new Discount(2, 10,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31),
                "10% Chocolate promo",
                chocolate);
        chocolate.setDiscount(promo10Chocolate);

        // RECIPE 1
        Recipe pancake = new Recipe("Pancake", "Mix ingredients and cook in a pan.");
        pancake.setCompositions(new ArrayList<>(Arrays.asList(
                new RecipeComposition(200, pancake, flour),
                new RecipeComposition(50, pancake, sugar),
                new RecipeComposition(2, pancake, egg),
                new RecipeComposition(300, pancake, milk)
        )));

        // RECIPE 2
        Recipe chocolateCake = new Recipe("Chocolate Cake", "Bake everything in the oven.");
        chocolateCake.setCompositions(new ArrayList<>(Arrays.asList(
                new RecipeComposition(250, chocolateCake, flour),
                new RecipeComposition(100, chocolateCake, sugar),
                new RecipeComposition(3, chocolateCake, egg),
                new RecipeComposition(150, chocolateCake, chocolate)
        )));

        // RECIPE 3
        Recipe omelette = new Recipe("Omelette", "Cook eggs in a pan.");
        omelette.setCompositions(new ArrayList<>(Arrays.asList(
                new RecipeComposition(4, omelette, egg),
                new RecipeComposition(100, omelette, milk)
        )));

        recipes.add(pancake);
        recipes.add(chocolateCake);
        recipes.add(omelette);

        return recipes;
    }
}