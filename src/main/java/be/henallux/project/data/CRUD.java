package main.java.be.henallux.project.data;

import java.sql.ResultSet;
import java.sql.Date;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.print.attribute.PrintServiceAttributeSet;

import main.java.be.henallux.project.data.exception.DataBaseException;

import main.java.be.henallux.project.model.Model;
import main.java.be.henallux.project.model.exception.DataValidationException;

public abstract class CRUD<Model> {
    CRUD<Model> instance;
    String TABLE_NAME;
    Map<Object, Model> IDS_MAPPING_OBJECT;

    abstract Model mapDataToObject(ResultSet data, boolean mapping) throws DataBaseException, DataValidationException;

    abstract List<Model> getAll() throws DataBaseException, DataValidationException;

    abstract Model getById(int id, boolean mapping) throws DataBaseException, DataValidationException;

    abstract Model getById(int id) throws DataBaseException, DataValidationException;

    abstract List<Model> getsByIds(List<Integer> ids, boolean mapping) throws DataBaseException, DataValidationException;

    abstract List<Model> getsByIds(List<Integer> ids) throws DataBaseException, DataValidationException;

    abstract boolean insert(Model model) throws DataBaseException, DataValidationException;

    abstract boolean update(Model model) throws DataBaseException;

    abstract boolean delete(Model model) throws DataBaseException;

    /**
     * Check if a given instance of a Model exist in the database
     * @param model the model to evaluate
     * @effect If the model does not exist in the DB, it will insert the object
     */
    abstract boolean checkExist(Model model) throws DataBaseException, DataValidationException;

    public LocalDate SQLDateToLocalDate(Date d) {
        return d.toLocalDate();
    }

    public Date LocalDateToSQLDate(LocalDate d) {
        return Date.valueOf(d);
    }
}
