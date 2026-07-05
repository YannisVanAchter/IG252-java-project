package be.henallux.project.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import be.henallux.project.data.exception.DataBaseException;
import be.henallux.project.model.Recipe;
import be.henallux.project.model.exception.DataValidationException;

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

    public List<Recipe> search(String nom, List<String> ingredients) throws DataBaseException, DataValidationException {
        List<Recipe> recipes = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT DISTINCT r.id_ AS recipeID FROM Recipe r");

        // JOIN sur les ingrédients seulement si on filtre par ingrédient
        if (ingredients != null && !ingredients.isEmpty()) {
            for (int i = 0; i < ingredients.size(); i++) {
                sql.append(" JOIN RecipeComposition rc").append(i)
                        .append(" ON r.id_ = rc").append(i).append(".recipeId")
                        .append(" JOIN Product p").append(i)
                        .append(" ON rc").append(i).append(".productId = p").append(i).append(".id_");
            }
        }

        sql.append(" WHERE 1=1");

        if (nom != null && !nom.isEmpty()) {
            sql.append(" AND r.name_ LIKE ?");
        }
        if (ingredients != null && !ingredients.isEmpty()) {
            for (int i = 0; i < ingredients.size(); i++) {
                sql.append(" AND p").append(i).append(".name_ LIKE ?");
            }
        }

        try (
                Connection connection = MySQLConnector.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql + ";")
        ) {
            int idx = 1;
            if (nom != null && !nom.isEmpty()) {
                statement.setString(idx++, "%" + nom + "%");
            }
            if (ingredients != null && !ingredients.isEmpty()) {
                for (String ing : ingredients) {
                    statement.setString(idx++, "%" + ing + "%");
                }
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