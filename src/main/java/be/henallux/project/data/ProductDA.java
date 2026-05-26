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
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.QuantityProduct;
import main.java.be.henallux.project.model.LocationProduct;
import main.java.be.henallux.project.model.Discount;

public class ProductDA extends CRUD<Product> {
    private static volatile ProductDA instance;
    private final String TABLE_NAME = "Product";
    private Map<Integer, Product> dataMappingObject;
    private DiscountDA discountDA;
    private QuantityProductDA quantityDA;
    private LocationProductDA locationDA;
    private ProductCategoryDA categoryDA;

    private ProductDA() {
        super();
        this.dataMappingObject = new HashMap<>();
        discountDA = DiscountDA.getInstance();
        quantityDA = QuantityProductDA.getInstance();
        locationDA = LocationProductDA.getInstance();
        categoryDA = ProductCategoryDA.getInstance();
    }

    @SuppressWarnings("DoubleCheckedLocking")
    public static ProductDA getInstance() {
        synchronized (ProductDA.class){
            if (instance == null) {
                setInstance(new ProductDA());
            }
        }

        return instance;
    }

    @SuppressWarnings("DoubleCheckedLocking")
    private static void setInstance(ProductDA productDA) {
        synchronized (ProductDA.class){
            if (instance == null) {
                instance = productDA;
            }
        }
    }

    @Override
    public Product mapDataToObject(ResultSet data, boolean mapping) throws DataBaseException {
        int id;

        try {
            id = data.getInt("id_");

            if (dataMappingObject.get(id) == null) {

                Product product = new Product(
                        id,
                        data.getString("name_"),
                        data.getBigDecimal("priceEVAT"),
                        data.getBigDecimal("VAT"),
                        data.getInt("loyaltyPoints"),
                        data.getBoolean("isEdible"),
                        data.getInt("minStockQuantity"),
                        categoryDA.getById(data.getInt("categoryId")),
                        null,
                        null
                );

                if (mapping) {
                    discountDA.getAll(); // Discounts set by getting all discounts
                    List<Integer> idsLocation = locationDA.getAll().stream().mapToInt(location -> 
                        QuantityProduct.hashCode(location, product)
                    );

                    product.setLocation(quantityDA.getByIds(idsLocation));
                }

                dataMappingObject.put(id, product);
            }

        } catch (SQLException e) {
            throw new DataBaseException("Error mapping Product", e);
        }

        return dataMappingObject.get(id);
    }

    @Override
    public List<Product> getAll() throws DataBaseException {
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " ORDER BY id_";
        List<Product> products = new ArrayList<>();

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {

            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                products.add(mapDataToObject(resultSet, true));
            }

        } catch (SQLException e) {
            throw new DataBaseException("Error getting all products", e);
        }

        return products;
    }

    @Override
    public List<Product> getsByIds(List<Integer> ids, boolean mapping) throws DataBaseException {
        List<Product> products = new ArrayList<>();

        if (ids == null) {
            return products;
        }

        for (int i = 0; i < ids.size(); i++) {
            int id = ids.get(i);
            Product p = dataMappingObject.get(id);
            if (p != null) {
                products.add(p);
                ids.remove(i);
                i--;
            }
        }

        if (ids.isEmpty()) return products;

        StringBuilder SQLInstruction = new StringBuilder(
                "SELECT * FROM " + TABLE_NAME + " WHERE id_ IN ("
        );

        for (int i = 0; i < ids.size(); i++) {
            SQLInstruction.append("?");

            if (i < ids.size() - 1) {
                SQLInstruction.append(",");
            }
        }

        SQLInstruction.append(");");

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {

            PreparedStatement statement = connection.prepareStatement(SQLInstruction.toString());

            for (int i = 0; i < ids.size(); i++) {
                statement.setInt(i + 1, ids.get(i));
            }

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                products.add(mapDataToObject(resultSet, mapping));
            }

        } catch (SQLException e) {
            throw new DataBaseException("Error getting products by ids", e);
        }

        return products;
    }

    @Override
    public List<Product> getsByIds(List<Integer> ids) throws DataBaseException {
        return getsByIds(ids, true);
    }

    @Override
    public Product getById(int id, boolean mapping) throws DataBaseException {

        Product product = dataMappingObject.get(id);

        if (product != null) {
            return product;
        }

        String SQLInstruction =
                "SELECT * FROM " + TABLE_NAME + " WHERE id_=?;";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {

            PreparedStatement statement = connection.prepareStatement(
                    SQLInstruction
            );

            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                product = mapDataToObject(result, mapping);
            }

        } catch (SQLException e) {
            throw new DataBaseException("SQL Exception", e);
        }

        return product;
    }

    @Override
    public Product getById(int id)
            throws DataBaseException {

        return getById(id, true);
    }

    @Override
    public boolean insert(Product newProduct) throws DataBaseException {

        boolean inserted = false;

        String SQLInstruction =
                "INSERT INTO " + TABLE_NAME +
                " (name_, priceEVAT, VAT, loyaltyPoints, isEdible, minStockQuantity, categoryId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {

            PreparedStatement statement = connection.prepareStatement(
                    SQLInstruction,
                    PreparedStatement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, newProduct.getName());
            statement.setBigDecimal(2, newProduct.getPriceEVAT());
            statement.setBigDecimal(3, newProduct.getVatT());
            statement.setInt(4, newProduct.getFidelityPoint());
            statement.setBoolean(5, newProduct.getIsEdible());
            statement.setInt(6, newProduct.getMinStockQuantity());
            statement.setInt(7, newProduct.getCategory().getId());

            inserted = 0 < statement.executeUpdate();

            if (inserted) {

                ResultSet generatedKeys = statement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);

                    newProduct.setId(generatedId);

                    dataMappingObject.put(generatedId, newProduct);
                }
            }

        } catch (SQLException e) {
            throw new DataBaseException("Insert product impossible", e);
        }

        return inserted;
    }

    @Override
    public boolean update(Product product, Product newProduct) throws DataBaseException {

        String SQLInstruction = "UPDATE " + TABLE_NAME +
                " SET priceEVAT=?, VAT=?, loyaltyPoints=?, minStockQuantity=? " +
                "WHERE id_=?;";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {

            PreparedStatement statement = connection.prepareStatement(
                    SQLInstruction
            );

            statement.setBigDecimal(1, newProduct.getPriceEVAT());
            statement.setBigDecimal(2, newProduct.getVat());
            statement.setInt(3, newProduct.getFidelityPoint());
            statement.setInt(4, newProduct.getMinStockQuantity());
            statement.setInt(5, product.getProductId());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(product.getProductId());

            dataMappingObject.put(
                    newProduct.getProductId(),
                    newProduct
            );

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Update impossible", e);
        }
    }

    public boolean updatePrice(Product product, BigDecimal newPriceEVAT, BigDecimal newVat) throws DataBaseException {

        String SQLInstruction = "UPDATE " + TABLE_NAME +
                " SET priceEVAT=?, VAT=? " +
                "WHERE id_=?;";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {

            product.setPriceEVAT(newPriceEVAT);
            product.setVat(newVat);

            PreparedStatement statement = connection.prepareStatement(
                    SQLInstruction
            );

            statement.setBigDecimal(1, newPriceEVAT);
            statement.setBigDecimal(2, newVat);
            statement.setInt(3, product.getProductId());

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Update impossible", e);
        }
    }

    @Override
    public boolean updateFidelityPoint(Product product, int newFidelityPoint) throws DataBaseException {

        String SQLInstruction = "UPDATE " + TABLE_NAME +
                " SET loyaltyPoints=? " +
                "WHERE id_=?;";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {

            product.setFidelityPoint(newFidelityPoint);

            PreparedStatement statement = connection.prepareStatement(
                    SQLInstruction
            );

            statement.setInt(1, newFidelityPoint);
            statement.setInt(2, product.getProductId());

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Update impossible", e);
        }
    }

    @Override
    public boolean updateQuantity(Product product, int newQuantity) throws DataBaseException {

        String SQLInstruction = "UPDATE " + TABLE_NAME +
                " SET minStockQuantity=? " +
                "WHERE id_=?;";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {

            product.setMinStockQuantity(newQuantity);

            PreparedStatement statement = connection.prepareStatement(
                    SQLInstruction
            );

            statement.setInt(1, newQuantity);
            statement.setInt(2, product.getProductId());

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Update impossible", e);
        }
    }

    @Override
    public boolean delete(Product product)
            throws DataBaseException {

        String SQLInstruction =
                "DELETE FROM " + TABLE_NAME + " WHERE id_=?;";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {

            PreparedStatement statement = connection.prepareStatement(
                    SQLInstruction
            );

            statement.setInt(1, product.getProductId());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(product.getProductId());

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Delete impossible", e);
        }
    }

    @Override
    public boolean checkExist(Product product)
            throws DataBaseException {

        boolean exist = false;

        if (product != null) {

            String SQLInstruction =
                    "SELECT COUNT(*) as nbProduct FROM " +
                    TABLE_NAME +
                    " WHERE id_=?;";

            try (Connection connection = MySQLConnector.getInstance().getConnection()) {

                PreparedStatement statement = connection.prepareStatement(
                        SQLInstruction
                );

                statement.setInt(1, product.getProductId());

                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    exist = result.getInt("nbProduct") == 1;
                }

                if (!exist) {
                    insert(product);
                    exist = true;
                }

            } catch (SQLException e) {
                throw new DataBaseException("Check impossible", e);
            }
        }

        return exist;
    }
}