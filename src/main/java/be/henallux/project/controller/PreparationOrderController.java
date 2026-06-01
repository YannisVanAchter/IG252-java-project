package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.PreparationOrderManager;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.Recipe;
import main.java.be.henallux.project.model.RecipeComposition;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public ArrayList<Recipe> getAllRecipes() throws BusinessException, DataValidationException {
        return new ArrayList<>(preparationOrderManager.getAllRecipes());
    }

    /**
     * Returns a recipe by id.
     * @param recipeId the id of the recipe
     * @return the matching {@link Recipe}
     * @see PreparationOrderManager#getRecipe(int)
     */
    public Recipe getRecipe(int recipeId) throws BusinessException {
        return preparationOrderManager.getRecipe(recipeId);
    }

    /**
     * Returns the ingredients of a recipe.
     * @param recipeId the id of the recipe
     * @return list of {@link RecipeComposition} for the recipe
     * @see PreparationOrderManager#getIngredients(int)
     */
    public List<RecipeComposition> getIngredients(int recipeId) throws BusinessException {
        return preparationOrderManager.getIngredients(recipeId);
    }

    /**
     * Creates a new recipe with its ingredients.
     * @param newRecipe   the {@link Recipe} to create
     * @param ingredients map of each {@link Product} to its quantity
     * @return the created {@link Recipe}
     * @see PreparationOrderManager#createRecipe(Recipe, HashMap)
     */
    public Recipe createNewRecipe(Recipe newRecipe, HashMap<Product, Integer> ingredients) throws BusinessException {
        return preparationOrderManager.createRecipe(newRecipe, ingredients);
    }
}