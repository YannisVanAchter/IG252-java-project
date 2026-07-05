package be.henallux.project.model;

import be.henallux.project.model.exception.DataValidationException;

/**
 * A workflow type defines if the linked workflow is a buy, sell, or internal workflow.
 * It cannot be two or more of these at the same time.
 */
public class WorkFlowType implements Model {
    private int id;
    private String name;
    private boolean isBuy = false;
    private boolean isSell = false;
    private boolean isInternal = false;

    public WorkFlowType(int id, String name, boolean isBuy, boolean isSell, boolean isInternal) throws DataValidationException {
        setId(id);
        setName(name);
        setIsBuy(isBuy);
        setIsSell(isSell);
        setIsInternal(isInternal);

        check();
    }

    private void check() throws DataValidationException {
        if (!isBuy && !isSell && !isInternal) {
            throw new DataValidationException("A workflow type must be at least one of: buy, sell, internal.");
        }
        if (isBuy && isSell) {
            throw new DataValidationException("A workflow type cannot be both buy and sell.");
        }
        if (isBuy && isInternal) {
            throw new DataValidationException("A workflow type cannot be both buy and internal.");
        }
        if (isSell && isInternal) {
            throw new DataValidationException("A workflow type cannot be both sell and internal.");
        }
    }

    public int getId() { return id; }

    private void setId(int id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) throws DataValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new DataValidationException("Workflow type name cannot be null or empty.");
        }
        this.name = name;
    }

    public boolean getIsBuy() { return isBuy; }

    private void setIsBuy(boolean isBuy) {
        this.isBuy = isBuy;
    }

    public boolean getIsSell() { return isSell; }

    private void setIsSell(boolean isSell) {
        this.isSell = isSell;
    }

    public boolean getIsInternal() { return isInternal; }

    private void setIsInternal(boolean isInternal) {
        this.isInternal = isInternal;
    }

    public String getLabel() { return getName(); }

    @Override
    public String toString() {
        return "WorkFlowType{name='" + name + "', isBuy=" + isBuy + ", isSell=" + isSell + ", isInternal=" + isInternal + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        WorkFlowType other = (WorkFlowType) obj;
        return this.id == other.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
