package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.*;
import java.util.List;

public class PreparationOrderManager extends DocumentManager {

    private final RecipeData recipeData;

    public PreparationOrderManager(RecipeData recipeData, DocumentData documentData) {
        super(documentData);
        this.recipeData = recipeData;
    }

    public List<Recipe> getAllRecipes() throws BusinessException {
        try {
            return recipeData.getAllRecipes();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des recettes.", e);
        }
    }

    public Recipe getRecipe(String recipeName) throws BusinessException {
        try {
            return recipeData.getRecipe(recipeName);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération de la recette.", e);
        }
    }

    public List<Pair<Product, Integer>> getIngredient(String recipeName) throws BusinessException {
        try {
            return recipeData.getIngredient(recipeName);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des ingrédients.", e);
        }
    }

    public void createRecipe(Recipe recipe, List<Pair<Product, Integer>> ingredients) throws BusinessException {
        try {
            recipeData.createRecipe(recipe, ingredients);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la création de la recette.", e);
        }
    }
}
