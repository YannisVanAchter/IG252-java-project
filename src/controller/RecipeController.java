package controller;

import model.*;

import java.util.ArrayList;
import java.util.Arrays;

public class RecipeController {

    public ArrayList<Recipe> getAllRecipe() {

        ArrayList<Recipe> recipes = new ArrayList<>();

        // LOCATIONS
        Location refrigeratedLocation = new Location(true);
        Location normalLocation = new Location(false);

        // CATEGORIES
        ProductCategory bakingCategory = new ProductCategory("Baking");
        ProductCategory dairyCategory = new ProductCategory("Dairy");
        ProductCategory freshCategory = new ProductCategory("Fresh");

        // PROMOTIONS
        Promotion promo10 = new Promotion(
                "10%",
                2,
                "2026-05-01",
                "2026-05-31"
        );

        // PRODUCTS

        Product flour = new Product(
                "Flour",
                2.50,
                6.0,
                10,
                null,
                bakingCategory,
                new QuantityProduct(100, 1, 1, normalLocation),
                10
        );

        Product sugar = new Product(
                "Sugar",
                1.80,
                6.0,
                8,
                promo10,
                bakingCategory,
                new QuantityProduct(80, 1, 2, normalLocation),
                5
        );

        Product milk = new Product(
                "Milk",
                1.20,
                6.0,
                5,
                null,
                dairyCategory,
                new QuantityProduct(50, 2, 1, refrigeratedLocation),
                10
        );

        Product egg = new Product(
                "Egg",
                3.40,
                6.0,
                12,
                null,
                freshCategory,
                new QuantityProduct(200, 3, 1, refrigeratedLocation),
                30
        );

        Product chocolate = new Product(
                "Chocolate",
                4.90,
                21.0,
                20,
                promo10,
                bakingCategory,
                new QuantityProduct(40, 1, 3, normalLocation),
                5
        );

        // =========================
        // RECIPE 1 : PANCAKE
        // =========================

        Recipe pancake = new Recipe(
                "Pancake",
                "Mix ingredients and cook in a pan."
        );

        ArrayList<RecipeComposition> pancakeCompositions = new ArrayList<>(
                Arrays.asList(
                        new RecipeComposition(200, pancake, flour),
                        new RecipeComposition(50, pancake, sugar),
                        new RecipeComposition(2, pancake, egg),
                        new RecipeComposition(300, pancake, milk)
                )
        );

        pancake.setCompositions(pancakeCompositions);

        // =========================
        // RECIPE 2 : CHOCOLATE CAKE
        // =========================

        Recipe chocolateCake = new Recipe(
                "Chocolate Cake",
                "Bake everything in the oven."
        );

        ArrayList<RecipeComposition> cakeCompositions = new ArrayList<>(
                Arrays.asList(
                        new RecipeComposition(250, chocolateCake, flour),
                        new RecipeComposition(100, chocolateCake, sugar),
                        new RecipeComposition(3, chocolateCake, egg),
                        new RecipeComposition(150, chocolateCake, chocolate)
                )
        );

        chocolateCake.setCompositions(cakeCompositions);

        // =========================
        // RECIPE 3 : OMELETTE
        // =========================

        Recipe omelette = new Recipe(
                "Omelette",
                "Cook eggs in a pan."
        );

        ArrayList<RecipeComposition> omeletteCompositions = new ArrayList<>(
                Arrays.asList(
                        new RecipeComposition(4, omelette, egg),
                        new RecipeComposition(100, omelette, milk)
                )
        );

        omelette.setCompositions(omeletteCompositions);

        // ADD RECIPES
        recipes.add(pancake);
        recipes.add(chocolateCake);
        recipes.add(omelette);

        return recipes;
    }
}