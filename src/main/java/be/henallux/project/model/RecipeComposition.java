package be.henallux.project.model;

import be.henallux.project.model.exception.DataValidationException;

public class RecipeComposition implements Model {
    private int quantity;
    private Product product;
    private Recipe recipe;

    public RecipeComposition(int quantity, Product product, Recipe recipe) throws DataValidationException {
        setQuantity(quantity);
        setProduct(product);
        setRecipe(recipe);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) throws DataValidationException {
        if (quantity <= 0)
            throw new DataValidationException("Quantity cannot be negative or equal to zero");
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    private void setProduct(Product product) throws DataValidationException {
        if (product == null)
            throw new DataValidationException("Product cannot be null");
        this.product = product;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) throws DataValidationException {
        if (recipe == null) 
            throw new DataValidationException("Recipe cannot be null");
        this.recipe = recipe;
    }

    public String getLabel() {
        return String.format("%s - %d %s", recipe.getLabel(), quantity, product.getLabel());
    }

    @Override
    public String toString() {
        return String.format("RecipeComposition{quantity=%d, recipe=%s, product=%s}",
            quantity,
            recipe.toString(),
            product.toString()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        RecipeComposition other = (RecipeComposition) obj;
        return  recipe.equals(other.getRecipe()) &&
                product.equals(other.getProduct());
    }

    @Override
    public int hashCode() {
        int result = recipe.hashCode();
        result = 31 * result + product.hashCode();
        return result;
    }
}