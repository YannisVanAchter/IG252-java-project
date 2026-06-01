package main.java.be.henallux.project.data;

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

    /**
     * Singleton instance
     */
    private static volatile DocumentTypeDA instance;

    /**
     * Constructor
     */
    private DocumentTypeDA() throws DataBaseException, DataValidationException {
        TABLE_NAME = "DocumentType";
        IDS_MAPPING_OBJECT = new HashMap<>();

        DocumentTypeRepository documentTypeRepository = DocumentTypeRepository.getInstance();
        for (DocumentType docType: documentTypeRepository.getDocumentTypes())
            this.checkExist(docType);
    }

    /**
     * Get singleton instance
     *
     * @return DocumentTypeDA instance
     */
    public static DocumentTypeDA getInstance() {
        synchronized (DocumentTypeDA.class) {
            try {
                if (instance == null) {
                    instance = new DocumentTypeDA();
                }
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

            /*
             * Return mapped object if already loaded
             */
            if (IDS_MAPPING_OBJECT.containsKey(id)) {
                return IDS_MAPPING_OBJECT.get(id);
            }

            String name = data.getString("name_");

            DocumentType documentType = new DocumentType(id, name);

            IDS_MAPPING_OBJECT.put(id, documentType);

            return documentType;

        } catch (SQLException exception) {
            throw new DataBaseException(
                    "Error while mapping DocumentType data",
                    exception
            );
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

        try (
                Statement statement = connector.getConnection().createStatement();
                ResultSet result = statement.executeQuery(query)
        ) {

            while (result.next()) {
                documentTypes.add(mapDataToObject(result, true));
            }

        } catch (SQLException exception) {
            throw new DataBaseException(
                    "Error while getting all DocumentTypes",
                    exception
            );
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

        if (mapping && IDS_MAPPING_OBJECT.containsKey(id)) {
            return IDS_MAPPING_OBJECT.get(id);
        }

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE id_ = ?";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return mapDataToObject(result, mapping);
                }
            }

        } catch (SQLException exception) {
            throw new DataBaseException(
                    "Error while getting DocumentType by id",
                    exception
            );
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

        for (Integer id : ids) {
            DocumentType documentType = getById(id, mapping);

            if (documentType != null) {
                documentTypes.add(documentType);
            }
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

        if (checkExist(model)) {
            return false;
        }

        String query = "INSERT INTO " + TABLE_NAME + " (name_) VALUES (?)";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(
                                query,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setString(1, model.getName());

            int affectedRows = statement.executeUpdate();

            if (affectedRows <= 0) {
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

        } catch (SQLException exception) {
            throw new DataBaseException(
                    "Error while inserting DocumentType",
                    exception
            );
        }
    }

    /**
     * Update an existing DocumentType
     *
     * @param model old model
     * @param newModel new model values
     *
     * @return true if updated
     *
     * @throws DataBaseException SQL error
     * @throws DataValidationException invalid data
     */
    @Override
    public boolean update(DocumentType model, DocumentType newModel)
            throws DataBaseException, DataValidationException {

        String query =
                "UPDATE " + TABLE_NAME +
                        " SET name_ = ?" +
                        " WHERE id_ = ?";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setString(1, newModel.getName());
            statement.setInt(2, model.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {

                IDS_MAPPING_OBJECT.remove(model.getId());
                IDS_MAPPING_OBJECT.put(newModel.getId(), newModel);

                return true;
            }

            return false;

        } catch (SQLException exception) {
            throw new DataBaseException(
                    "Error while updating DocumentType",
                    exception
            );
        }
    }

    /**
     * Update only name field
     *
     * @param model target model
     * @param newName new name value
     *
     * @return true if updated
     *
     * @throws DataBaseException SQL error
     */
    public boolean updateFieldName(DocumentType model, String newName)
            throws DataBaseException {

        String query =
                "UPDATE " + TABLE_NAME +
                        " SET name_ = ?" +
                        " WHERE id_ = ?";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setString(1, newName);
            statement.setInt(2, model.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {

                IDS_MAPPING_OBJECT.remove(model.getId());

                return true;
            }

            return false;

        } catch (SQLException exception) {
            throw new DataBaseException(
                    "Error while updating DocumentType name",
                    exception
            );
        }
    }

    /**
     * Delete a DocumentType
     *
     * @param model model to delete
     *
     * @return true if deleted
     *
     * @throws DataBaseException SQL error
     */
    @Override
    public boolean delete(DocumentType model)
            throws DataBaseException {

        String query =
                "DELETE FROM " + TABLE_NAME +
                        " WHERE id_ = ?";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, model.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {

                IDS_MAPPING_OBJECT.remove(model.getId());

                return true;
            }

            return false;

        } catch (SQLException exception) {
            throw new DataBaseException(
                    "Error while deleting DocumentType",
                    exception
            );
        }
    }

    /**
     * Check if a DocumentType already exists in database
     *
     * @param model model to evaluate
     *
     * @return true if exists
     *
     * @throws DataBaseException SQL error
     */
    @Override
    public boolean checkExist(DocumentType model)
            throws DataBaseException, DataValidationException {
        if (getById(model.getId(), false) == null)
            return insert(model);
        return true;
    }
}