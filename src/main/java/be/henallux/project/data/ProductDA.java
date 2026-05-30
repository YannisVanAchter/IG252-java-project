package main.java.be.henallux.project.data;

import java.math.BigDecimal;
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
import main.java.be.henallux.project.data.DiscountDA;
import main.java.be.henallux.project.data.QuantityProductDA;
import main.java.be.henallux.project.data.LocationProductDA;
import main.java.be.henallux.project.data.ProductCategoryDA;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.QuantityProduct;

public class ProductDA extends CRUD<Product> {
    private static volatile ProductDA instance;
    private final String TABLE_NAME = "Product";
    private final Map<Integer, Product> dataMappingObject;
    private final DiscountDA discountDA;
    private final QuantityProductDA quantityDA;
    private final LocationProductDA locationDA;
    private final ProductCategoryDA categoryDA;

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
    public Product mapDataToObject(ResultSet data, boolean mapping) throws DataBaseException, DataValidationException {
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
                    List<Integer> idsLocation = locationDA.getAll().stream().map(location ->
                        QuantityProduct.hashCode(location, product)
                    ).toList();

                    product.setLocation(quantityDA.getsByIds(idsLocation));
                }

                dataMappingObject.put(id, product);
            }

        } catch (SQLException e) {
            throw new DataBaseException("Error mapping Product", e);
        }

        return dataMappingObject.get(id);
    }

    @Override
    public List<Product> getAll() throws DataBaseException, DataValidationException {
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " ORDER BY id_";
        List<Product> products = new ArrayList<>();

        try (Connection connection = connector.getConnection()) {

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
    public List<Product> getsByIds(List<Integer> ids, boolean mapping) throws DataBaseException, DataValidationException {
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

        try (Connection connection = connector.getConnection()) {

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
    public List<Product> getsByIds(List<Integer> ids) throws DataBaseException, DataValidationException {
        return getsByIds(ids, true);
    }

    @Override
    public Product getById(int id, boolean mapping) throws DataBaseException, DataValidationException {

        Product product = dataMappingObject.get(id);

        if (product != null) {
            return product;
        }

        String SQLInstruction =
                "SELECT * FROM " + TABLE_NAME + " WHERE id_=?;";

        try (Connection connection = connector.getConnection()) {

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
            throws DataBaseException, DataValidationException {

        return getById(id, true);
    }

    @Override
    public boolean insert(Product newProduct) throws DataBaseException, DataValidationException {

        boolean inserted = false;

        String SQLInstruction =
                "INSERT INTO " + TABLE_NAME +
                " (name_, priceEVAT, VAT, loyaltyPoints, isEdible, minStockQuantity, categoryId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (Connection connection = connector.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(
                    SQLInstruction,
                    PreparedStatement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, newProduct.getName());
            statement.setBigDecimal(2, newProduct.getPriceEVAT());
            statement.setBigDecimal(3, newProduct.getVat());
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
    public boolean update(Product product, Product newProduct) throws DataBaseException, DataValidationException {

        String SQLInstruction = "UPDATE " + TABLE_NAME +
                " SET priceEVAT=?, VAT=?, loyaltyPoints=?, minStockQuantity=? " +
                "WHERE id_=?;";

        try (Connection connection = connector.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(
                    SQLInstruction
            );

            statement.setBigDecimal(1, newProduct.getPriceEVAT());
            statement.setBigDecimal(2, newProduct.getVat());
            statement.setInt(3, newProduct.getFidelityPoint());
            statement.setInt(4, newProduct.getMinStockQuantity());
            statement.setInt(5, product.getId());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(product.getId());

            dataMappingObject.put(
                    newProduct.getId(),
                    newProduct
            );

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Update impossible", e);
        }
    }

    public boolean updatePrice(Product product, BigDecimal newPriceEVAT, BigDecimal newVat) throws DataBaseException, DataValidationException {

        String SQLInstruction = "UPDATE " + TABLE_NAME +
                " SET priceEVAT=?, VAT=? " +
                "WHERE id_=?;";

        try (Connection connection = connector.getConnection()) {

            product.setPriceEVAT(newPriceEVAT);
            product.setVat(newVat);

            PreparedStatement statement = connection.prepareStatement(
                    SQLInstruction
            );

            statement.setBigDecimal(1, newPriceEVAT);
            statement.setBigDecimal(2, newVat);
            statement.setInt(3, product.getId());

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Update impossible", e);
        }
    }

    public boolean updateFidelityPoint(Product product, int newFidelityPoint) throws DataBaseException, DataValidationException {

        String SQLInstruction = "UPDATE " + TABLE_NAME +
                " SET loyaltyPoints=? " +
                " WHERE id_=?;";

        try (Connection connection = connector.getConnection()) {

            product.setFidelityPoint(newFidelityPoint);

            PreparedStatement statement = connection.prepareStatement(
                    SQLInstruction
            );

            statement.setInt(1, newFidelityPoint);
            statement.setInt(2, product.getId());

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Update impossible", e);
        }
    }

    public boolean updateQuantity(Product product, int newQuantity) throws DataBaseException, DataValidationException {

        String SQLInstruction = "UPDATE " + TABLE_NAME +
                " SET minStockQuantity=? " +
                "WHERE id_=?;";

        try (Connection connection = connector.getConnection()) {

            product.setMinStockQuantity(newQuantity);

            PreparedStatement statement = connection.prepareStatement(
                    SQLInstruction
            );

            statement.setInt(1, newQuantity);
            statement.setInt(2, product.getId());

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Update impossible", e);
        }
    }

    @Override
    public boolean delete(Product product)
            throws DataBaseException, DataValidationException {

        String SQLInstruction =
                "DELETE FROM " + TABLE_NAME + " WHERE id_=?;";

        try (Connection connection = connector.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(
                    SQLInstruction
            );

            statement.setInt(1, product.getId());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(product.getId());

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Delete impossible", e);
        }
    }

    @Override
    public boolean checkExist(Product product)
            throws DataBaseException, DataValidationException {

        boolean exist = false;

        if (product != null) {

            String SQLInstruction =
                    "SELECT COUNT(*) as nbProduct FROM " +
                    TABLE_NAME +
                    " WHERE id_=?;";

            try (Connection connection = connector.getConnection()) {

                PreparedStatement statement = connection.prepareStatement(
                        SQLInstruction
                );

                statement.setInt(1, product.getId());

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

    public Map<ClientSupplier, List<Product>> getLowQuantityProduct() throws DataBaseException, DataValidationException {
        String SQLInstruction = "SELECT * FROM vw_LowQuantity_ProductSupplier;";
        try (Statement statement = connector.getInstance().getConnection().createStatement(SQLInstruction)) {
            ResultSet result = statement.executeQuery();

            Map<ClientSupplier, List<Product>> supplier_mapping_product = new HashMap<>();
            ClientSupplierDA supplierDA = ClientSupplierDA.getInstance();
            ClientSupplier supplier;
            result.next();
            do {
                if (supplier == null || supplier.getId() != result.getInt("supplierId")) {
                    supplier = supplierDA.getById(result.getInt("supplierId"));
                    supplier_mapping_product.put(supplier, new ArrayList<>());
                }
                supplier_mapping_product
                        .get(supplier)
                        .add(getById(result.getInt("productId")));
            } while (result.next());

            return supplier_mapping_product;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }
}