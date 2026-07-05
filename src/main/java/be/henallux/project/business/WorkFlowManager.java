package be.henallux.project.business;
import be.henallux.project.data.WorkFlowDA;
import be.henallux.project.data.exception.DataBaseException;
import be.henallux.project.business.exception.BusinessException;

import be.henallux.project.model.*;
import be.henallux.project.model.exception.DataValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.List;
import java.util.stream.Collectors;

public class WorkFlowManager {

    // quand une maj est réalisée sur l'objet, il faut incrémenter un compteur de version.

    private final WorkFlowDA workFlowDA;
    private final DocumentManager documentManager;

    public WorkFlowManager() {
        this.workFlowDA = WorkFlowDA.getInstance();
        this.documentManager = new DocumentManager();
    }

    public List<WorkFlow> getAllWorkFlows() throws BusinessException, DataValidationException {
        try {
            return workFlowDA.getAll();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the workflows.", e);
        }
    }

    public List<WorkFlow> getAllBuying() throws BusinessException, DataValidationException {
        List<WorkFlow> workFlows = getAllWorkFlows();
        return workFlows.stream().filter(w -> w.getWorkflowType().getIsBuy()).toList();
    }

    public List<WorkFlow> getAllInternal() throws BusinessException, DataValidationException {
        List<WorkFlow> workFlows = getAllWorkFlows();
        return workFlows.stream().filter(w -> w.getWorkflowType().getIsInternal()).toList();
    }

    public List<WorkFlow> getAllSelling() throws BusinessException, DataValidationException {
        List<WorkFlow> workFlows = getAllWorkFlows();
        return workFlows.stream().filter(w -> w.getWorkflowType().getIsSell()).toList();
    }

    public List<WorkFlowType> getWorkFlowType() {
        return WorkFlowTypeRepository.getInstance().getWorkFLowTypes();
    }

    public void createWorkFlow(WorkFlow workFlow) throws BusinessException, DataValidationException {
        if (workFlow == null) {
            throw new BusinessException("The workflow cannot be null.");
        }
        if (!workFlow.getUs().getIsUs()) {
            throw new BusinessException("The workFLow Us is not an Us clientSupplier.");
        }
        if (workFlow.getOtherParty().getIsUs()) {
            throw new BusinessException("The other party cannot be an Us clientSupplier.");
        }
        if (!(workFlow.getOtherParty().getIsClient() || workFlow.getOtherParty().getIsSupplier())) {
            throw new BusinessException("The other party must be an client or a supplier.");
        }
        if (workFlow.getOtherParty() == workFlow.getUs()) {
            throw new BusinessException("The other party and Us cannot be the same clientSupplier.");
        }
        try {
            for (Document document : workFlow.getDocuments()) {
                for (Detail detail : document.getDetails()) {
                    Product product = detail.getProduct();
                    List<Discount> discountList = product.getDiscounts();
                    BigDecimal currentDiscountPercentage = BigDecimal.ZERO;
                    if (workFlow.getWorkflowType().getIsSell()) {
                        for (Discount discount : discountList) {
                            LocalDate now = LocalDate.now();
                            boolean isDateValid = now.isAfter(discount.getStartDate())
                                    && now.isBefore(discount.getEndDate());
                            boolean isQuantityValid = (detail.getQuantity() > discount.getRequiredQuantity());

                            if (isDateValid && isQuantityValid) {
                                currentDiscountPercentage = discount.getDiscountPercentage();
                            }
                        }
                    }
                    BigDecimal quantity = BigDecimal.valueOf(detail.getQuantity());
                    BigDecimal hundred = BigDecimal.valueOf(100);
                    BigDecimal discountFactor = hundred.subtract(currentDiscountPercentage)
                            .divide(hundred);

                    BigDecimal resultDpEVAT = quantity
                            .multiply(product.getPriceEVAT())
                            .multiply(discountFactor);

                    detail.setPriceEVAT(resultDpEVAT.doubleValue());
                    detail.setVat(product.getVat());
                    detail.setFidelityPointEarned(product.getFidelityPoint() * detail.getQuantity());
                }
            }
            workFlowDA.insert(workFlow);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when adding the workflow.", e);
        }
    }

    public void changeStatus(int workFlowId, Status status) throws BusinessException, DataValidationException {
        if (workFlowId < 0) {
            throw new BusinessException("The workflowId cannot be null.");
        }
        if (status == null) {
            throw new BusinessException("The status cannot be null.");
        }
        try {
            WorkFlow workFlow = workFlowDA.getById(workFlowId);
            workFlowDA.updateFieldStatus(workFlow, status);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the status.", e);
        }
    }

    public void addDocument(int workFlowId, Document document) throws BusinessException, DataValidationException {
        if (document == null) {
            throw new BusinessException("The document cannot be null.");
        }
        documentManager.createDocument(document);
    }
}
