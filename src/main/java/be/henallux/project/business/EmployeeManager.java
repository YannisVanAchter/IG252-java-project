package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;

public class EmployeeManager {

    private final EmployeeDataAccess employeeDataAccess;

    public EmployeeManager(EmployeeDataAccess employeeDataAccess) {
        this.employeeDataAccess = employeeDataAccess;
    }

    public List<Employee> getAllEmployees() throws BusinessException {
        try {
            return employeeDataAccess.getAllEmployees();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des employés.", e);
        }
    }

    public Employee getEmployee(int employeeID) throws BusinessException {
        try {
            return employeeDataAccess.getEmployee(employeeID);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération de l'employé.", e);
        }
    }

    public void changeAddress(int employeeID, Address address) throws BusinessException {
        if (address == null) {
            throw new BusinessException("L'adresse ne peut pas être nulle.");
        }
        try {
            employeeDataAccess.changeAddress(employeeID, address);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement d'adresse.", e);
        }
    }

    public void changePassword(int employeeID, String password) throws BusinessException {
        if (password == null || password.isBlank()) {
            throw new BusinessException("Le mot de passe ne peut pas être nul ou vide.");
        }
        try {
            employeeDataAccess.changePassword(employeeID, password);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement de mot de passe.", e);
        }
    }

    public void changeManager(int employeeID, Employee employee) throws BusinessException {
        if (employee == null) {
            throw new BusinessException("L'employé ne peut pas être nul.");
        }
        try {
            employeeDataAccess.changeManager(employeeID, employee);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement de manager.", e);
        }
    }

    public void changeBankAccount(int employeeID, String IBAN) throws BusinessException {
        if (IBAN == null || IBAN.isBlank()) {
            throw new BusinessException("Le IBAN ne peut pas être nul ou vide.");
        }
        try {
            employeeDataAccess.changeBankAccount(employeeID, IBAN);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement de compte bancaire.", e);
        }
    }

    public void changeHoursSalary(int employeeID, double Salary) throws BusinessException {
        if (Salary <= 0) {
            throw new BusinessException("Le salaire horaire doit être un nombre positif.");
        }
        try {
            employeeDataAccess.changeHoursSalary(employeeID, Salary);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement du salaire horaire.", e);
        }
    }

    public void changeHalfDayBreak(int employeeID, int nbHalfDay) throws BusinessException {
        if (nbHalfDay < 0) {
            throw new BusinessException("Le nombre de demi-journées de pause doit être un nombre positif.");
        }
        try {
            employeeDataAccess.changeHalfDayBreak(employeeID, nbHalfDay);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement du nombre de demi-journées de pause.", e);
        }
    }



    public void assignPosition(int employeeID, Position position) throws BusinessException {
        if (position == null) {
            throw new BusinessException("Le poste ne peut pas être nul.");
        }
        try {
            employeeDataAccess.assignPosition(employeeID, position);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'attribution du poste.", e);
        }
    }

    public void addPosition(Position position) throws BusinessException {
        if (position == null) {
            throw new BusinessException("Le poste ne peut pas être nul.");
        }
        try {
            employeeDataAccess.addPosition(position);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'ajout du poste.", e);
        }
    }

    public void revokePosition(int employeeID, Position position) throws BusinessException {
        if (position == null) {
            throw new BusinessException("Le poste ne peut pas être nul.");
        }
        try {
            employeeDataAccess.revokePosition(employeeID, position);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du retrait du poste.", e);
        }
    }

    public void checkIn(int employeeID) throws BusinessException {
        if (employeeID <= 0) {
            throw new BusinessException("L'ID de l'employé doit être un nombre positif.");
        }
        try {
            if (employeeDataAccess.isCheckedIn(employeeID)) {
                throw new BusinessException("L'employé est déjà enregistré comme présent.");
            }
            employeeDataAccess.checkIn(employeeID);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la validation de la présence.", e);
        }
    }

    public void checkOut(int employeeID) throws BusinessException {
        if (employeeID <= 0) {
            throw new BusinessException("L'ID de l'employé doit être un nombre positif.");
        }
        if (!employeeDataAccess.isCheckedIn(employeeID)) {
            throw new BusinessException("L'employé n'est pas enregistré comme présent.");
        }
        try {
            employeeDataAccess.checkOut(employeeID);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la validation du départ.", e);
        }
    }

    private void validateNonAttendanceBase(int employeeID, NonAttendanceType type, String description) throws BusinessException {
        if (employeeID <= 0) {
            throw new BusinessException("L'ID de l'employé doit être un nombre positif.");
        }
        if (type == null) {
            throw new BusinessException("Le type d'absence ne peut pas être nul.");
        }
        if (description == null || description.isBlank()) {
            throw new BusinessException("La description ne peut pas être nulle ou vide.");
        }
    }


    public void addNonAttendance(int employeeID, NonAttendanceType type, String description) throws BusinessException {
        validateNonAttendanceBase(employeeID, type, description);
        try {
            employeeDataAccess.addNonAttendance(employeeID, type, description);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'ajout de l'absence.", e);
        }
    }

    public void addNonAttendance(int employeeID, NonAttendanceType type, String description, LocalDate startDate) throws BusinessException {
        validateNonAttendanceBase(employeeID, type, description);
        if (startDate == null) {
            throw new BusinessException("La date de début ne peut pas être nulle.");
        }
        try {
            employeeDataAccess.addNonAttendance(employeeID, type, description, startDate);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'ajout de l'absence.", e);
        }
    }

    public void addNonAttendance(int employeeID, NonAttendanceType type, String description, LocalDate startDate, LocalDate endDate) throws BusinessException {
        validateNonAttendanceBase(employeeID, type, description);
        if (startDate == null) {
            throw new BusinessException("La date de début ne peut pas être nulle.");
        }
        if (endDate == null) {
            throw new BusinessException("La date de fin ne peut pas être nulle.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("La date de début doit être antérieure à la date de fin.");
        }
        try {
            employeeDataAccess.addNonAttendance(employeeID, type, description, startDate, endDate);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'ajout de l'absence.", e);
        }
    }

    public void changeNonAttendance(int employeeID, LocalDate startDate, LocalDate endDate) throws BusinessException {
        if (employeeID <= 0) {
            throw new BusinessException("L'ID de l'employé doit être un nombre positif.");
        }
        if (startDate == null) {
            throw new BusinessException("La date de début de l'absence ne peut pas être nulle.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("La date de début doit être antérieure à la date de fin.");
        }
        if (endDate == null) {
            throw new BusinessException("La date de fin de l'absence ne peut pas être nulle.");
        }
        if (endDate.isBefore(startDate)) {
            throw new BusinessException("La date de fin doit être postérieure à la date de début.");
        }
        try {
            employeeDataAccess.changeNonAttendance(employeeID, startDate, endDate);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement de l'absence.", e);
        }
    }
}
