package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.RecipeDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;

import main.java.be.henallux.project.model.Recipe;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.RecipeComposition;
import main.java.be.henallux.project.model.QuantityProduct;
import main.java.be.henallux.project.model.Discount;
import java.util.List;

public class RecipeSearchManager {
    private final RecipeDA recipeDA;

    public RecipeSearchManager() {
        this.recipeDA = RecipeDA.getInstance();
    }

    public List<Recipe> searchRecipes(String name, String product) throws BusinessException {
        try {
            return recipeDA.searchRecipes(name, product);
        } catch (DataBaseException e) {
            throw new BusinessException("Error occurred while searching for recipes.", e);
        }
    }
}
