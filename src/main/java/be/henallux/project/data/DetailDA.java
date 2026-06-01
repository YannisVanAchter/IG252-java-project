package main.java.be.henallux.project.data;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.Batch;
import main.java.be.henallux.project.model.Detail;
import main.java.be.henallux.project.model.Document;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.exception.DataValidationException;

public class DetailDA extends CRUD<Detail> {
    private static volatile DetailDA instance;

    private final ProductDA productDA;
    private final DocumentDA documentDA;
    // TODO Implement BatchDA to be complete with the database

    private DetailDA() {
        TABLE_NAME = "Detail";
        IDS_MAPPING_OBJECT = new HashMap<>();

        productDA = ProductDA.getInstance();
        documentDA = DocumentDA.getInstance();
    }

    public static DetailDA getInstance() {
        synchronized (DetailDA.class) {
            if (instance == null)
                instance = new DetailDA();
        }
        return instance;
    }

    @Override
    Detail mapDataToObject(ResultSet data, boolean mapping)
            throws DataBaseException, DataValidationException {
        try {
            int id = data.getInt("id_");

            if (IDS_MAPPING_OBJECT.containsKey(id))
                return IDS_MAPPING_OBJECT.get(id);

            Product product =
                    productDA.getById(data.getInt("productId"), mapping);

            Document document =
                    documentDA.getById(data.getInt("documentId"), mapping);

            List<Batch> batches = null;

            Detail detail = new Detail(
                    id,
                    data.getDouble("priceVAT"),
                    data.getBigDecimal("VAT"),
                    data.getInt("fidelityPointsEarned"),
                    data.getInt("quantity"),
                    document.getDetails(),
                    product,
                    batches
            );

            document.addDetail(detail);

            IDS_MAPPING_OBJECT.put(id, detail);

            return detail;

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<Detail> getAll()
            throws DataBaseException, DataValidationException {
        List<Detail> details = new ArrayList<>();

        try {
            PreparedStatement ps = connector.getConnection().prepareStatement("SELECT * FROM " + TABLE_NAME);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                details.add(
                        mapDataToObject(rs, true)
                );
            }

            return details;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public Detail getById(int id, boolean mapping)
            throws DataBaseException, DataValidationException {
        if (mapping && IDS_MAPPING_OBJECT.containsKey(id))
            return IDS_MAPPING_OBJECT.get(id);

        try {
            PreparedStatement ps =
                    connector.getConnection().prepareStatement(
                            "SELECT * FROM " + TABLE_NAME + " WHERE id_ = ?"
                    );

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return mapDataToObject(rs, mapping);

            return null;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public List<Detail> getsByIds(List<Integer> ids, boolean mapping)
            throws DataBaseException, DataValidationException {
        List<Detail> details = new ArrayList<>();

        for (Integer id : ids) {
            Detail detail = getById(id, mapping);

            if (detail != null)
                details.add(detail);
        }

        return details;
    }

    @Override
    boolean insert(Detail detail)
            throws DataBaseException, DataValidationException {
        productDA.checkExist(detail.getProduct());
        documentDA.checkExist(detail.getDocument());
        try (
                PreparedStatement ps =
                        connector.getConnection().prepareStatement(
                                String.format("""
                                        INSERT INTO %s
                                        (
                                            documentId,
                                            productId,
                                            quantity,
                                            priceVAT,
                                            VAT,
                                            fidelityPointsEarned
                                        )
                                        VALUES (?, ?, ?, ?, ?, ?)
                                        """, TABLE_NAME),
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            ps.setInt(1, detail.getDocument().getId());
            ps.setInt(2, detail.getProduct().getId());
            ps.setInt(3, detail.getQuantity());
            ps.setDouble(4, detail.getPriceEVAT());
            ps.setBigDecimal(5, detail.getVat());
            ps.setInt(6, detail.getFidelityPointEarned());

            int rows = ps.executeUpdate();

            if (rows == 0)
                return false;

            ResultSet keys = ps.getGeneratedKeys();

            if (keys.next()) {
                detail.setId(keys.getInt(1));
                IDS_MAPPING_OBJECT.put(detail.getId(), detail);
            }

            return true;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    boolean update(Detail oldDetail, Detail newDetail)
            throws DataBaseException {
        try (
                PreparedStatement ps =
                        connector.getConnection().prepareStatement(
                                String.format("""
                                        UPDATE %s
                                        SET
                                            documentId=?,
                                            productId=?,
                                            quantity=?,
                                            priceVAT=?,
                                            VAT=?,
                                            fidelityPointsEarned=?
                                        WHERE id_=?
                                        """, TABLE_NAME)
                        )
        ) {
            ps.setInt(1, newDetail.getDocument().getId());
            ps.setInt(2, newDetail.getProduct().getId());
            ps.setInt(3, newDetail.getQuantity());
            ps.setDouble(4, newDetail.getPriceEVAT());
            ps.setBigDecimal(5, newDetail.getVat());
            ps.setInt(6, newDetail.getFidelityPointEarned());
            ps.setInt(7, oldDetail.getId());

            boolean isUpdated = ps.executeUpdate() > 0;

            if (isUpdated) {
                IDS_MAPPING_OBJECT.remove(oldDetail.getId());
                IDS_MAPPING_OBJECT.put(newDetail.getId(), newDetail);
            }

            return isUpdated;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    boolean delete(Detail detail)
            throws DataBaseException {
        try (
                PreparedStatement ps =
                        connector.getConnection().prepareStatement(
                                "DELETE FROM " + TABLE_NAME + " WHERE id_=?"
                        );
        ) {

            ps.setInt(1, detail.getId());

            boolean deleted = ps.executeUpdate() > 0;

            if (deleted)
                IDS_MAPPING_OBJECT.remove(detail.getId());

            return deleted;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    boolean checkExist(Detail detail)
            throws DataBaseException, DataValidationException {
        if (getById(detail.getId()) != null)
            return true;

        return insert(detail);
    }

    private boolean updateField(Detail detail, String field, Object value) throws DataBaseException {
        try (PreparedStatement statement = connector.getConnection().prepareStatement(
                "UPDATE " + TABLE_NAME + " SET " + field + "=? WHERE id_=?"
        )) {
            statement.setObject(1, value);
            statement.setInt(2, detail.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public boolean updateQuantity(Detail detail, int quantity) throws DataBaseException {
        return updateField(detail, "quantity", quantity);
    }

    public boolean updatePriceVat(Detail detail, BigDecimal priceVAT) throws DataBaseException {
        return updateField(detail, "priceVAT", priceVAT);
    }

    public boolean updateVAT(Detail detail, BigDecimal vat) throws DataBaseException {
        return updateField(detail, "VAT", vat);
    }

    public boolean updateFidelityPoints(Detail detail, int fidelityPoints) throws DataBaseException {
        return updateField(detail, "fidelityPointsEarned", fidelityPoints);
    }
}