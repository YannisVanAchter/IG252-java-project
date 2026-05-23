package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.*;
import java.util.List;

public class PreparationOrderManager extends DocumentManager {

private final RecipeDA recipeDA;

    public PreparationOrderManager(RecipeDA recipeDA, DocumentDA documentDA, ProductManager productManager, StockManager stockManager) {
        super(documentDA, productManager, stockManager);
        this.recipeDA = recipeDA;
    }

    public List<Recipe> getAllRecipes() throws BusinessException {
        try {
            return recipeDA.getAllRecipes();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des recettes.", e);
        }
    }

    public Recipe getRecipe(String recipeName) throws BusinessException {
        if (recipeName == null || recipeName.isBlank()) {
            throw new BusinessException("Le nom de la recette ne peut pas être vide.");
        }
        try {
            return recipeDA.getRecipe(recipeName);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération de la recette.", e);
        }
    }

    public List<Pair<Product, Integer>> getIngredient(String recipeName) throws BusinessException {
        if (recipeName == null || recipeName.isBlank()) {
            throw new BusinessException("Le nom de la recette ne peut pas être vide.");
        }
        try {
            return recipeDA.getIngredient(recipeName);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des ingrédients.", e);
        }
    }

    public void createRecipe(Recipe recipe, List<Pair<Product, Integer>> ingredients) throws BusinessException {
        if (recipe == null) {
            throw new BusinessException("La recette ne peut pas être nulle.");
        }
        if (ingredients == null || ingredients.isEmpty()) {
            throw new BusinessException("Les ingrédients ne peuvent pas être vides.");
        }
        try {
            recipeDA.createRecipe(recipe, ingredients);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la création de la recette.", e);
        }
    }
}
