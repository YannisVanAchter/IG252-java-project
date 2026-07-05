package be.henallux.project.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import be.henallux.project.data.exception.DataBaseException;
import be.henallux.project.model.Product;
import be.henallux.project.model.ProductCategory;
import be.henallux.project.model.exception.DataValidationException;

public class ProductSearchDA {

    private CRUD<Product> productDA;

    public ProductSearchDA() {
        productDA = ProductDA.getInstance();
    }

    public List<Product> search(String nom, ProductCategory category, boolean isDiscounted) throws DataBaseException, DataValidationException {
        List<Product> products = new ArrayList<>();
        LocalDate today = LocalDate.now();

        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT Product.id_ as productID " +
                        "FROM Product " +
                        "JOIN ProductCategory AS Category ON Product.categoryId = Category.id_ "
        );

        if (isDiscounted) {
            sql.append(
                    "JOIN Discount ON Product.id_ = Discount.productId " +
                            "AND Discount.startDate <= ? AND ? <= Discount.endDate "
            );
        }

        boolean hasWhere = false;

        if (nom != null && !nom.isEmpty()) {
            sql.append(hasWhere ? " AND" : " WHERE");
            sql.append(" Product.name_ LIKE ?");
            hasWhere = true;
        }
        if (category != null) {
            sql.append(hasWhere ? " AND" : " WHERE");
            sql.append(" Category.id_ = ?");
            hasWhere = true;
        }

        try (Connection connection = MySQLConnector.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            int currentIndex = 1;

            if (isDiscounted) {
                statement.setDate(currentIndex++, Date.valueOf(today));
                statement.setDate(currentIndex++, Date.valueOf(today));
            }
            if (nom != null && !nom.isEmpty()) {
                statement.setString(currentIndex++, "%" + nom + "%");
            }
            if (category != null) {
                statement.setInt(currentIndex++, category.getId());
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