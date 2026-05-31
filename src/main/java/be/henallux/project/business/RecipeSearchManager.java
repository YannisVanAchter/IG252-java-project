package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.RecipeSearchDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;

import main.java.be.henallux.project.model.Recipe;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.RecipeComposition;
import main.java.be.henallux.project.model.QuantityProduct;
import main.java.be.henallux.project.model.Discount;

import java.util.ArrayList;
import java.util.List;

public class RecipeSearchManager {
    private final RecipeSearchDA recipeSearchDA;

    public RecipeSearchManager() {
        this.recipeSearchDA = RecipeSearchDA.getInstance();
    }
    
    public List<Recipe> searchRecipes(String name, List<String> products) throws BusinessException {
        try {
            List<Recipe> recipes = new ArrayList<>();
            List<Recipe> cleanRecipes = new ArrayList<>();
            for (String product : products) {
                recipes.add(recipeSearchDA.search(name, product));
            }
            cleanRecipes = recipes.stream().distinct().toList();
            return cleanRecipes;
        } catch (DataBaseException e) {
            throw new BusinessException("Error occurred while searching for recipes.", e);
        }
    }
}
