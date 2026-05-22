package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class PreparationOrderManager {
    public List<Recipe> getAllRecipes() throws DataBaseException {
        RecipeData data = new RecipeData();
        return data.getAllRecipe();
    }

    public Recipe getRecipe(String recipeName) throws DataBaseException {
        RecipeData data = new RecipeData();
        return data.getRecipe(recipeName);
    }

    public List<Pair<Product, Integer>> getIngredient(String recipeName) throws DataBaseException {
        RecipeData data = new RecipeData();
        return data.getIngredient(recipeName);
    }

    public void createRecipe(Recipe recipe, List<Pair<Product, Integer>> ingredients) throws DataBaseException {
        RecipeData data = new RecipeData();
        data.createRecipe(recipe, ingredients);
    }
}
