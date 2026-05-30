package main.java.be.henallux.project.data;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.java.be.henallux.project.data.MySQLConnector;
import main.java.be.henallux.project.data.exception.DataBaseException;

import main.java.be.henallux.project.model.Recipe;

public class RecipeSearchDA {
    private CRUD<Recipe> recipeDA;

    RecipeSearchDA() {
        recipeDA = RecipeDA.getInstance();
    }

    public List<Recipe> search(String nom, String productName) throws DataBaseException, DataValidationException {
        List<Recipe> recipes = new ArrayList();
        StringBuilder SQLInstruction = new StringBuilder("""
                    SELECT Recipe.id_ as recipeID
                    FROM Recipe, RecipeComposition AS Composition, Product 
                    WHERE Recipe.id_ = Composition.recipeId AND Composition.productId = Product.id_
                    """);
        if (nom != null && !nom.isEmpty()) {
            SQLInstruction.add(" AND Recipe.name_=?");
        }
        if (productName != null && !productName.isEmpty()) {
            SQLInstruction.add(" AND Product.name_=?");
        }

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
            Statement statement = connection.prepareStatement(SQLInstruction.toString() + ";");
            int currentIndex = 1;
            if (nom != null && !nom.isEmpty()) {
                statement.setString(currentIndex, nom);
                currentIndex++;
            }
            if (productName != null && !productName.isEmpty()) {
                statement.setInt(currentIndex, productName);
                currentIndex++;
            }

            ResultSet result = statement.executeQuerry();
            while  (result.next()) {
                recipes.add(recipeDA.getById(result.getInt("recipeID")));
            }
            return recipes;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }
}