package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;
import java.util.Map;
import java.io.ObjectInputFilter.Status;
import java.util.ArrayList;

public class WorkFlowManager {
    
    public List<WorkFlow> getAllWorkFlows() throws DataBaseException {
        return new WorkFlowData().getAllWorkFlows();
    }

    public List<WorkFlow> getAllBuying() throws DataBaseException {
        return new WorkFlowData().getAllBuying();
    }

    public List<WorkFlow> getAllInternal() throws DataBaseException {
        return new WorkFlowData().getAllInternal();
    }

    public List<WorkFlow> getAllSelling() throws DataBaseException {
        return new WorkFlowData().getAllSelling();
    }

    public List<WorkFlowType> getWorkFlowType() throws DataBaseException {
        return new WorkFlowData().getWorkFlowType();
    }

    public void addWorkFlowType(WorkFlowType workFlowType) throws DataBaseException {
        new WorkFlowData().addWorkFlowType(workFlowType);
    }

    public void addWorkFlow(WorkFlow workFlow) throws DataBaseException {
        new WorkFlowData().addWorkFlow(workFlow);
    }

    public void changeStatus(int WorkFlowId, Status status) throws DataBaseException {
        new WorkFlowData().changeStatus(WorkFlowId, status);
    }

    public void addDocument(int workFlowId, Document document) throws DataBaseException {
        new WorkFlowData().addDocument(workFlowId, document);
    }
}
