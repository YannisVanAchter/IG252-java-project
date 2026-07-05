package be.henallux.project.model;

import be.henallux.project.data.exception.DataBaseException;
import be.henallux.project.model.exception.DataValidationException;

import java.util.List;
import java.util.Optional;

public class WorkFlowStatusRepository {
    public static volatile WorkFlowStatusRepository INSTANCE;
    private final List<Status> TYPES;

    public WorkFlowStatusRepository() {
        try {
            TYPES = List.of(
                    new Status("Pending")
            );
        } catch (DataValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public static WorkFlowStatusRepository getInstance() {
        if (INSTANCE == null) {
            setInstance(new WorkFlowStatusRepository());
        }
        return INSTANCE;
    }

    private static void setInstance(WorkFlowStatusRepository workFlowStatusRepository) {
        INSTANCE = workFlowStatusRepository;
    }


    // Research by type name
    public Optional<Status> findByName(String name) {
        return TYPES.stream()
                .filter(t -> t.getName().equals(name))
                .findFirst();
    }

    // All documentTypes
    public List<Status> getWorkFlowStatuses() {
        return TYPES;
    }
}
