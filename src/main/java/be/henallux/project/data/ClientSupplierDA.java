package main.java.be.henallux.project.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.be.henallux.project.data.exception.DataBaseException;

import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.FidelityCard;

import main.java.be.henallux.project.model.exception.DataValidationException;

public class ClientSupplierDA extends CRUD<ClientSupplier> {

    private static final volatile ClientSupplierDA instance;
    private final String TABLE_NAME;
    private final AddressDA addressDA;
    private final FidelityCardDA fidelityCardDA;

    private ClientSupplierDA() {
        TABLE_NAME = "Client_supplier";
        IDS_MAPPING_OBJECT = new HashMap<>();

        addressDA = AddressDA.getInstance();
        fidelityCardDA = FidelityCardDA.getInstance();
    }

    public static ClientSupplierDA getInstance() {
        synchronized (ClientSupplierDA.class) {
            if (instance == null) {
                instance = new ClientSupplierDA();
            }
        }
        return instance;
    }

    @Override
    ClientSupplier mapDataToObject(ResultSet data, boolean mapping)
            throws DataBaseException, DataValidationException {

        try {
            int id = data.getInt("id_");

            // Cache mapping
            if (IDS_MAPPING_OBJECT.containsKey(id)) {
                return IDS_MAPPING_OBJECT.get(id);
            }

            String name = data.getString("name_");
            String firstname = data.getString("firstname");
            String email = data.getString("email");
            String phoneNumber = data.getString("phoneNumber");

            boolean isClient = data.getBoolean("isClient");
            boolean isSupplier = data.getBoolean("isSupplier");
            boolean isUs = data.getBoolean("isUs");

            String VATNumber = data.getString("VATNumber");

            java.sql.Date sqlDate = data.getDate("dateBecameClient");

            Address address = null;
            FidelityCard fidelityCard = null;

            int addressId = data.getInt("addressId");

            if ( mapping && !data.wasNull()) {
                address = addressDA.getById(addressId, mapping);
            }

            if (isClient && mapping) {
                try {
                    fidelityCard = fidelityCardDA.getByClientSupplierId(id);
                } catch (Exception e) {
                    fidelityCard = null;
                }
            }

            ClientSupplier clientSupplier = new ClientSupplier(
                    id,
                    name,
                    firstname,
                    email,
                    phoneNumber,
                    address,
                    isClient,
                    isSupplier,
                    isUs,
                    VATNumber,
                    (sqlDate != null ? SQLDateToLocalDate(sqlDate) : null),
                    fidelityCard
            );

            IDS_MAPPING_OBJECT.put(id, clientSupplier);

            return clientSupplier;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while mapping ClientSupplier data to object",
                    e
            );
        }
    }

    @Override
    public List<ClientSupplier> getAll()
            throws DataBaseException, DataValidationException {

        List<ClientSupplier> clientSuppliers = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME + ";";

        try (
                Statement statement = connector.getConnection().createStatement();
                ResultSet resultSet = statement.executeQuery(query)
        ) {

            while (resultSet.next()) {
                clientSuppliers.add(mapDataToObject(resultSet, true));
            }

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while retrieving all ClientSupplier",
                    e
            );
        }

        return clientSuppliers;
    }

    @Override
    public ClientSupplier getById(int id, boolean mapping)
            throws DataBaseException, DataValidationException {

        if (IDS_MAPPING_OBJECT.containsKey(id))
            return IDS_MAPPING_OBJECT.get(id);

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE id_ = ?;";

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
                    "Error while retrieving ClientSupplier by id",
                    e
            );
        }

        return null;
    }

    @Override
    public List<ClientSupplier> getsByIds(List<Integer> ids, boolean mapping)
            throws DataBaseException, DataValidationException {

        List<ClientSupplier> clientSuppliers = new ArrayList<>();

        for (Integer id : ids) {
            ClientSupplier clientSupplier = getById(id, mapping);

            if (clientSupplier != null)
                clientSuppliers.add(clientSupplier);
        }

        return clientSuppliers;
    }

    @Override
    public boolean insert(ClientSupplier clientSupplier)
            throws DataBaseException, DataValidationException {
        addressDA.chechExist(clientSupplier.getAddress())
        String query = String.format("""
                INSERT INTO %S
                (
                    name_,
                    firstname,
                    email,
                    phoneNumber,
                    isClient,
                    isSupplier,
                    isUs,
                    VATNumber,
                    dateBecameClient,
                    addressId
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(
                                query,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setString(1, clientSupplier.getName());
            statement.setString(2, clientSupplier.getFirstname());
            statement.setString(3, clientSupplier.getEmail());
            statement.setString(4, clientSupplier.getPhoneNumber());

            statement.setBoolean(5, clientSupplier.getIsClient());
            statement.setBoolean(6, clientSupplier.getIsSupplier());
            statement.setBoolean(7, clientSupplier.getIsUs());

            statement.setString(8, clientSupplier.getVATNumber());

            if (clientSupplier.getBecameClientDate() != null) {
                statement.setDate(
                        9,
                        LocalDateToSQLDate(
                                clientSupplier.getBecameClientDate()
                        )
                );
            } else {
                statement.setDate(9, null);
            }

            if (clientSupplier.getAddress() != null) {
                statement.setInt(
                        10,
                        clientSupplier.getAddress().getAddressId()
                );
            } else {
                statement.setNull(10, java.sql.Types.INTEGER);
            }

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    int generatedId = generatedKeys.getInt(1);

                    IDS_MAPPING_OBJECT.put(
                            generatedId,
                            getById(generatedId)
                    );
                }
            }

            return true;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while inserting ClientSupplier",
                    e
            );
        }
    }

    @Override
    public boolean update(
            ClientSupplier oldModel,
            ClientSupplier newModel
    ) throws DataBaseException, DataValidationException {

        String query = String.format("""
                UPDATE %s
                SET
                    name_ = ?,
                    firstname = ?,
                    email = ?,
                    phoneNumber = ?,
                    isClient = ?,
                    isSupplier = ?,
                    isUs = ?,
                    VATNumber = ?,
                    dateBecameClient = ?,
                    addressId = ?
                WHERE id_ = ?;
                """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setString(1, newModel.getName());
            statement.setString(2, newModel.getFirstname());
            statement.setString(3, newModel.getEmail());
            statement.setString(4, newModel.getPhoneNumber());

            statement.setBoolean(5, newModel.getIsClient());
            statement.setBoolean(6, newModel.getIsSupplier());
            statement.setBoolean(7, newModel.getIsUs());

            statement.setString(8, newModel.getVATNumber());

            if (newModel.getBecameClientDate() != null) {
                statement.setDate(
                        9,
                        LocalDateToSQLDate(
                                newModel.getBecameClientDate()
                        )
                );
            } else {
                statement.setDate(9, null);
            }

            if (newModel.getAddress() != null) {
                statement.setInt(
                        10,
                        newModel.getAddress().getAddressId()
                );
            } else {
                statement.setNull(10, java.sql.Types.INTEGER);
            }

            statement.setInt(11, oldModel.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {

                IDS_MAPPING_OBJECT.remove(oldModel.getId());

                IDS_MAPPING_OBJECT.put(
                        newModel.getId(),
                        newModel
                );


                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while updating ClientSupplier",
                    e
            );
        }
    }

    public boolean updateAddress(
            ClientSupplier oldModel,
            Address newAddress
    ) throws DataBaseException, DataValidationException {
        addressDA.checkExist(newAddress);
        String query = String.format("""
                UPDATE %s
                SET
                    addressId = ?
                WHERE id_ = ?;
                """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {
            statement.setInt(
                    1,
                    newAddress.getAddressId()
            );
            statement.setInt(2, oldModel.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                oldModel.setAddress(newAddress);

                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while updating ClientSupplier",
                    e
            );
        }
    }

    public boolean updateEmail(
            ClientSupplier oldModel,
            String newEmail
    ) throws DataBaseException, DataValidationException {
        String query = String.format("""
                UPDATE %s
                SET
                    email = ?
                WHERE id_ = ?;
                """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {
            statement.setInt(
                    1,
                    newEmail
            );
            statement.setInt(2, oldModel.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                oldModel.setEmail(newEmail);

                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while updating ClientSupplier",
                    e
            );
        }
    }

    public boolean updatePhoneNumber(
            ClientSupplier oldModel,
            String newPhoneNumber
    ) throws DataBaseException, DataValidationException {
        String query = String.format("""
                UPDATE %s
                SET
                    phoneNumber = ?
                WHERE id_ = ?;
                """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {
            statement.setInt(
                    1,
                    newPhoneNumber
            );
            statement.setInt(2, oldModel.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                oldModel.setPhoneNumber(newPhoneNumber);

                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while updating ClientSupplier",
                    e
            );
        }
    }

    @Override
    public boolean delete(ClientSupplier clientSupplier)
            throws DataBaseException, DataValidationException {

        String query = "DELETE FROM " + TABLE_NAME + " WHERE id_ = ?;";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, clientSupplier.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                IDS_MAPPING_OBJECT.remove(clientSupplier.getId());
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while deleting ClientSupplier",
                    e
            );
        }
    }

    @Override
    public boolean checkExist(ClientSupplier clientSupplier)
            throws DataBaseException, DataValidationException {

        String query = String.format("""
                SELECT id_
                FROM %s
                WHERE
                    email = ?
                """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setString(1, clientSupplier.getEmail());

            try (ResultSet resultSet = statement.executeQuery()) {

                return resultSet.next();
            }

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while checking existence of ClientSupplier",
                    e
            );
        }
    }

    public List<ClientSupplier> getClients()
            throws DataBaseException, DataValidationException {

        List<ClientSupplier> clients = new ArrayList<>();

        String query = String.format("""
                SELECT *
                FROM %s
                WHERE isClient = true
                """, TABLE_NAME);

        try (
                Statement statement =
                        connector.getConnection().createStatement();

                ResultSet resultSet =
                        statement.executeQuery(query)
        ) {

            while (resultSet.next()) {
                clients.add(mapDataToObject(resultSet, true));
            }

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while retrieving clients",
                    e
            );
        }

        return clients;
    }

    public List<ClientSupplier> getSuppliers()
            throws DataBaseException, DataValidationException {

        List<ClientSupplier> suppliers = new ArrayList<>();

        String query = String.format("""
                SELECT *
                FROM %s
                WHERE isSupplier = true
                """, TABLE_NAME);

        try (
                Statement statement =
                        connector.getConnection().createStatement();

                ResultSet resultSet =
                        statement.executeQuery(query)
        ) {

            while (resultSet.next()) {
                suppliers.add(mapDataToObject(resultSet, true));
            }

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while retrieving suppliers",
                    e
            );
        }

        return suppliers;
    }
}