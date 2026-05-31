package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.RecipeDA;
import main.java.be.henallux.project.data.DocumentDA;
import main.java.be.henallux.project.data.ProductDA;

import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.Recipe;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.util.HashMap;
import java.util.List;

public class PreparationOrderManager extends DocumentManager {

private final RecipeDA recipeDA;

    public PreparationOrderManager() {
        super();
        this.recipeDA = RecipeDA.getInstance();
    }

    public List<Recipe> getAllRecipes() throws BusinessException {
        try {
            return recipeDA.getAll();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving all recipes.", e);
        }
    }

    public Recipe getRecipe(String recipeName) throws BusinessException {
        if (recipeName == null || recipeName.isBlank()) {
            throw new BusinessException("The recipe name cannot be null or blank.");
        }
        try {
            return recipeDA.getRecipe(recipeName);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the recipe.", e);
        }
    }

    public HashMap<Product, Integer> getIngredient(String recipeName) throws BusinessException {
        if (recipeName == null || recipeName.isBlank()) {
            throw new BusinessException("The recipe name cannot be null or blank.");
        }
        try {
            return recipeDA.getIngredient(recipeName);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the ingredients.", e);
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
            recipeDA.createRecipe(recipe, ingredients);
            return recipe;
        } catch (DataBaseException e) {
            throw new BusinessException("Error when creating the recipe.", e);
        }
    }
}
