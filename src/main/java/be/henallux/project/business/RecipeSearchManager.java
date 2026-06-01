package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.RecipeSearchDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;

import main.java.be.henallux.project.model.Recipe;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.util.ArrayList;
import java.util.List;

public class RecipeSearchManager {
    private final RecipeSearchDA recipeSearchDA;

    public RecipeSearchManager() {
        this.recipeSearchDA = RecipeSearchDA.getInstance();
    }

    public List<Recipe> searchRecipes(String name, List<String> products) throws BusinessException, DataValidationException {
        try {
            List<Recipe> recipes = new ArrayList<>();
            for (String product : products) {
                recipes.addAll(recipeSearchDA.search(name, product));
            }
            return recipes.stream().distinct().toList();
        } catch (DataBaseException e) {
            throw new BusinessException("Error occurred while searching for recipes.", e);
        }
    }
}
