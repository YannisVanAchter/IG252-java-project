package main.java.be.henallux.project.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.data.CRUD;
import main.java.be.henallux.project.model.ProductCategory;
import main.java.be.henallux.project.model.exception.DataValidationException;

public class ProductCategoryDA extends CRUD<ProductCategory> {
    private static volatile ProductCategoryDA instance;
    private final String TABLE_NAME = "ProductCategory";
    private final Map<Integer, ProductCategory> dataMappingObject;

    private ProductCategoryDA() {
        super();
        this.dataMappingObject = new HashMap<>();
    }

    @SuppressWarnings("DoubleCheckedLocking")
    public static ProductCategoryDA getInstance() {
        if (instance == null) {
            synchronized (ProductCategoryDA.class) {
                if (instance == null) {
                    setInstance(new ProductCategoryDA());
                }
            }
        }

        return instance;
    }

    @SuppressWarnings("DoubleCheckedLocking")
    private static void setInstance(ProductCategoryDA productCategoryDA) {
        if (instance == null) {
            synchronized (ProductCategoryDA.class) {
                if (instance == null) {
                    instance = productCategoryDA;
                }
            }
        }
    }

    @Override
    public ProductCategory mapDataToObject(ResultSet data, boolean mapping) throws DataBaseException, DataValidationException {
        int id;

        try {
            id = data.getInt("id_");

            if (dataMappingObject.get(id) == null) {
                ProductCategory productCategory = new ProductCategory(
                        id,
                        data.getString("name_")
                );

                dataMappingObject.put(id, productCategory);
            }

        } catch (SQLException e) {
            throw new DataBaseException("Error mapping ProductCategory", e);
        }

        return dataMappingObject.get(id);
    }

    @Override
    public List<ProductCategory> getAll() throws DataBaseException, DataValidationException {
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " ORDER BY name_";
        List<ProductCategory> productCategories = new ArrayList<>();

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                productCategories.add(mapDataToObject(resultSet, true));
            }

        } catch (SQLException e) {
            throw new DataBaseException("Error getting all product categories", e);
        }

        return productCategories;
    }

    @Override
    public List<ProductCategory> getsByIds(List<Integer> ids, boolean mapping) throws DataBaseException, DataValidationException {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<ProductCategory> productCategories = new ArrayList<>();

        for (int id : ids) {
            ProductCategory productCategory = dataMappingObject.get(id);

            if (productCategory != null) {
                productCategories.add(productCategory);
            }
        }

        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " WHERE id_ IN (" + placeholders + ") ORDER BY name_";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            for (int i = 0; i < ids.size(); i++) {
                statement.setInt(i + 1, ids.get(i));
            }

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                productCategories.add(mapDataToObject(resultSet, mapping));
            }

        } catch (SQLException e) {
            throw new DataBaseException("Error getting product categories by ids", e);
        }

        return productCategories;
    }

    @Override
    public List<ProductCategory> getsByIds(List<Integer> ids) throws DataBaseException, DataValidationException {
        return getsByIds(ids, true);
    }

    @Override
    public ProductCategory getById(int id, boolean mapping) throws DataBaseException, DataValidationException {
        ProductCategory productCategory = dataMappingObject.get(id);

        if (productCategory != null) {
            return productCategory;
        }

        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " WHERE id_=?;";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);
            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                productCategory = mapDataToObject(result, mapping);
            }

        } catch (SQLException e) {
            throw new DataBaseException("SQL Exception", e);
        }

        return productCategory;
    }

    @Override
    public ProductCategory getById(int id) throws DataBaseException, DataValidationException {
        return getById(id, true);
    }

    @Override
    public boolean insert(ProductCategory newProductCategory) throws DataBaseException, DataValidationException {
        boolean inserted = false;

        String SQLInstruction = "INSERT INTO " + TABLE_NAME + " (name_) VALUES (?);";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, newProductCategory.getName());

            inserted = 0 < statement.executeUpdate();

            SQLInstruction = "SELECT id_ FROM " + TABLE_NAME + " WHERE name_=?;";
            statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, newProductCategory.getName());

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                try {
                    newProductCategory.setId(result.getInt("id_"));
                } catch (DataValidationException e) {
                    throw new DataValidationException("Update ID impossible", e);
                }
                dataMappingObject.put(newProductCategory.getId(), newProductCategory);
            }

        } catch (SQLException e) {
            throw new DataBaseException("Insert product category impossible", e);
        }

        return inserted;
    }

    @Override
    public boolean update(ProductCategory productCategory, ProductCategory newProductCategory) throws DataBaseException {
        String SQLInstruction = "UPDATE " + TABLE_NAME + " SET name_=? WHERE id_=?;";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, newProductCategory.getName());
            statement.setInt(2, productCategory.getId());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(productCategory);
            dataMappingObject.put(newProductCategory.getId(), newProductCategory);

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Update impossible", e);
        }
    }

    @Override
    public boolean delete(ProductCategory productCategory) throws DataBaseException {
        String SQLInstruction = "DELETE FROM " + TABLE_NAME + " WHERE id_=?;";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setInt(1, productCategory.getId());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(productCategory.getId());

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Delete impossible", e);
        }
    }

    @Override
    public boolean checkExist(ProductCategory productCategory) throws DataBaseException, DataValidationException {
        boolean exist = false;

        if (productCategory != null) {
            String SQLInstruction = "SELECT COUNT(*) as nbProductCategory FROM " + TABLE_NAME + " WHERE id_=?;";

            try (Connection connection = connector.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(SQLInstruction);

                statement.setInt(1, productCategory.getId());

                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    exist = result.getInt("nbProductCategory") > 0;
                }

                if (!exist) {
                    try {
                        insert(productCategory);
                    } catch (DataValidationException e) {
                        throw e;
                    }
                    exist = true;
                }

            } catch (SQLException e) {
                throw new DataBaseException("Check impossible", e);
            }
        }

        return exist;
    }
}
