package be.henallux.project.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import be.henallux.project.data.exception.DataBaseException;

import be.henallux.project.model.Product;
import be.henallux.project.model.Recipe;
import be.henallux.project.model.RecipeComposition;

import be.henallux.project.model.exception.DataValidationException;

public class RecipeDA extends CRUD<Recipe> {

    private static volatile RecipeDA instance;
    private CRUD<RecipeComposition> compositionDA;

    private RecipeDA() {
        TABLE_NAME = "Recipe";
        IDS_MAPPING_OBJECT = new HashMap<>();
        // FIX: compositionDA is no longer fetched here
    }

    public static RecipeDA getInstance() {
        synchronized (RecipeDA.class) {
            if (instance == null)
                instance = new RecipeDA();
        }
        return instance;
    }

    private CRUD<RecipeComposition> getCompositionDA() {
        if (compositionDA == null)
            compositionDA = RecipeCompositionDA.getInstance();
        return compositionDA;
    }

    @Override
    Recipe mapDataToObject(ResultSet data, boolean mapping)
            throws DataBaseException, DataValidationException {

        try {
            int id = data.getInt("id_");

            if (IDS_MAPPING_OBJECT.containsKey(id))
                return IDS_MAPPING_OBJECT.get(id);

            String name = data.getString("name_");
            String instruction = data.getString("instructions");
            int finalProductId = data.getInt("finalProductId");

            Product finalProduct = ProductDA.getInstance().getById(finalProductId, mapping);

            Recipe recipe = new Recipe(id, name, instruction, finalProduct, null);

            IDS_MAPPING_OBJECT.put(id, recipe);

            if (mapping)
                getCompositionDA().getAll();

            return recipe;

        } catch (Exception e) {
            throw new DataBaseException("Error while mapping Recipe data : " + e.getMessage());
        }
    }

    @Override
    public List<Recipe> getAll() throws DataBaseException, DataValidationException {

        List<Recipe> recipes = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + ";";

        try (
                Connection connection = connector.getConnection();
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(query)
        ) {
            while (result.next())
                recipes.add(mapDataToObject(result, true));

            return recipes;

        } catch (Exception e) {
            throw new DataBaseException("Error while getting all recipes : " + e.getMessage());
        }
    }

    @Override
    public Recipe getById(int id, boolean mapping)
            throws DataBaseException, DataValidationException {

        if (IDS_MAPPING_OBJECT.containsKey(id))
            return IDS_MAPPING_OBJECT.get(id);

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE id_ = ?;";

        try (
                Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next())
                    return mapDataToObject(result, mapping);
            }

            return null;

        } catch (Exception e) {
            throw new DataBaseException("Error while getting recipe by id : " + e.getMessage());
        }
    }

    @Override
    public List<Recipe> getsByIds(List<Integer> ids, boolean mapping)
            throws DataBaseException, DataValidationException {

        List<Recipe> recipes = new ArrayList<>();

        if (ids == null || ids.isEmpty())
            return recipes;

        List<Integer> uncachedIds = new ArrayList<>();
        for (Integer id : ids) {
            Recipe cached = IDS_MAPPING_OBJECT.get(id);
            if (cached != null)
                recipes.add(cached);
            else
                uncachedIds.add(id);
        }

        if (uncachedIds.isEmpty())
            return recipes;

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM " + TABLE_NAME + " WHERE id_ IN (");
        for (int i = 0; i < uncachedIds.size(); i++)
            sql.append(i > 0 ? ",?" : "?");
        sql.append(")");

        try (
                Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString())
        ) {
            for (int i = 0; i < uncachedIds.size(); i++)
                statement.setInt(i + 1, uncachedIds.get(i));

            try (ResultSet result = statement.executeQuery()) {
                while (result.next())
                    recipes.add(mapDataToObject(result, mapping));
            }

        } catch (Exception e) {
            throw new DataBaseException("Error while getting recipes by ids : " + e.getMessage());
        }

        return recipes;
    }

    @Override
    public boolean insert(Recipe recipe)
            throws DataBaseException, DataValidationException {

        ProductDA.getInstance().checkExist(recipe.getFinalProduct());

        String query = String.format("""
            INSERT INTO %s (name_, instructions, finalProductId)
            VALUES (?, ?, ?)
        """, TABLE_NAME);

        try (
                Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        query, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, recipe.getName());
            statement.setString(2, recipe.getInstruction());
            statement.setInt(3, recipe.getFinalProduct().getId());

            if (statement.executeUpdate() == 0)
                return false;

            // FIX: ResultSet is now closed via try-with-resources to prevent a resource leak
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next())
                    recipe.setId(generatedKeys.getInt(1));
            }

            IDS_MAPPING_OBJECT.put(recipe.getId(), recipe);

            for (RecipeComposition composition : recipe.getComposition())
                getCompositionDA().insert(composition);

            return true;

        } catch (Exception e) {
            throw new DataBaseException("Error while inserting recipe : " + e.getMessage());
        }
    }

    @Override
    public boolean update(Recipe recipe, Recipe newRecipe)
            throws DataBaseException, DataValidationException {

        String query = String.format("""
            UPDATE %s
            SET name_ = ?, instructions = ?, finalProductId = ?
            WHERE id_ = ?
        """, TABLE_NAME);

        try (
                Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, newRecipe.getName());
            statement.setString(2, newRecipe.getInstruction());
            statement.setInt(3, newRecipe.getFinalProduct().getId());
            statement.setInt(4, recipe.getId());

            if (statement.executeUpdate() > 0) {
                IDS_MAPPING_OBJECT.remove(recipe.getId());
                IDS_MAPPING_OBJECT.put(newRecipe.getId(), newRecipe);
                return true;
            }

            return false;

        } catch (Exception e) {
            throw new DataBaseException("Error while updating recipe : " + e.getMessage());
        }
    }

    @Override
    public boolean delete(Recipe recipe)
            throws DataBaseException, DataValidationException {

        for (RecipeComposition composition : recipe.getComposition())
            RecipeCompositionDA.getInstance().delete(composition);

        String query = "DELETE FROM " + TABLE_NAME + " WHERE id_ = ?";

        try (
                Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setInt(1, recipe.getId());

            if (statement.executeUpdate() > 0) {
                IDS_MAPPING_OBJECT.remove(recipe.getId());
                return true;
            }

            return false;

        } catch (Exception e) {
            throw new DataBaseException(
                    "Error while deleting recipe : " + e.getMessage()
            );
        }
    }

    @Override
    public boolean checkExist(Recipe recipe)
            throws DataBaseException, DataValidationException {

        String query = String.format("""
            SELECT id_
            FROM %s
            WHERE id_ = ?
               OR name_ = ?
        """, TABLE_NAME);

        try (
                Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setInt(1, recipe.getId());
            statement.setString(2, recipe.getName());

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }

        } catch (Exception e) {
            throw new DataBaseException(
                    "Error while checking recipe existence : " + e.getMessage());
        }
    }

    /*
     * ===========================
     * UPDATE FIELD METHODS
     * ===========================
     */

    public boolean updateName(Recipe recipe, String newName)
            throws DataBaseException {

        String query = String.format("""
            UPDATE %s
            SET name_ = ?
            WHERE id_ = ?
        """, TABLE_NAME);

        try (
                Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, newName);
            statement.setInt(2, recipe.getId());
            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            throw new DataBaseException(
                    "Error while updating recipe name : "
                            + e.getMessage()
            );
        }
    }

    public boolean updateInstruction(Recipe recipe, String newInstruction)
            throws DataBaseException {

        String query = String.format("""
            UPDATE %s
            SET instructions = ?
            WHERE id_ = ?
        """, TABLE_NAME);

        try (
                Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, newInstruction);
            statement.setInt(2, recipe.getId());
            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            throw new DataBaseException(
                    "Error while updating recipe instruction : "
                            + e.getMessage()
            );
        }
    }

    public boolean updateFinalProduct(Recipe recipe, Product newProduct)
            throws DataBaseException {

        String query = String.format("""
            UPDATE %s
            SET finalProductId = ?
            WHERE id_ = ?
        """, TABLE_NAME);

        try (
                Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {

            statement.setInt(1, newProduct.getId());
            statement.setInt(2, recipe.getId());

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            throw new DataBaseException(
                    "Error while updating recipe final product : "
                            + e.getMessage()
            );
        }
    }
}