package main.java.be.henallux.project.data;

import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import main.java.be.henallux.project.data.exception.DataBaseException;

import main.java.be.henallux.project.model.Model;

public abstract class CRUD<Model> {
    public CRUD<Model> instance;
    public String tableName;
    public Map<String, Model> dataMappingObject;
    static public final Map<Class<Model>, CRUD<Model>> dataMappingModel;

    public CRUD() {
        if (this.dataMappingModel == null)
            this.dataMappingModel = new HashMap<>();
    }

    abstract public CRUD<Model> getInstance();

    abstract public Model mapDataToObject(ResultSet data, boolean mapping);

    abstract public List<Model> getAll() throws DataBaseException;

    abstract public Model getById(int id, boolean mapping) throws DataBaseException;

    abstract public Model getById(int id) throws DataBaseException;

    abstract public List<Model> getsByIds(List<Integer> ids, boolean mapping) throws DataBaseException;

    abstract public List<Model> getsByIds(List<Integer> ids) throws DataBaseException;

    abstract public boolean insert(Model model) throws DataBaseException;

    abstract public boolean update(Model model) throws DataBaseException;

    abstract public boolean delete(Model model) throws DataBaseException;

    abstract public boolean checkExist(Model model) throws DataBaseException;
}
