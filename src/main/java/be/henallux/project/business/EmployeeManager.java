/*
package be.henallux.project.business;

import be.henallux.project.data.EmployeeDA;
import be.henallux.project.data.exception.DataBaseException;
import be.henallux.project.business.exception.BusinessException;
import be.henallux.project.model.Employee;
import be.henallux.project.model.Address;
import be.henallux.project.model.Position;
import be.henallux.project.model.NonAttendanceType;
import java.time.LocalDate;
import java.util.List;

public class EmployeeManager {

    private final EmployeeDA employeeDA;

    public EmployeeManager() {
        this.employeeDA = EmployeeDA.getInstance();
    }

    public List<Employee> getAllEmployees() throws BusinessException {
        try {
            return employeeDA.getAllEmployees();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving employees.", e);
        }
    }

    public Employee getEmployee(int employeeID) throws BusinessException {
        try {
            return employeeDA.getEmployee(employeeID);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the employee.", e);
        }
    }

    public void changeAddress(int employeeID, Address address) throws BusinessException {
        if (address == null) {
            throw new BusinessException("The address cannot be null.");
        }
        try {
            employeeDA.changeAddress(employeeID, address);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the address.", e);
        }
    }

    public void changePassword(int employeeID, String password) throws BusinessException {
        if (password == null || password.isBlank()) {
            throw new BusinessException("The password cannot be null or empty.");
        }
        try {
            employeeDA.changePassword(employeeID, password);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the password.", e);
        }
    }

    public void changeManager(int employeeID, Employee employee) throws BusinessException {
        if (employee == null) {
            throw new BusinessException("The employee cannot be null.");
        }
        try {
            employeeDA.changeManager(employeeID, employee);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the manager.", e);
        }
    }

    public void changeBankAccount(int employeeID, String IBAN) throws BusinessException {
        if (IBAN == null || IBAN.isBlank()) {
            throw new BusinessException("The IBAN cannot be null or empty.");
        }
        try {
            employeeDA.changeBankAccount(employeeID, IBAN);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the bank account.", e);
        }
    }

    public void changeHoursSalary(int employeeID, double Salary) throws BusinessException {
        if (Salary <= 0) {
            throw new BusinessException("The hourly salary must be a positive number.");
        }
        try {
            employeeDA.changeHoursSalary(employeeID, Salary);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the hourly salary.", e);
        }
    }

    public void changeHalfDayBreak(int employeeID, int nbHalfDay) throws BusinessException {
        if (nbHalfDay < 0) {
            throw new BusinessException("The number of half-days off must be a positive number.");
        }
        try {
            employeeDA.changeHalfDayBreak(employeeID, nbHalfDay);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the number of half-days off.", e);
        }
    }



    public void assignPosition(int employeeID, Position position) throws BusinessException {
        if (position == null) {
            throw new BusinessException("The position cannot be null.");
        }
        try {
            employeeDA.assignPosition(employeeID, position);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when assigning the position.", e);
        }
    }

    public void addPosition(Position position) throws BusinessException {
        if (position == null) {
            throw new BusinessException("The position cannot be null.");
        }
        try {
            employeeDA.addPosition(position);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when adding the position.", e);
        }
    }

    public void revokePosition(int employeeID, Position position) throws BusinessException {
        if (position == null) {
            throw new BusinessException("The position cannot be null.");
        }
        try {
            employeeDA.revokePosition(employeeID, position);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when revoking the position.", e);
        }
    }

    public void checkIn(int employeeID) throws BusinessException {
        if (employeeID <= 0) {
            throw new BusinessException("The employee ID must be a positive number.");
        }
        try {
            if (employeeDA.isCheckedIn(employeeID)) {
                throw new BusinessException("The employee is already checked in.");
            }
            employeeDA.checkIn(employeeID);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when checking in the employee.", e);
        }
    }

    public void checkOut(int employeeID) throws BusinessException {
        if (employeeID <= 0) {
            throw new BusinessException("The employee ID must be a positive number.");
        }
        if (!employeeDA.isCheckedIn(employeeID)) {
            throw new BusinessException("The employee is not checked in.");
        }
        try {
            employeeDA.checkOut(employeeID);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when checking out the employee.", e);
        }
    }

    private void validateNonAttendanceBase(int employeeID, NonAttendanceType type, String description) throws BusinessException {
        if (employeeID <= 0) {
            throw new BusinessException("The employee ID must be a positive number.");
        }
        if (type == null) {
            throw new BusinessException("The non-attendance type cannot be null.");
        }
        if (description == null || description.isBlank()) {
            throw new BusinessException("The description cannot be null or blank.");
        }
    }


    public void addNonAttendance(int employeeID, NonAttendanceType type, String description) throws BusinessException {
        validateNonAttendanceBase(employeeID, type, description);
        try {
            employeeDA.addNonAttendance(employeeID, type, description);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when adding the non-attendance.", e);
        }
    }

    public void addNonAttendance(int employeeID, NonAttendanceType type, String description, LocalDate startDate) throws BusinessException {
        validateNonAttendanceBase(employeeID, type, description);
        if (startDate == null) {
            throw new BusinessException("The start date cannot be null.");
        }
        try {
            employeeDA.addNonAttendance(employeeID, type, description, startDate);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when adding the non-attendance.", e);
        }
    }

    public void addNonAttendance(int employeeID, NonAttendanceType type, String description, LocalDate startDate, LocalDate endDate) throws BusinessException {
        validateNonAttendanceBase(employeeID, type, description);
        if (startDate == null) {
            throw new BusinessException("The start date cannot be null.");
        }
        if (endDate == null) {
            throw new BusinessException("The end date cannot be null.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("The start date must be before the end date.");
        }
        try {
            employeeDA.addNonAttendance(employeeID, type, description, startDate, endDate);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when adding the non-attendance.", e);
        }
    }

    public void changeNonAttendance(int employeeID, LocalDate startDate, LocalDate endDate) throws BusinessException {
        if (employeeID <= 0) {
            throw new BusinessException("The employee ID must be a positive number.");
        }
        if (startDate == null) {
            throw new BusinessException("The start date cannot be null.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("The start date must be before the end date.");
        }
        if (endDate == null) {
            throw new BusinessException("The end date cannot be null.");
        }
        if (endDate.isBefore(startDate)) {
            throw new BusinessException("The end date must be after the start date.");
        }
        try {
            employeeDA.changeNonAttendance(employeeID, startDate, endDate);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the non-attendance.", e);
        }
    }
}
*/