package main.java.be.henallux.project.data;

import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.FidelityCard;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FidelityCardDA extends CRUD<FidelityCard> {

    private static FidelityCardDA instance;

    private FidelityCardDA() {
        TABLE_NAME = "FidelityCard";
        IDS_MAPPING_OBJECT = new HashMap<>();
    }

    public static FidelityCardDA getInstance() {
        if (instance == null) {
            instance = new FidelityCardDA();
        }
        return instance;
    }

    @Override
    FidelityCard mapDataToObject(ResultSet data, boolean mapping)
            throws DataBaseException, DataValidationException {

        try {
            int id = data.getInt("id_");

            if (mapping && IDS_MAPPING_OBJECT.containsKey(id)) {
                return IDS_MAPPING_OBJECT.get(id);
            }

            int points = data.getInt("points");
            boolean isValid = data.getBoolean("isValid");

            int clientId = data.getInt("clientId");

            ClientSupplier client =
                    ClientSupplierDA.getInstance().getById(clientId, mapping);

            FidelityCard fidelityCard =
                    new FidelityCard(id, points, isValid, client);

            if (mapping) {
                IDS_MAPPING_OBJECT.put(id, fidelityCard);
            }

            return fidelityCard;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while mapping FidelityCard data: " + e.getMessage()
            );
        }
    }

    @Override
    public List<FidelityCard> getAll()
            throws DataBaseException, DataValidationException {

        List<FidelityCard> fidelityCards = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME;

        try (
                Statement statement = connector.getConnection().createStatement();
                ResultSet resultSet = statement.executeQuery(query)
        ) {

            while (resultSet.next()) {
                fidelityCards.add(mapDataToObject(resultSet, true));
            }

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while getting all fidelity cards: " + e.getMessage()
            );
        }

        return fidelityCards;
    }

    @Override
    public FidelityCard getById(int id, boolean mapping)
            throws DataBaseException, DataValidationException {

        if (mapping && IDS_MAPPING_OBJECT.containsKey(id)) {
            return IDS_MAPPING_OBJECT.get(id);
        }

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE id_ = ?";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapDataToObject(resultSet, mapping);
                }
            }

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while getting fidelity card by id: " + e.getMessage()
            );
        }

        return null;
    }

    @Override
    public List<FidelityCard> getsByIds(List<Integer> ids, boolean mapping)
            throws DataBaseException, DataValidationException {

        List<FidelityCard> fidelityCards = new ArrayList<>();

        for (Integer id : ids) {
            FidelityCard fidelityCard = getById(id, mapping);

            if (fidelityCard != null) {
                fidelityCards.add(fidelityCard);
            }
        }

        return fidelityCards;
    }

    @Override
    public boolean insert(FidelityCard model)
            throws DataBaseException, DataValidationException {
        ClientSupplierDA.getInstance().checkExist(model.getClient())

        String query = String.format("""
                INSERT INTO %s (points, isValid, clientId)
                VALUES (?, ?, ?)
                """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(
                                query,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setInt(1, model.getTotalPoint());
            statement.setBoolean(2, model.getIsValid());
            statement.setInt(3, model.getClient().getId());

            int rows = statement.executeUpdate();

            if (rows <= 0) {
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);

                    model.setId(generatedId);

                    IDS_MAPPING_OBJECT.put(generatedId, model);
                }
            }

            return true;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while inserting fidelity card: " + e.getMessage()
            );
        }
    }

    @Override
    public boolean update(FidelityCard model, FidelityCard newModel)
            throws DataBaseException, DataValidationException {

        String query = String.format("""
                UPDATE %s
                SET points = ?, isValid = ?, clientId = ?
                WHERE id_ = ?
                """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, newModel.getTotalPoint());
            statement.setBoolean(2, newModel.getIsValid());
            statement.setInt(3, newModel.getClient().getId());
            statement.setInt(4, model.getId());

            int rows = statement.executeUpdate();

            if (rows > 0) {
                IDS_MAPPING_OBJECT.remove(model.getId());
                IDS_MAPPING_OBJECT.put(newModel.getId(), newModel);
            }

            return rows > 0;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while updating fidelity card: " + e.getMessage()
            );
        }
    }

    @Override
    public boolean delete(FidelityCard model)
            throws DataBaseException {

        String query = String.format("DELETE FROM %s WHERE id_ = ?", TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, model.getId());

            int rows = statement.executeUpdate();

            if (rows > 0) {
                IDS_MAPPING_OBJECT.remove(model.getId());
            }

            return rows > 0;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while deleting fidelity card: " + e.getMessage()
            );
        }
    }

    @Override
    public boolean checkExist(FidelityCard model)
            throws DataBaseException, DataValidationException {

        FidelityCard existing = getById(model.getId(), false);

        return existing != null;
    }

    /*
     * ===========================
     * UPDATE FIELD METHODS
     * ===========================
     */

    public boolean updateTotalPoint(FidelityCard fidelityCard, int totalPoint)
            throws DataBaseException, DataValidationException {

        String query = String.format"""
                UPDATE %s
                SET points = ?
                WHERE id_ = ?
                """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, totalPoint);
            statement.setInt(2, fidelityCard.getId());

            int rows = statement.executeUpdate();

            if (rows > 0) {
                fidelityCard.addPoint(
                        totalPoint - fidelityCard.getTotalPoint()
                );
            }

            return rows > 0;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while updating fidelity card points: "
                            + e.getMessage()
            );
        }
    }

    public boolean updateIsValid(FidelityCard fidelityCard, boolean isValid)
            throws DataBaseException {

        String query = String.format("""
                UPDATE %s
                SET isValid = ?
                WHERE id_ = ?
                """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setBoolean(1, isValid);
            statement.setInt(2, fidelityCard.getId());

            int rows = statement.executeUpdate();

            if (rows > 0) {
                fidelityCard.setIsValid(isValid);
            }

            return rows > 0;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while updating fidelity card validity: "
                            + e.getMessage()
            );
        }
    }

    public boolean updateClient(
            FidelityCard fidelityCard,
            ClientSupplier client
    ) throws DataBaseException, DataValidationException {

        String query = String.format("""
                UPDATE %s
                SET clientId = ?
                WHERE id_ = ?
                """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, client.getId());
            statement.setInt(2, fidelityCard.getId());

            int rows = statement.executeUpdate();

            if (rows > 0) {
                fidelityCard.setClient(client);
            }

            return rows > 0;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while updating fidelity card client: "
                            + e.getMessage()
            );
        }
    }
}