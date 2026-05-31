package main.java.be.henallux.project.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.ProductCategory;
import main.java.be.henallux.project.model.exception.DataValidationException;

public class ProductSearchDA {

    private CRUD<Product> productDA;

    public ProductSearchDA() {
        productDA = ProductDA.getInstance();
    }

    public List<Product> search(String nom, ProductCategory category, boolean isDiscounted) throws DataBaseException, DataValidationException {
        List<Product> products = new ArrayList<>();
        StringBuilder SQLInstruction = new StringBuilder("""
                SELECT Product.id_ as productID
                FROM Product, ProductCategory AS Category, Discount
                WHERE Product.id_ = Discount.productId AND Product.categoryId = Category.id_
                """);

        if (nom != null && !nom.isEmpty()) {
            SQLInstruction.append(" AND Product.name=?");
        }
        if (category != null) {
            SQLInstruction.append(" AND Category.id_=?");
        }

        LocalDate today = LocalDate.now();
        if (isDiscounted) {
            SQLInstruction.append("""
                     AND (
                     Discount.startDate <= ? AND
                     ? <= Discount.endDate
                     )""");
        }

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction.toString() + ";");
            int currentIndex = 1;

            if (nom != null && !nom.isEmpty()) {
                statement.setString(currentIndex, nom);
                currentIndex++;
            }
            if (category != null) {
                statement.setInt(currentIndex, category.getId());
                currentIndex++;
            }
            if (isDiscounted) {
                statement.setDate(currentIndex, Date.valueOf(today));
                currentIndex++;
                statement.setDate(currentIndex, Date.valueOf(today));
                currentIndex++;
            }

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                products.add(productDA.getById(result.getInt("productID")));
            }
            return products;

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }
}