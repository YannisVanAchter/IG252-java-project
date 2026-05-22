package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;

public class PreparationOrderManager extends DocumentManager {

    private final RecipeData recipeData;

    public PreparationOrderManager(RecipeData recipeData, DocumentData documentData) {
        super(documentData);
        this.recipeData = recipeData;
    }

    public List<Recipe> getAllRecipes() throws DataBaseException {
        return recipeData.getAllRecipes();
    }

    public Recipe getRecipe(String recipeName) throws DataBaseException {
        return recipeData.getRecipe(recipeName);
    }

    public List<Pair<Product, Integer>> getIngredient(String recipeName) throws DataBaseException {
        return recipeData.getIngredient(recipeName);
    }

    public void createRecipe(Recipe recipe, List<Pair<Product, Integer>> ingredients) throws DataBaseException {
        recipeData.createRecipe(recipe, ingredients);
    }
}
