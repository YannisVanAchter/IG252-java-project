package be.henallux.project.data;

import java.sql.ResultSet;
import java.sql.Date;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

import be.henallux.project.data.exception.DataBaseException;

import be.henallux.project.model.Model;
import be.henallux.project.model.exception.DataValidationException;

public abstract class CRUD<M extends Model> {
    protected static volatile CRUD<?> instance;
    protected String TABLE_NAME;
    protected Map<Object, M> IDS_MAPPING_OBJECT;
    public final static MySQLConnector connector = MySQLConnector.getInstance();

    abstract M mapDataToObject(ResultSet data, boolean mapping) throws DataBaseException, DataValidationException;

    abstract List<M> getAll() throws DataBaseException, DataValidationException;

    abstract M getById(int id, boolean mapping) throws DataBaseException, DataValidationException;

    public M getById(int id) throws DataBaseException, DataValidationException {
        return this.getById(id, true);
    }

    abstract List<M> getsByIds(List<Integer> ids, boolean mapping) throws DataBaseException, DataValidationException;

    public List<M> getsByIds(List<Integer> ids) throws DataBaseException, DataValidationException {
        return this.getsByIds(ids, true);
    }

    abstract boolean insert(M model) throws DataBaseException, DataValidationException;

    abstract boolean update(M model, M newModel) throws DataBaseException, DataValidationException;

    abstract boolean delete(M model) throws DataBaseException, DataValidationException;

    /**
     * Check if a given instance of a Model exists in the database
     * @param model the model to evaluate
     * effect If the model does not exist in the DB, it will insert the object
     */
    abstract boolean checkExist(M model) throws DataBaseException, DataValidationException;

    public LocalDate SQLDateToLocalDate(Date d) {
        return d.toLocalDate();
    }

    public Date LocalDateToSQLDate(LocalDate d) {
        return Date.valueOf(d);
    }
}
