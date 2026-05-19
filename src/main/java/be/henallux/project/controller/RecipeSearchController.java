package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                for (RecipeComposition comp : r.getComposition()) {
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
    
    
    private ArrayList<Recipe> getAllRecipes() {

        try {
            ArrayList<Recipe> recipes = new ArrayList<>();

            // CATEGORIES
            ProductCategory bakingCategory = new ProductCategory(1, "Baking");
            ProductCategory dairyCategory = new ProductCategory(2, "Dairy");
            ProductCategory freshCategory = new ProductCategory(3, "Fresh");

            Product flour = new Product(6, "Flour",
                    new BigDecimal("2.50"), new BigDecimal("6"),
                    10, true, 10, bakingCategory, null, null);
            flour.setLocation(new ArrayList<>(List.of(new QuantityProduct(
                    new LocationProduct("A", "1", false, false), flour, 100))));

            Product sugar = new Product(7, "Sugar",
                    new BigDecimal("1.80"), new BigDecimal("6"),
                    8, true, 5, bakingCategory, null, null);
            sugar.setLocation(new ArrayList<>(List.of(new QuantityProduct(
                    new LocationProduct("A", "2", false, false), sugar, 80))));
            sugar.addDiscount(new Discount(2, new BigDecimal("10"),
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31),
                    "10% Sugar promo", sugar));

            Product milk = new Product(8, "Milk",
                    new BigDecimal("1.20"), new BigDecimal("6"),
                    5, true, 10, dairyCategory, null, null);
            milk.setLocation(new ArrayList<>(List.of(new QuantityProduct(
                    new LocationProduct("B", "1", true, true), milk, 50))));

            Product egg = new Product(9, "Egg",
                    new BigDecimal("3.40"), new BigDecimal("6"),
                    12, true, 30, freshCategory, null, null);
            egg.setLocation(new ArrayList<>(List.of(new QuantityProduct(
                    new LocationProduct("B", "2", true, false), egg, 200))));

            Product chocolate = new Product(10, "Chocolate",
                    new BigDecimal("4.90"), new BigDecimal("21"),
                    20, true, 5, bakingCategory, null, null);
            chocolate.setLocation(new ArrayList<>(List.of(new QuantityProduct(
                    new LocationProduct("A", "3", false, false), chocolate, 40))));
            chocolate.addDiscount(new Discount(2, new BigDecimal("10"),
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31),
                    "10% Chocolate promo", chocolate));

            Recipe pancake = new Recipe(1, "Pancake", "Mix ingredients and cook in a pan.", flour, null);
            pancake.addProductInComposition(flour, 200);
            pancake.addProductInComposition(sugar, 50);
            pancake.addProductInComposition(egg, 2);
            pancake.addProductInComposition(milk, 300);
            recipes.add(pancake);

            Recipe chocolateCake = new Recipe(2, "Chocolate Cake", "Bake everything in the oven.", flour, null);
            chocolateCake.addProductInComposition(flour, 250);
            chocolateCake.addProductInComposition(sugar, 100);
            chocolateCake.addProductInComposition(egg, 3);
            chocolateCake.addProductInComposition(chocolate, 150);
            recipes.add(chocolateCake);

            Recipe omelette = new Recipe(3, "Omelette", "Cook eggs in a pan.", egg, null);
            omelette.addProductInComposition(egg, 4);
            omelette.addProductInComposition(milk, 100);
            recipes.add(omelette);

            return recipes;
        } catch (DataValidationException e) {
            return new ArrayList<>();
        }
    }
}