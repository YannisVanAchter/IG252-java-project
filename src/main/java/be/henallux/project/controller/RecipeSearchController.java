package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.RecipeSearchManager;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * @see main.java.be.henallux.project.view.RecipeSearchTable
 * @see RecipeSearchManager
 */
public class RecipeSearchController {

    private final RecipeSearchManager recipeSearchManager;

    public RecipeSearchController() {
        this.recipeSearchManager = new RecipeSearchManager();
    }

    /**
     * Searches for recipes by name and ingredients.
     * @param name        the recipe name to search for, or {@code null} to ignore
     * @param ingredients list of ingredient names to filter, or {@code null} to ignore
     * @return list of matching {@link Recipe}
     * @see RecipeSearchManager#searchRecipes(String, List)
     */
    public ArrayList<Recipe> searchRecipes(String name, List<String> ingredients) throws BusinessException {
        return new ArrayList<>(recipeSearchManager.searchRecipes(name, ingredients));
    }
}