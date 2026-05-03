package model;

import exception.DataValidationException;

/**
 * A workflow type define if the linked workflow is a buy, sell or internal workflow.
 * It can not be two or more of these at the same time.
 */
public class WorkFlowType {
    private String name;
    private boolean isBuy = false;
    private boolean isSell = false;
    private boolean isInternal = false;

    public WorkFlowType(String name, boolean isBuy, boolean isSell, boolean isInternal) throws DataValidationException {
        setName(name);
        setIsBuy(isBuy);
        setIsSell(isSell);
        setIsInternal(isInternal);

        check();
    }

    private void check() throws DataValidationException {
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

    public String getName() { return name; }

    private void setName(String name) throws DataValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new DataValidationException("Workflow type name cannot be null or empty.");
        }
        this.name = name;
    }

    public boolean getIsBuy() { return isBuy; }

    private void setIsBuy(boolean isBuy) throws DataValidationException {
        if (getIsSell() || getIsInternal()) {
            throw new DataValidationException("Cannot set isBuy to true when isSell or isInternal is already true.");
        }
        this.isBuy = isBuy;
    }

    public boolean getIsSell() { return isSell; }

    private void setIsSell(boolean isSell) throws DataValidationException {
        if (getIsBuy() || getIsInternal()) {
            throw new DataValidationException("Cannot set isSell to true when isBuy or isInternal is already true.");
        }
        this.isSell = isSell;
    }

    public boolean getIsInternal() { return isInternal; }

    private void setIsInternal(boolean isInternal) throws DataValidationException {
        if (getIsBuy() || getIsSell()) {
            throw new DataValidationException("Cannot set isInternal to true when isBuy or isSell is already true.");
        }
        this.isInternal = isInternal;
    }

    @Override
    public String toString() {
        return "WorkFlowType{name='" + name + "', isBuy=" + isBuy + ", isSell=" + isSell + ", isInternal=" + isInternal + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        WorkFlowType other = (WorkFlowType) obj;
        return name.equals(other.getName()) && isBuy == other.getIsBuy() && isSell == other.getIsSell() && isInternal == other.getIsInternal();
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (isBuy ? 1 : 0);
        result = 31 * result + (isSell ? 1 : 0);
        result = 31 * result + (isInternal ? 1 : 0);
        return result;
    }
}
