package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class EmployeeManager {
    public List<Employee> getAllEmployees() throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        return employeeDataAccess.getAllEmployees();
    }

    public Employee getEmployee(int employeeID) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        return employeeDataAccess.getEmployee(employeeID);
    }

    public void changeAddress(int employeeID, Address address) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        employeeDataAccess.changeAddress(employeeID, address);
    }

    public void changePassword(int employeeID, String password) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        employeeDataAccess.changePassword(employeeID, password);
    }

    public void changeManager(int employeeID, Employee employee) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        employeeDataAccess.changeManager(employeeID, employee);
    }

    public void changeBankAccount(int employeeID, String IBAN) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        employeeDataAccess.changeBankAccount(employeeID, IBAN);
    }

    public void changeHoursSalary(int employeeID, double Salary) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        employeeDataAccess.changeHoursSalary(employeeID, Salary);
    }

    public void changeHalfDayBreak(int employeeID, int nbHalfDay) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        employeeDataAccess.changeHalfDayBreak(employeeID, nbHalfDay);
    }



    public void assignPosition(int employeeID, Position position) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        employeeDataAccess.assignPosition(employeeID, position);
    }

    public void addPosition(Position position) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        employeeDataAccess.addPosition(position);
    }

    public void revokePosition(int employeeID, Position position) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        employeeDataAccess.revokePosition(employeeID, position);
    }

    public void checkIn(int employeeID) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        employeeDataAccess.checkIn(employeeID);
    }

    public void checkOut(int employeeID) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        employeeDataAccess.checkOut(employeeID);
    }

    public void addNonAttendance(int employeeID, NonAttendanceType type, String description) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        employeeDataAccess.addNonAttendance(employeeID, type, description);
    }

    public void addNonAttendance(int employeeID, NonAttendanceType type, String description, LocalDate startDate) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        employeeDataAccess.addNonAttendance(employeeID, type, description, startDate);
    }

    public void addNonAttendance(int employeeID, NonAttendanceType type, String description, LocalDate startDate, LocalDate enDate) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        employeeDataAccess.addNonAttendance(employeeID, type, description, startDate, enDate);
    }

    public void changeNonAttendance(int employeeID, LocalDate startDate, LocalDate endDate) throws DataBaseException {
        EmployeeDataAccess employeeDataAccess = new EmployeeDataAccess();
        employeeDataAccess.changeNonAttendance(employeeID, startDate, endDate);
    }
}
