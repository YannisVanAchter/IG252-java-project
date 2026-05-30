package main.java.be.henallux.project.model;

import main.java.be.henallux.project.data.AddressDA;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.util.List;
import java.util.Optional;

public class WorkFlowTypeRepository {

    public static volatile WorkFlowTypeRepository INSTANCE;
    private final List<WorkFlowType> TYPES;

    public WorkFlowTypeRepository() {
        try {
            TYPES = List.of(
                    new WorkFlowType(1, "WorkFlowType1", true, false, false),
                    new WorkFlowType(2, "WorkFlowType2", false, true, false),
                    new WorkFlowType(3, "WorkFlowType3", false, false, true)
            );
        } catch (DataValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public static WorkFlowTypeRepository getInstance() {
        if (INSTANCE == null) {
            setInstance(new WorkFlowTypeRepository());
        }
        return INSTANCE;
    }

    private static void setInstance(WorkFlowTypeRepository workFlowTypeRepository) {
        INSTANCE = workFlowTypeRepository;
    }

    // Research by boolean flag
    public Optional<WorkFlowType> findBuyType() {
        return TYPES.stream()
                .filter(WorkFlowType::getIsBuy)
                .findFirst();
    }

    public Optional<WorkFlowType> findSellType() {
        return TYPES.stream()
                .filter(WorkFlowType::getIsSell)
                .findFirst();
    }

    public Optional<WorkFlowType> findInternalType() {
        return TYPES.stream()
                .filter(WorkFlowType::getIsInternal)
                .findFirst();
    }

    // All WorkFlowTypes
    public List<WorkFlowType> getWorkFLowTypes() {
        return TYPES;
    }
}
