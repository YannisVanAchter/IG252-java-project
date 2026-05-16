package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exception.DataValidationException;

public class Recipe {
    private int id;
    private String name;
    private String instruction;
    private Product finalProduct;
    private List<RecipeComposition> composition;

    public Recipe(int id, String name, String instruction, Product finalProduct, List<RecipeComposition> composition) throws DataValidationException {
        setId(id);
        setName(name);
        setInstruction(instruction);
        setFinalProduct(finalProduct);
        setComposition(composition);
    }

    public int getId() {
        return id;
    }

    private void setId(int id) throws DataValidationException {
        if (id < 0)
            throw new DataValidationException("Id cannot be negative");
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) throws DataValidationException {
        if (name == null || name.isEmpty())
            throw new DataValidationException("Name cannot be null or empty");
        this.name = name;
    }

    public String getInstruction() {
        return instruction;
    }

    private void setInstruction(String instruction) throws DataValidationException {
        if (instruction == null || instruction.isEmpty())
            throw new DataValidationException("Instruction cannot be empty");
        this.instruction = instruction;
    }

    public Product getFinalProduct() {
        return finalProduct;
    }

    private void setFinalProduct(Product finalProduct) throws DataValidationException {
        if (finalProduct == null)
            throw new DataValidationException("The final product cannot be null");
        this.finalProduct = finalProduct;
    }

    public List<RecipeComposition> getComposition() {
        return Collections.unmodifiable(composition);
    }

    private void setComposition(List<RecipeComposition> composition) {
        if (composition == null)
            this.composition = new ArrayList<>();
        else
            this.composition = composition;
    }

    public void addProductInComposition(RecipeComposition ingredient) throws DataValidationException {
        if (!composition.contains(ingredient))
            composition.add(ingredient);
    }

    public void addProductInComposition(Product ingredient, int quantity) throws DataValidationException {
        RecipeComposition c = new RecipeComposition(quantity, ingredient, this);
        if (!composition.contains(c))
            composition.add(c);
    }

    public String getLabel() { return name; }

    @Override
    public String toString() {
        return String.format("Recipe{id=%d, name=%s, instruction=%s, finalProduct=%s, composition=%s}",
            id,
            name,
            instruction,
            finalProduct.toString(),
            composition.toString()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Recipe other = (Recipe) obj;
        return  id == other.getId() && name.equals(other.getName()) &&
                instruction.equals(other.getInstruction()) &&
                finalProduct.equals(other.getFinalProduct());
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(id);
        result = 31 * result + name.hashCode();
        result = 31 * result + instruction.hashCode();
        result = 31 * result + finalProduct.hashCode();
        result = 31 * result + composition.hashCode();
        return result;
    }
}