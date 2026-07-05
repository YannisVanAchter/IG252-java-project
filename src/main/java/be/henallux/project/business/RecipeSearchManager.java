package be.henallux.project.business;

import be.henallux.project.data.RecipeSearchDA;
import be.henallux.project.data.exception.DataBaseException;
import be.henallux.project.business.exception.BusinessException;

import be.henallux.project.model.Recipe;
import be.henallux.project.model.exception.DataValidationException;

import java.util.List;

public class RecipeSearchManager {
    private final RecipeSearchDA recipeSearchDA;

    public RecipeSearchManager() {
        this.recipeSearchDA = RecipeSearchDA.getInstance();
    }

    public List<Recipe> searchRecipes(String name, List<String> products)
            throws BusinessException, DataValidationException {
        try {
            return recipeSearchDA.search(name, products);
        } catch (DataBaseException e) {
            throw new BusinessException("Error occurred while searching for recipes.", e);
        }
    }
}