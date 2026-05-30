package main.java.be.henallux.project.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.be.henallux.project.data.exception.DataBaseException;

import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.Document;
import main.java.be.henallux.project.model.DocumentDetails;
import main.java.be.henallux.project.model.DocumentType;
import main.java.be.henallux.project.model.Recipe;
import main.java.be.henallux.project.model.WorkFlow;

import main.java.be.henallux.project.model.exception.DataValidationException;

public class DocumentDA extends CRUD<Document>
{
    private static volatile DocumentDA instance;

    private final String TABLE_NAME = "Document_";

    private final HashMap<Object, Document> IDS_MAPPING_OBJECT = new HashMap<>();
    private final DetailDA detailDA;

    private DocumentDA() {
        detailDA = DetailDA.getInstance();
    }

    public static DocumentDA getInstance()
    {
        synchronized (DocumentDA.class) {
            if (instance == null)
                instance = new DocumentDA();
        }
        return instance;
    }

    @Override
    public Document mapDataToObject(ResultSet data, boolean mapping)
            throws DataBaseException, DataValidationException
    {
        try
        {
            int id = data.getInt("id_");

            if(IDS_MAPPING_OBJECT.containsKey(id))
                return IDS_MAPPING_OBJECT.get(id);

            LocalDate dateOfCreation = SQLDateToLocalDate(data.getDate("date_"));

            LocalDate plannedSendingDate = null;
            if(data.getDate("plannedSendingDate") != null)
                plannedSendingDate = SQLDateToLocalDate(data.getDate("plannedSendingDate"));

            LocalDate plannedReceiveDate = null;
            if(data.getDate("plannedReceiveDate") != null)
                plannedReceiveDate = SQLDateToLocalDate(data.getDate("plannedReceiveDate"));

            LocalDate effectiveSendingDate = null;
            if(data.getDate("effectiveSendingDate") != null)
                effectiveSendingDate = SQLDateToLocalDate(data.getDate("effectiveSendingDate"));

            LocalDate effectiveReceiveDate = null;
            if(data.getDate("effectiveReceiveDate") != null)
                effectiveReceiveDate = SQLDateToLocalDate(data.getDate("effectiveReceiveDate"));

            int paymentDelay = data.getInt("paymentDelay");

            String commentary = data.getString("commentary");

            boolean isChecked = data.getBoolean("isChecked");

            int workflowId = data.getInt("workflowId");
            int documentTypeId = data.getInt("documentTypeId");

            Integer addressId = data.getObject("addressId", Integer.class);

            WorkFlow workflow = WorkFlowDA.getInstance().getById(workflowId, false);

            DocumentType documentType =
                    DocumentTypeDA.getInstance().getById(documentTypeId, mapping);

            Address address = null;
            if(addressId != null)
                address = AddressDA.getInstance().getById(addressId, mapping);

            Document document = new Document(
                    id,
                    dateOfCreation,
                    documentType,
                    new DocumentDetails(),
                    isChecked,
                    plannedSendingDate,
                    plannedReceiveDate,
                    effectiveSendingDate,
                    effectiveReceiveDate,
                    paymentDelay,
                    workflow,
                    address,
                    commentary,
                    (Recipe) null
            );

            IDS_MAPPING_OBJECT.put(id, document);

            document.getWorkflow().addDocument(document);

            if (mapping)
                detailDA.getAll();

            return document;
        }
        catch(SQLException e)
        {
            throw new DataBaseException(
                    "Error while mapping Document data : " + e.getMessage()
            );
        }
    }

    @Override
    public List<Document> getAll()
            throws DataBaseException, DataValidationException
    {
        List<Document> documents = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME;

        try
        {
            Statement statement = connector.getConnection().createStatement();

            ResultSet result = statement.executeQuery(query);

            while(result.next())
            {
                documents.add(mapDataToObject(result, true));
            }

            return documents;
        }
        catch(SQLException e)
        {
            throw new DataBaseException(
                    "Error while getting all documents : " + e.getMessage()
            );
        }
    }

    @Override
    public Document getById(int id, boolean mapping)
            throws DataBaseException, DataValidationException
    {
        if(mapping && IDS_MAPPING_OBJECT.containsKey(id))
            return IDS_MAPPING_OBJECT.get(id);

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE id_ = ?";

        try
        {
            PreparedStatement statement =
                    connector.getConnection().prepareStatement(query);

            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            if(result.next())
                return mapDataToObject(result, mapping);

            return null;
        }
        catch(SQLException e)
        {
            throw new DataBaseException(
                    "Error while getting document by id : " + e.getMessage()
            );
        }
    }

    @Override
    public List<Document> getsByIds(List<Integer> ids, boolean mapping)
            throws DataBaseException, DataValidationException
    {
        List<Document> documents = new ArrayList<>();

        for(Integer id : ids)
        {
            Document document = getById(id, mapping);

            if(document != null)
                documents.add(document);
        }

        return documents;
    }

    @Override
    public boolean insert(Document document)
            throws DataBaseException, DataValidationException
    {
        String query =
                "INSERT INTO " + TABLE_NAME + " (" +
                        "date_, " +
                        "plannedSendingDate, " +
                        "plannedReceiveDate, " +
                        "effectiveSendingDate, " +
                        "effectiveReceiveDate, " +
                        "paymentDelay, " +
                        "commentary, " +
                        "isChecked, " +
                        "workflowId, " +
                        "documentTypeId, " +
                        "addressId" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try
        {
            PreparedStatement statement =
                    connector.getConnection().prepareStatement(
                            query,
                            Statement.RETURN_GENERATED_KEYS
                    );

            statement.setDate(1,
                    LocalDateToSQLDate(document.getDateOfCreation()));

            if(document.getPlannedSendDate() != null)
                statement.setDate(2,
                        LocalDateToSQLDate(document.getPlannedSendDate()));
            else
                statement.setNull(2, java.sql.Types.DATE);

            if(document.getPlannedDateOfReceipt() != null)
                statement.setDate(3,
                        LocalDateToSQLDate(document.getPlannedDateOfReceipt()));
            else
                statement.setNull(3, java.sql.Types.DATE);

            if(document.getActualSendDate() != null)
                statement.setDate(4,
                        LocalDateToSQLDate(document.getActualSendDate()));
            else
                statement.setNull(4, java.sql.Types.DATE);

            if(document.getActualDateOfReceipt() != null)
                statement.setDate(5,
                        LocalDateToSQLDate(document.getActualDateOfReceipt()));
            else
                statement.setNull(5, java.sql.Types.DATE);

            statement.setInt(6, document.getPaymentDelay());

            statement.setString(7, document.getComment());

            statement.setBoolean(8, document.getIsChecked());

            statement.setInt(9, document.getWorkflow().getId());

            statement.setInt(10, document.getDocumentType().getId());

            if(document.getAddress() != null)
                statement.setInt(11, document.getAddress().getAddressId());
            else
                statement.setNull(11, java.sql.Types.INTEGER);

            int affectedRows = statement.executeUpdate();

            if(affectedRows <= 0)
                return false;

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if(generatedKeys.next())
            {
                int generatedId = generatedKeys.getInt(1);

                document.setId(generatedId);

                IDS_MAPPING_OBJECT.put(
                        generatedId,
                        document
                );
            }

            return true;
        }
        catch(SQLException e)
        {
            throw new DataBaseException(
                    "Error while inserting document : " + e.getMessage()
            );
        }
    }

    @Override
    public boolean update(Document oldDocument, Document newDocument)
            throws DataBaseException, DataValidationException
    {
        String query =
                "UPDATE " + TABLE_NAME + " SET " +
                        "date_ = ?, " +
                        "plannedSendingDate = ?, " +
                        "plannedReceiveDate = ?, " +
                        "effectiveSendingDate = ?, " +
                        "effectiveReceiveDate = ?, " +
                        "paymentDelay = ?, " +
                        "commentary = ?, " +
                        "isChecked = ?, " +
                        "workflowId = ?, " +
                        "documentTypeId = ?, " +
                        "addressId = ? " +
                        "WHERE id_ = ?";

        try
        {
            PreparedStatement statement =
                    connector.getConnection().prepareStatement(query);

            statement.setDate(1,
                    LocalDateToSQLDate(newDocument.getDateOfCreation()));

            if(newDocument.getPlannedSendDate() != null)
                statement.setDate(2,
                        LocalDateToSQLDate(newDocument.getPlannedSendDate()));
            else
                statement.setNull(2, java.sql.Types.DATE);

            if(newDocument.getPlannedDateOfReceipt() != null)
                statement.setDate(3,
                        LocalDateToSQLDate(newDocument.getPlannedDateOfReceipt()));
            else
                statement.setNull(3, java.sql.Types.DATE);

            if(newDocument.getActualSendDate() != null)
                statement.setDate(4,
                        LocalDateToSQLDate(newDocument.getActualSendDate()));
            else
                statement.setNull(4, java.sql.Types.DATE);

            if(newDocument.getActualDateOfReceipt() != null)
                statement.setDate(5,
                        LocalDateToSQLDate(newDocument.getActualDateOfReceipt()));
            else
                statement.setNull(5, java.sql.Types.DATE);

            statement.setInt(6, newDocument.getPaymentDelay());

            statement.setString(7, newDocument.getComment());

            statement.setBoolean(8, newDocument.getIsChecked());

            statement.setInt(9, newDocument.getWorkflow().getId());

            statement.setInt(10, newDocument.getDocumentType().getId());

            if(newDocument.getAddress() != null)
                statement.setInt(11, newDocument.getAddress().getAddressId());
            else
                statement.setNull(11, java.sql.Types.INTEGER);

            statement.setInt(12, oldDocument.getId());

            int affectedRows = statement.executeUpdate();

            if(affectedRows > 0)
            {
                IDS_MAPPING_OBJECT.remove(oldDocument);
                IDS_MAPPING_OBJECT.put(newDocument.getId(), newDocument);
                return true;
            }

            return false;
        }
        catch(SQLException e)
        {
            throw new DataBaseException(
                    "Error while updating document : " + e.getMessage()
            );
        }
    }

    @Override
    public boolean delete(Document document)
            throws DataBaseException
    {
        String query = "DELETE FROM " + TABLE_NAME + " WHERE id_ = ?";

        try
        {
            PreparedStatement statement =
                    connector.getConnection().prepareStatement(query);

            statement.setInt(1, document.getId());

            int affectedRows = statement.executeUpdate();

            if(affectedRows > 0)
            {
                IDS_MAPPING_OBJECT.remove(document.getId());
                return true;
            }

            return false;
        }
        catch(SQLException e)
        {
            throw new DataBaseException(
                    "Error while deleting document : " + e.getMessage()
            );
        }
    }

    @Override
    public boolean checkExist(Document document)
            throws DataBaseException, DataValidationException
    {
        Document existingDocument = getById(document.getId());

        if(existingDocument != null)
            return true;

        return insert(document);
    }

    /*
     * ==========================
     * UPDATE FIELD METHODS
     * ==========================
     */

    private boolean updateField(int documentId, String field, Object value)
            throws DataBaseException
    {
        String query =
                "UPDATE " + TABLE_NAME +
                        " SET " + field + " = ? WHERE id_ = ?";

        try
        {
            PreparedStatement statement =
                    connector.getConnection().prepareStatement(query);

            statement.setObject(1, value);

            statement.setInt(2, documentId);

            return statement.executeUpdate() > 0;
        }
        catch(SQLException e)
        {
            throw new DataBaseException(
                    "Error while updating field " + field +
                            " : " + e.getMessage()
            );
        }
    }

    public boolean updateIsChecked(Document document, boolean isChecked)
            throws DataBaseException
    {
        return updateField(
                document.getId(),
                "isChecked",
                isChecked
        );
    }

    public boolean updateComment(Document document, String comment)
            throws DataBaseException
    {
        return updateField(
                document.getId(),
                "commentary",
                comment
        );
    }

    public boolean updatePlannedSendDate(Document document, LocalDate date)
            throws DataBaseException
    {
        return updateField(
                document.getId(),
                "plannedSendingDate",
                LocalDateToSQLDate(date)
        );
    }

    public boolean updateActualSendDate(Document document, LocalDate date)
            throws DataBaseException
    {
        return updateField(
                document.getId(),
                "effectiveSendingDate",
                LocalDateToSQLDate(date)
        );
    }

    public boolean updatePlannedReceiveDate(Document document, LocalDate date)
            throws DataBaseException
    {
        return updateField(
                document.getId(),
                "plannedReceiveDate",
                LocalDateToSQLDate(date)
        );
    }

    public boolean updateActualReceiveDate(Document document, LocalDate date)
            throws DataBaseException
    {
        return updateField(
                document.getId(),
                "effectiveReceiveDate",
                LocalDateToSQLDate(date)
        );
    }

    public boolean updateWorkflow(Document document, WorkFlow workflow)
            throws DataBaseException
    {
        return updateField(
                document.getId(),
                "workflowId",
                workflow.getId()
        );
    }

    public boolean updateAddress(Document document, Address address)
            throws DataBaseException
    {
        return updateField(
                document.getId(),
                "addressId",
                address.getAddressId()
        );
    }
}
