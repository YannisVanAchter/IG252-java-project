package main.java.be.henallux.project.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.be.henallux.project.data.exception.DataBaseException;

import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.Recipe;
import main.java.be.henallux.project.model.RecipeComposition;

import main.java.be.henallux.project.model.exception.DataValidationException;

public class RecipeCompositionDA extends CRUD<RecipeComposition> {

    private static RecipeCompositionDA instance;

    private final ProductDA productDA;
    private final RecipeDA recipeDA;

    private RecipeCompositionDA() {
        this.TABLE_NAME = "RecipeComposition";
        this.IDS_MAPPING_OBJECT = new HashMap<>();

        this.productDA = ProductDA.getInstance();
        this.recipeDA = RecipeDA.getInstance();
    }

    public static RecipeCompositionDA getInstance() {
        synchronized (RecipeCompositionDA.class) {
            if (instance == null) {
                instance = new RecipeCompositionDA();
            }
        }
        return instance;
    }

    @Override
    RecipeComposition mapDataToObject(ResultSet data, boolean mapping)
            throws DataBaseException, DataValidationException {

        try {
            int recipeId = data.getInt("recipeId");
            int productId = data.getInt("productId");

            String key = recipeId + "-" + productId;

            if (mapping && IDS_MAPPING_OBJECT.containsKey(key)) {
                return IDS_MAPPING_OBJECT.get(key);
            }

            int quantity = data.getInt("quantity");

            Product product = productDA.getById(productId, mapping);
            Recipe recipe = recipeDA.getById(recipeId, mapping);

            RecipeComposition recipeComposition =
                    new RecipeComposition(quantity, product, recipe);
            if (mapping)
                recipe.addProductInComposition(recipeComposition);

            IDS_MAPPING_OBJECT.put(key, recipeComposition);
            return recipeComposition;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while mapping RecipeComposition data: " + e.getMessage()
            );
        }
    }

    @Override
    public ArrayList<RecipeComposition> getAll()
            throws DataBaseException, DataValidationException {

        ArrayList<RecipeComposition> recipeCompositions = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME;

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query);

                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                recipeCompositions.add(
                        mapDataToObject(resultSet, true)
                );
            }

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while getting all RecipeComposition: " + e.getMessage()
            );
        }

        return recipeCompositions;
    }

    @Override
    public RecipeComposition getById(int id, boolean mapping)
            throws DataBaseException, DataValidationException {

        throw new DataBaseException(
                "RecipeComposition uses composite key (recipeId, productId)"
        );
    }

    /**
     * Get RecipeComposition by composite key
     */
    public RecipeComposition getByIds(
            int recipeId,
            int productId,
            boolean mapping
    ) throws DataBaseException, DataValidationException {

        String query =
                "SELECT * FROM " + TABLE_NAME +
                        " WHERE recipeId = ? AND productId = ?";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, recipeId);
            statement.setInt(2, productId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapDataToObject(resultSet, mapping);
                }

            }

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while getting RecipeComposition: " + e.getMessage()
            );
        }

        return null;
    }

    public RecipeComposition getByIds(
            int recipeId,
            int productId
    ) throws DataBaseException, DataValidationException {

        return getByIds(recipeId, productId, true);
    }

    @Override
    public List<RecipeComposition> getsByIds(
            List<Integer> ids,
            boolean mapping
    ) throws DataBaseException, DataValidationException {

        throw new DataBaseException(
                "RecipeComposition uses composite primary key"
        );
    }

    /**
     * Get all RecipeComposition for a recipe
     */
    public ArrayList<RecipeComposition> getByRecipe(Recipe recipe)
            throws DataBaseException, DataValidationException {

        ArrayList<RecipeComposition> recipeCompositions = new ArrayList<>();

        String query =
                "SELECT * FROM " + TABLE_NAME +
                        " WHERE recipeId = ?";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, recipe.getId());

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    recipeCompositions.add(
                            mapDataToObject(resultSet, true)
                    );
                }

            }

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while getting RecipeComposition by recipe: "
                            + e.getMessage()
            );
        }

        return recipeCompositions;
    }

    @Override
    public boolean insert(RecipeComposition recipeComposition)
            throws DataBaseException, DataValidationException {

        productDA.checkExist(recipeComposition.getProduct());
        recipeDA.checkExist(recipeComposition.getRecipe());

        String query =
                "INSERT INTO " + TABLE_NAME +
                        " (recipeId, productId, quantity) VALUES (?, ?, ?)";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(
                    1,
                    recipeComposition.getRecipe().getId()
            );

            statement.setInt(
                    2,
                    recipeComposition.getProduct().getId()
            );

            statement.setInt(
                    3,
                    recipeComposition.getQuantity()
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while inserting RecipeComposition: "
                            + e.getMessage()
            );
        }
    }

    @Override
    public boolean update(
            RecipeComposition recipeComposition,
            RecipeComposition newRecipeComposition
    ) throws DataBaseException, DataValidationException {

        String query =
                "UPDATE " + TABLE_NAME +
                        " SET quantity = ?, recipeId = ?, productId = ? " +
                        "WHERE recipeId = ? AND productId = ?";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(
                    1,
                    newRecipeComposition.getQuantity()
            );

            statement.setInt(
                    2,
                    newRecipeComposition.getRecipe().getId()
            );

            statement.setInt(
                    3,
                    newRecipeComposition.getProduct().getId()
            );

            statement.setInt(
                    4,
                    recipeComposition.getRecipe().getId()
            );

            statement.setInt(
                    5,
                    recipeComposition.getProduct().getId()
            );

            boolean success = statement.executeUpdate() > 0;

            if (success) {
                String oldKey =
                        recipeComposition.getRecipe().getId()
                                + "-"
                                + recipeComposition.getProduct().getId();

                IDS_MAPPING_OBJECT.remove(oldKey);

                String newKey =
                        newRecipeComposition.getRecipe().getId()
                                + "-"
                                + newRecipeComposition.getProduct().getId();

                IDS_MAPPING_OBJECT.put(
                        newKey,
                        newRecipeComposition
                );
            }

            return success;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while updating RecipeComposition: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Update quantity field
     */
    public boolean updateFieldQuantity(
            RecipeComposition recipeComposition,
            int quantity
    ) throws DataBaseException, DataValidationException {

        String query =
                "UPDATE " + TABLE_NAME +
                        " SET quantity = ? " +
                        "WHERE recipeId = ? AND productId = ?";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, quantity);

            statement.setInt(
                    2,
                    recipeComposition.getRecipe().getId()
            );

            statement.setInt(
                    3,
                    recipeComposition.getProduct().getId()
            );

            boolean success = statement.executeUpdate() > 0;

            if (success) {
                recipeComposition.setQuantity(quantity);
            }

            return success;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while updating quantity: " + e.getMessage()
            );
        }
    }

    /**
     * Update recipe field
     */
    public boolean updateFieldRecipe(
            RecipeComposition recipeComposition,
            Recipe recipe
    ) throws DataBaseException, DataValidationException {

        String query =
                "UPDATE " + TABLE_NAME +
                        " SET recipeId = ? " +
                        "WHERE recipeId = ? AND productId = ?";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, recipe.getId());

            statement.setInt(
                    2,
                    recipeComposition.getRecipe().getId()
            );

            statement.setInt(
                    3,
                    recipeComposition.getProduct().getId()
            );

            boolean success = statement.executeUpdate() > 0;

            if (success) {
                recipeComposition.setRecipe(recipe);
            }

            return success;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while updating recipe: " + e.getMessage()
            );
        }
    }

    @Override
    public boolean delete(RecipeComposition recipeComposition)
            throws DataBaseException {

        String query =
                "DELETE FROM " + TABLE_NAME +
                        " WHERE recipeId = ? AND productId = ?";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(
                    1,
                    recipeComposition.getRecipe().getId()
            );

            statement.setInt(
                    2,
                    recipeComposition.getProduct().getId()
            );

            boolean success = statement.executeUpdate() > 0;

            if (success) {
                String key =
                        recipeComposition.getRecipe().getId()
                                + "-"
                                + recipeComposition.getProduct().getId();

                IDS_MAPPING_OBJECT.remove(key);
            }

            return success;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while deleting RecipeComposition: "
                            + e.getMessage()
            );
        }
    }

    @Override
    public boolean checkExist(RecipeComposition recipeComposition)
            throws DataBaseException, DataValidationException {

        return getByIds(
                recipeComposition.getRecipe().getId(),
                recipeComposition.getProduct().getId(),
                false
        ) != null;
    }
}