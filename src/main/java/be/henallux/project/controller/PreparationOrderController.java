package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.PreparationOrderManager;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.Recipe;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import javafx.util.Pair;

/**
 * @see PreparationOrderManager
 * @see DocumentController
 */
public class PreparationOrderController extends DocumentController {

    private final PreparationOrderManager preparationOrderManager;

    public PreparationOrderController() {
        this.preparationOrderManager = new PreparationOrderManager();
    }

    /**
     * Returns all recipes.
     * @return list of all {@link Recipe}
     * @see PreparationOrderManager#getAllRecipes()
     */
    public ArrayList<Recipe> getAllRecipes() throws BusinessException {
        return new ArrayList<>(preparationOrderManager.getAllRecipes());
    }

    /**
     * Returns a recipe by name.
     * @param recipeName the name of the recipe
     * @return the matching {@link Recipe}
     * @see PreparationOrderManager#getRecipe(String)
     */
    public Recipe getRecipe(String recipeName) throws BusinessException {
        return preparationOrderManager.getRecipe(recipeName);
    }

    /**
     * Returns the ingredients of a recipe.
     * @param recipeName the name of the recipe
     * @return list of entries mapping each {@link Product} to its quantity
     * @see PreparationOrderManager#getIngredient(String)
     */
    public ArrayList<Map.Entry<Product, Integer>> getIngredients(String recipeName) throws BusinessException {
        return new ArrayList<>(preparationOrderManager.getIngredient(recipeName));
    }

    /**
     * Creates a new recipe with its ingredients.
     * @param newRecipe   the {@link Recipe} to create
     * @param ingredients list of entries mapping each {@link Product} to its quantity
     * @return the created {@link Recipe}
     * @see PreparationOrderManager#createRecipe(Recipe, List)
     */
    public Recipe createNewRecipe(Recipe newRecipe, ArrayList<Map.Entry<Product, Integer>> ingredients) throws BusinessException {
        List<Pair<Product, Integer>> newIngredients = new ArrayList<>();
        for (Map.Entry<Product, Integer> e : ingredients) {
            newIngredients.add(new Pair<>(e.getKey(), e.getValue()));
        }
        return preparationOrderManager.createRecipe(newRecipe, newIngredients);
    }
}