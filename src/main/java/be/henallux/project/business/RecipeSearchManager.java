package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.*;
import java.util.List;

public class RecipeSearchManager {
    private String name;
    private String product;

    public RecipeSearchManager(String name, String product) {
        this.name = name;
        this.product = product;
    }

    public List<Recipe> searchRecipes() throws BusinessException {
        RecipeData recipeData = new RecipeData();
        try {
            return recipeData.searchRecipes(name, product);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la recherche de recettes.", e);
        }
    }
}
