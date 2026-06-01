package main.java.be.henallux.project.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.be.henallux.project.data.exception.DataBaseException;

import main.java.be.henallux.project.model.DocumentType;
import main.java.be.henallux.project.model.DocumentTypeRepository;

import main.java.be.henallux.project.model.exception.DataValidationException;

/**
 * DAO for DocumentType table.
 *
 * Handle CRUD operations for DocumentType objects.
 */
public class DocumentTypeDA extends CRUD<DocumentType> {

    private static volatile DocumentTypeDA instance;

    private DocumentTypeDA() throws DataBaseException, DataValidationException {
        TABLE_NAME = "DocumentType";
        IDS_MAPPING_OBJECT = new HashMap<>();

        DocumentTypeRepository documentTypeRepository = DocumentTypeRepository.getInstance();
        for (DocumentType docType : documentTypeRepository.getDocumentTypes())
            this.checkExist(docType);
    }

    public static DocumentTypeDA getInstance() {
        synchronized (DocumentTypeDA.class) {
            try {
                if (instance == null)
                    instance = new DocumentTypeDA();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return instance;
    }

    /**
     * Convert SQL ResultSet row into DocumentType object
     *
     * @param data ResultSet positioned on a valid row
     * @param mapping use cache mapping system
     *
     * @return mapped DocumentType object
     *
     * @throws DataBaseException SQL error
     * @throws DataValidationException invalid object data
     */
    @Override
    DocumentType mapDataToObject(ResultSet data, boolean mapping)
            throws DataBaseException, DataValidationException {
        try {
            int id = data.getInt("id_");

            if (IDS_MAPPING_OBJECT.containsKey(id))
                return IDS_MAPPING_OBJECT.get(id);

            DocumentType documentType = new DocumentType(id, data.getString("name_"));
            IDS_MAPPING_OBJECT.put(id, documentType);
            return documentType;

        } catch (SQLException exception) {
            throw new DataBaseException("Error while mapping DocumentType data", exception);
        }
    }

    /**
     * Get all DocumentType entries
     *
     * @return list of DocumentType
     *
     * @throws DataBaseException SQL error
     * @throws DataValidationException invalid data
     */
    @Override
    public List<DocumentType> getAll()
            throws DataBaseException, DataValidationException {

        List<DocumentType> documentTypes = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;

        try (Connection connection = connector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            while (result.next())
                documentTypes.add(mapDataToObject(result, true));

        } catch (SQLException exception) {
            throw new DataBaseException("Error while getting all DocumentTypes", exception);
        }

        return documentTypes;
    }

    /**
     * Get DocumentType by id
     *
     * @param id DocumentType id
     * @param mapping use cache mapping system
     *
     * @return DocumentType object or null
     *
     * @throws DataBaseException SQL error
     * @throws DataValidationException invalid data
     */
    @Override
    public DocumentType getById(int id, boolean mapping)
            throws DataBaseException, DataValidationException {

        if (IDS_MAPPING_OBJECT.containsKey(id))
            return IDS_MAPPING_OBJECT.get(id);

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE id_ = ?";

        try (Connection connection = connector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next())
                    return mapDataToObject(result, mapping);
            }

        } catch (SQLException exception) {
            throw new DataBaseException("Error while getting DocumentType by id", exception);
        }

        return null;
    }

    /**
     * Get multiple DocumentType objects by ids
     *
     * @param ids list of ids
     * @param mapping use cache mapping system
     *
     * @return list of DocumentType
     *
     * @throws DataBaseException SQL error
     * @throws DataValidationException invalid data
     */
    @Override
    public List<DocumentType> getsByIds(List<Integer> ids, boolean mapping)
            throws DataBaseException, DataValidationException {

        List<DocumentType> documentTypes = new ArrayList<>();

        if (ids == null || ids.isEmpty())
            return documentTypes;

        List<Integer> uncachedIds = new ArrayList<>();
        for (Integer id : ids) {
            DocumentType cached = IDS_MAPPING_OBJECT.get(id);
            if (cached != null)
                documentTypes.add(cached);
            else
                uncachedIds.add(id);
        }

        if (uncachedIds.isEmpty())
            return documentTypes;

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM " + TABLE_NAME + " WHERE id_ IN (");
        for (int i = 0; i < uncachedIds.size(); i++)
            sql.append(i > 0 ? ",?" : "?");
        sql.append(")");

        try (Connection connection = connector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < uncachedIds.size(); i++)
                statement.setInt(i + 1, uncachedIds.get(i));

            try (ResultSet result = statement.executeQuery()) {
                while (result.next())
                    documentTypes.add(mapDataToObject(result, mapping));
            }

        } catch (SQLException exception) {
            throw new DataBaseException("Error while getting DocumentTypes by ids", exception);
        }

        return documentTypes;
    }

    /**
     * Insert a new DocumentType in database
     *
     * @param model DocumentType to insert
     *
     * @return true if inserted
     *
     * @throws DataBaseException SQL error
     * @throws DataValidationException invalid data
     */
    @Override
    public boolean insert(DocumentType model)
            throws DataBaseException, DataValidationException {

        String query = "INSERT INTO " + TABLE_NAME + " (name_) VALUES (?)";

        try (Connection connection = connector.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, model.getName());

            if (statement.executeUpdate() <= 0)
                return false;

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    model.setId(generatedId);
                    IDS_MAPPING_OBJECT.put(generatedId, model);
                }
            }

            return true;

        } catch (SQLException exception) {
            throw new DataBaseException("Error while inserting DocumentType", exception);
        }
    }

    @Override
    public boolean update(DocumentType model, DocumentType newModel)
            throws DataBaseException, DataValidationException {

        String query = "UPDATE " + TABLE_NAME + " SET name_ = ? WHERE id_ = ?";

        // FIX: Connection included in try-with-resources to prevent leaks
        try (Connection connection = connector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newModel.getName());
            statement.setInt(2, model.getId());

            if (statement.executeUpdate() > 0) {
                IDS_MAPPING_OBJECT.remove(model.getId());
                IDS_MAPPING_OBJECT.put(newModel.getId(), newModel);
                return true;
            }

            return false;

        } catch (SQLException exception) {
            throw new DataBaseException("Error while updating DocumentType", exception);
        }
    }

    public boolean updateFieldName(DocumentType model, String newName)
            throws DataBaseException {

        String query = "UPDATE " + TABLE_NAME + " SET name_ = ? WHERE id_ = ?";

        // FIX: Connection included in try-with-resources to prevent leaks
        try (Connection connection = connector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newName);
            statement.setInt(2, model.getId());

            if (statement.executeUpdate() > 0) {
                IDS_MAPPING_OBJECT.remove(model.getId());
                return true;
            }

            return false;

        } catch (SQLException exception) {
            throw new DataBaseException("Error while updating DocumentType name", exception);
        }
    }

    @Override
    public boolean delete(DocumentType model)
            throws DataBaseException {

        String query = "DELETE FROM " + TABLE_NAME + " WHERE id_ = ?";

        // FIX: Connection included in try-with-resources to prevent leaks
        try (Connection connection = connector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, model.getId());

            if (statement.executeUpdate() > 0) {
                IDS_MAPPING_OBJECT.remove(model.getId());
                return true;
            }

            return false;

        } catch (SQLException exception) {
            throw new DataBaseException("Error while deleting DocumentType", exception);
        }
    }

    @Override
    public boolean checkExist(DocumentType model)
            throws DataBaseException, DataValidationException {

        if (getById(model.getId(), false) == null)
            return insert(model);

        return true;
    }
}