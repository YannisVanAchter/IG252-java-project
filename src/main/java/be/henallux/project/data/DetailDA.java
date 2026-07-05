package be.henallux.project.data;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import be.henallux.project.data.exception.DataBaseException;
import be.henallux.project.model.Batch;
import be.henallux.project.model.Detail;
import be.henallux.project.model.Document;
import be.henallux.project.model.Product;
import be.henallux.project.model.exception.DataValidationException;

public class DetailDA extends CRUD<Detail>
{
    private static volatile DetailDA instance;

    private ProductDA productDA;
    private DocumentDA documentDA;
    private final MySQLConnector connector;
    // TODO Implement BatchDA to be complete with the database

    private DetailDA()
    {
        TABLE_NAME = "Detail";
        IDS_MAPPING_OBJECT = new HashMap<>();
        connector = MySQLConnector.getInstance();
    }

    public static DetailDA getInstance()
    {
        synchronized (DetailDA.class) {
            if (instance == null)
                instance = new DetailDA();
        }
        return instance;
    }

    private ProductDA getProductDA() {
        if (productDA == null) productDA = ProductDA.getInstance();
        return productDA;
    }

    private DocumentDA getDocumentDA() {
        if (documentDA == null) documentDA = DocumentDA.getInstance();
        return documentDA;
    }


    @Override
    Detail mapDataToObject(ResultSet data, boolean mapping)
            throws DataBaseException, DataValidationException
    {
        int id;
        try {
            id = data.getInt("id_");
        } catch (SQLException e) {
            throw new DataBaseException("Error getting id from ResultSet", e);
        }

        if(IDS_MAPPING_OBJECT.containsKey(id))
            return IDS_MAPPING_OBJECT.get(id);

        Product product;
        Document document;
        try {
            product = getProductDA().getById(data.getInt("productId"), mapping);
            document = getDocumentDA().getById(data.getInt("documentId"), mapping);
        } catch (SQLException e) {
            throw new DataBaseException("Error reading detail fields from ResultSet", e);
        }

        List<Batch> batches = null;

        double priceVAT;
        BigDecimal vat;
        int fidelityPointsEarned;
        int quantity;
        try {
            priceVAT = data.getDouble("priceVAT");
            vat = data.getBigDecimal("VAT").divide(BigDecimal.valueOf(100));
            fidelityPointsEarned = data.getInt("fidelityPointsEarned");
            quantity = data.getInt("quantity");
        } catch (SQLException e) {
            throw new DataBaseException("Error reading detail fields from ResultSet", e);
        }

        Detail detail = new Detail(
                id,
                priceVAT,
                vat,
                fidelityPointsEarned,
                quantity,
                document,
                product,
                batches
        );

        IDS_MAPPING_OBJECT.put(id, detail);
        document.addDetail(detail);

        return detail;
    }

    @Override
    public List<Detail> getAll()
            throws DataBaseException, DataValidationException
    {
        List<Detail> details = new ArrayList<>();

        try (Statement st = connector.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + TABLE_NAME)) {

            while (rs.next()) {
                details.add(mapDataToObject(rs, true));
            }

            return details;
        }
        catch(SQLException e)
        {
            throw new DataBaseException("Error getting all details", e);
        }
    }

    @Override
    public Detail getById(int id, boolean mapping)
            throws DataBaseException, DataValidationException
    {
        if(mapping && IDS_MAPPING_OBJECT.containsKey(id))
            return IDS_MAPPING_OBJECT.get(id);

        try
        {
            try (PreparedStatement ps =
                         connector.getConnection().prepareStatement(
                                 "SELECT * FROM " + TABLE_NAME + " WHERE id_ = ?")) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {

                    if(rs.next())
                        return mapDataToObject(rs, mapping);

                    return null;
                }
            }
        }
        catch(SQLException e)
        {
            throw new DataBaseException("Error getting detail by id", e);
        }
    }

    @Override
    public List<Detail> getsByIds(List<Integer> ids, boolean mapping)
            throws DataBaseException, DataValidationException
    {
        List<Detail> details = new ArrayList<>();

        for(Integer id : ids)
        {
            Detail detail = getById(id, mapping);

            if(detail != null)
                details.add(detail);
        }

        return details;
    }

    @Override
    public boolean insert(Detail detail)
            throws DataBaseException, DataValidationException
    {
        getProductDA().checkExist(detail.getProduct());
        getDocumentDA().checkExist(detail.getDocument());

        try (PreparedStatement ps =
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
                             Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, detail.getDocument().getId());
            ps.setInt(2, detail.getProduct().getId());
            ps.setInt(3, detail.getQuantity());
            ps.setDouble(4, detail.getPriceEVAT());
            ps.setBigDecimal(5, detail.getVat());
            ps.setInt(6, detail.getFidelityPointEarned());

            int rows = ps.executeUpdate();

            if(rows == 0)
                return false;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if(keys.next()) {
                    detail.setId(keys.getInt(1));
                    IDS_MAPPING_OBJECT.put(detail.getId(), detail);
                }
            }

            return true;
        }
        catch(SQLException e)
        {
            throw new DataBaseException("Error inserting detail", e);
        }
    }

    @Override
    boolean update(Detail oldDetail, Detail newDetail)
            throws DataBaseException, DataValidationException
    {
        try (PreparedStatement ps =
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
                        """, TABLE_NAME))) {

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
        }
        catch(SQLException e)
        {
            throw new DataBaseException("Error updating detail", e);
        }
    }

    @Override
    boolean delete(Detail detail)
            throws DataBaseException
    {
        try (PreparedStatement ps =
                     connector.getConnection().prepareStatement(
                             "DELETE FROM " + TABLE_NAME + " WHERE id_=?")) {

            ps.setInt(1, detail.getId());

            boolean deleted = ps.executeUpdate() > 0;

            if(deleted)
                IDS_MAPPING_OBJECT.remove(detail.getId());

            return deleted;
        }
        catch(SQLException e)
        {
            throw new DataBaseException("Error deleting detail", e);
        }
    }

    @Override
    boolean checkExist(Detail detail)
            throws DataBaseException, DataValidationException
    {
        if(getById(detail.getId(), true) != null)
            return true;

        return insert(detail);
    }

    private boolean updateField(Detail detail, String field, Object value) throws DataBaseException {
        try (PreparedStatement statement = connector.getConnection().prepareStatement(
                "UPDATE " + TABLE_NAME + " SET " + field + "=? WHERE id_=?")) {

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