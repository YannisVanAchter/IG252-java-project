package main.java.be.henallux.project.data;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.data.CRUD;
import main.java.be.henallux.project.data.ProductDA;
import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.Discount;
import main.java.be.henallux.project.model.Product;

public class DiscountDA extends CRUD<Discount> {
    private static volatile DiscountDA instance;
    private final String TABLE_NAME = "Discount";
    private final Map<Integer, Discount> dataMappingObject;
    private final ProductDA productDA;

    private DiscountDA() {
        super();
        this.dataMappingObject = new HashMap<>();
        productDA = ProductDA.getInstance();
    }

    @SuppressWarnings("DoubleCheckedLocking")
    public static DiscountDA getInstance() {
        if (instance == null) {
            synchronized (DiscountDA.class) {
                if (instance == null) 
                    setInstance(new DiscountDA());
            }
        }

        return instance;
    }

    @SuppressWarnings("DoubleCheckedLocking")
    private static void setInstance(DiscountDA discountDA) {
        if (instance == null) {
            synchronized (DiscountDA.class) {
                if (instance == null) 
                    instance = discountDA;
            }
        }
    }

    @Override
    public Discount mapDataToObject(ResultSet data, boolean mapping) throws DataBaseException, DataValidationException {
        int id;

        try {
            int quantity = data.getInt("requiredQuantity");
            BigDecimal discountPercentage = data.getBigDecimal("discountPercentage");
            LocalDate startDate = this.SQLDateToLocalDate(data.getDate("startDate"));
            LocalDate endDate = this.SQLDateToLocalDate(data.getDate("endDate"));

            id = Discount.hashCode(quantity, discountPercentage, startDate, endDate);
            if (dataMappingObject.get(id) == null) {
                Discount discount = new Discount(
                    quantity,
                    discountPercentage,
                    startDate,
                    endDate,
                    data.getString("name_"),
                    productDA.getById(data.getInt("productId"), false)
                );

                dataMappingObject.put(id, discount);

                discount.getProduct().addDiscount(discount);
            }

        } catch (SQLException e) {
            throw new DataBaseException("Error mapping Discount", e);
        }

        return dataMappingObject.get(id);
    }

    @Override
    public List<Discount> getAll() throws DataBaseException, DataValidationException {
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + ";";
        List<Discount> discounts = new ArrayList<>();

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                discounts.add(mapDataToObject(resultSet, true));
            }

        } catch (SQLException e) {
            throw new DataBaseException("Error getting all discounts", e);
        }

        return discounts;
    }

    /**
     * Get all discounts witch are identify by the hashCode 
     * @param hashCodes arrayLists of all discounts witch have the hashCode given in the list
     * @param mapping inform if we need to map ?
     */
    @Override
    public List<Discount> getsByIds(List<Integer> hashCodes, boolean mapping) throws DataBaseException, DataValidationException {
        return getAll().stream().filter(d ->
                hashCodes.contains(Discount.hashCode(
                        d.getRequiredQuantity(),
                        d.getDiscountPercentage(),
                        d.getStartDate(),
                        d.getEndDate()
                        )
                )
            ).toList();
    }

    @Override
    public List<Discount> getsByIds(List<Integer> ids) throws DataBaseException, DataValidationException {
        return getsByIds(ids, true);
    }

    @Override
    public Discount getById(int id, boolean mapping) throws DataBaseException, DataValidationException {
        getAll();
        
        return dataMappingObject.get(id);
    }

    @Override
    public Discount getById(int id) throws DataBaseException, DataValidationException {
        return getById(id, true);
    }

    @Override
    public boolean insert(Discount newDiscount) throws DataBaseException, DataValidationException {
        boolean inserted = false;
        productDA.checkExist(newDiscount.getProduct());

        String SQLInstruction =
                "INSERT INTO " + TABLE_NAME +
                " (discountPercentage, startDate, endDate, requiredQuantity, name_, productId) VALUES (?, ?, ?, ?, ?, ?);";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            productDA.insert(newDiscount.getProduct());

            statement.setBigDecimal(1, newDiscount.getDiscountPercentage());
            statement.setDate(2, Date.valueOf(newDiscount.getStartDate()));
            statement.setDate(3, Date.valueOf(newDiscount.getEndDate()));
            statement.setInt(4, newDiscount.getRequiredQuantity());
            statement.setString(5, newDiscount.getName());
            statement.setInt(6, newDiscount.getProduct().getId());

            inserted = 0 < statement.executeUpdate();

            if (inserted) {
                dataMappingObject.put(newDiscount.hashCode(), newDiscount);
            }

        } catch (SQLException e) {
            throw new DataBaseException("Insert discount impossible", e);
        }

        return inserted;
    }

    @Override
    public boolean update(Discount discount, Discount newDiscount) throws DataBaseException {
        String SQLInstruction =
                "UPDATE " + TABLE_NAME +
                " SET discountPercentage=? AND startDate=?, endDate=?, requiredQuantity=?, name_=?, productId=? WHERE discountPercentage=? AND startDate=? AND endDate=? AND requiredQuantity=?;";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setBigDecimal(1, newDiscount.getDiscountPercentage());
            statement.setDate(2, Date.valueOf(newDiscount.getStartDate()));
            statement.setDate(3, Date.valueOf(newDiscount.getEndDate()));
            statement.setInt(4, newDiscount.getRequiredQuantity());
            statement.setString(5, newDiscount.getName());
            statement.setInt(6, newDiscount.getProduct().getId());

            statement.setBigDecimal(7, discount.getDiscountPercentage());
            statement.setDate(8, Date.valueOf(discount.getStartDate()));
            statement.setDate(9, Date.valueOf(discount.getEndDate()));
            statement.setInt(10, discount.getRequiredQuantity());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(discount.hashCode());
            dataMappingObject.put(newDiscount.hashCode(), newDiscount);

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Update impossible", e);
        }
    }

    @Override
    public boolean delete(Discount discount) throws DataBaseException {
        String SQLInstruction = "DELETE FROM " + TABLE_NAME + " WHERE discountPercentage=? AND startDate=? AND endDate=? AND requiredQuantity=?;";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setBigDecimal(1, discount.getDiscountPercentage());
            statement.setDate(2, Date.valueOf(discount.getStartDate()));
            statement.setDate(3, Date.valueOf(discount.getEndDate()));
            statement.setInt(4, discount.getRequiredQuantity());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(discount.hashCode());

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Delete impossible", e);
        }
    }

    @Override
    public boolean checkExist(Discount discount) throws DataBaseException, DataValidationException {
        boolean exist = false;

        if (discount != null) {
            String SQLInstruction =
                    "SELECT COUNT(*) as nbDiscount FROM " +
                    TABLE_NAME +
                    " WHERE discountPercentage=? AND startDate=? AND endDate=? AND requiredQuantity=?;";

            try (Connection connection = connector.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(SQLInstruction);

                statement.setBigDecimal(1, discount.getDiscountPercentage());
                statement.setDate(2, Date.valueOf(discount.getStartDate()));
                statement.setDate(3, Date.valueOf(discount.getEndDate()));
                statement.setInt(4, discount.getRequiredQuantity());

                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    exist = result.getInt("nbDiscount") > 0;
                }

                if (!exist) {
                    insert(discount);
                    exist = true;
                }

            } catch (SQLException e) {
                throw new DataBaseException("Check impossible", e);
            }
        }

        return exist;
    }
}