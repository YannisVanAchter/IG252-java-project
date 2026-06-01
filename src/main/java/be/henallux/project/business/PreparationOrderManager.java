package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.RecipeDA;
import main.java.be.henallux.project.data.DocumentDA;
import main.java.be.henallux.project.data.ProductDA;

import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.Recipe;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.RecipeComposition;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.util.HashMap;
import java.util.List;

public class PreparationOrderManager extends DocumentManager {

private final RecipeDA recipeDA;

    public PreparationOrderManager() {
        super();
        this.recipeDA = RecipeDA.getInstance();
    }

    public List<Recipe> getAllRecipes() throws BusinessException, DataValidationException {
        try {
            return recipeDA.getAll();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving all recipes.", e);
        }
    }

    public Recipe getRecipe(int recipeId) throws BusinessException {
        try {
            return recipeDA.getById(recipeId, true);
        } catch (DataBaseException | DataValidationException e) {
            throw new BusinessException("Error when retrieving the recipe.", e);
        }
    }

    public List<RecipeComposition> getIngredients(int recipeId) throws BusinessException {
        try {
            Recipe recipe = recipeDA.getById(recipeId, true);
            if (recipe == null) throw new BusinessException("Recipe not found.");
            return recipe.getComposition();
        } catch (DataBaseException | DataValidationException e) {
            throw new BusinessException("Error when retrieving ingredients.", e);
        }
    }

    public Recipe createRecipe(Recipe recipe, HashMap<Product, Integer> ingredients) throws BusinessException {
        if (recipe == null) {
            throw new BusinessException("The recipe cannot be null.");
        }
        if (ingredients == null || ingredients.isEmpty()) {
            throw new BusinessException("The ingredients cannot be null or empty.");
        }
        try {
            recipeDA.insert(recipe);
            return recipe;
        } catch (DataBaseException | DataValidationException e ) {
            throw new BusinessException("Error when creating the recipe.", e);
        }
    }
}
