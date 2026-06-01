package main.java.be.henallux.project.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.Recipe;
import main.java.be.henallux.project.model.exception.DataValidationException;

public class RecipeSearchDA {

    private static volatile RecipeSearchDA instance;
    private final CRUD<Recipe> recipeDA;

    private RecipeSearchDA() {
        recipeDA = RecipeDA.getInstance();
    }

    public static RecipeSearchDA getInstance() {
        synchronized (RecipeSearchDA.class) {
            if (instance == null)
                instance = new RecipeSearchDA();
        }
        return instance;
    }

    public List<Recipe> search(String nom, String productName) throws DataBaseException, DataValidationException {
        List<Recipe> recipes = new ArrayList<>();
        StringBuilder SQLInstruction = new StringBuilder("""
                SELECT Recipe.id_ as recipeID
                FROM Recipe, RecipeComposition AS Composition, Product
                WHERE Recipe.id_ = Composition.recipeId AND Composition.productId = Product.id_
                """);

        if (nom != null && !nom.isEmpty()) {
            SQLInstruction.append(" AND Recipe.name_ = ?");
        }
        if (productName != null && !productName.isEmpty()) {
            SQLInstruction.append(" AND Product.name_ = ?");
        }

        try (
                Connection connection = MySQLConnector.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(SQLInstruction + ";")
        ) {
            int currentIndex = 1;
            if (nom != null && !nom.isEmpty()) {
                statement.setString(currentIndex, nom);
                currentIndex++;
            }
            if (productName != null && !productName.isEmpty()) {
                statement.setString(currentIndex, productName);
            }

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                recipes.add(recipeDA.getById(result.getInt("recipeID"), true));
            }
            return recipes;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }
}