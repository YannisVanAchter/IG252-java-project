package main.java.be.henallux.project.data;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.java.be.henallux.project.data.MySQLConnector;
import main.java.be.henallux.project.data.exception.DataBaseException;

import main.java.be.henallux.project.model.ClientSupplier;

public class ClientSupplierSearchDA {
    private CRUD<ClientSupplier> clientSupplierDA;

    ClientSupplierSearchDA() {
        clientSupplierDA = ClientSupplierDA.getInstance();
    }

    /**
     *
     */
    public List<ClientSupplier> search(String nom, String email, boolean isFidelityCardValid ) throws DataBaseException, DataValidationException {
        List<ClientSupplier> clientSupplier = new ArrayList();
        StringBuilder SQLInstruction = new StringBuilder("""
                    SELECT cs.id_ as ID
                    FROM Client_Supplier AS cs
                    WHERE 
                    """);
        boolean addedWhereClause = false;
        if (nom != null && !nom.isEmpty()) {
            SQLInstruction.add(" cs.name_=?");
            addedWhereClause = true;
        }
        if (email != null && !email.isEmpty()) {
            if (addedWhereClause) {
                SQLInstruction.add(" AND ");
            }
            SQLInstruction.add(" email=?");
            addedWhereClause = true;
        }
        if (isFidelityCardValid){
            if (addedWhereClause) {
                SQLInstruction.add(" AND ");
            }
            SQLInstruction.add("""
                    cs.id_ in (
                        SELECT fd.clientID FROM FidelityCard AS fd
                        WHERE isValid=?
                    )
                    """);
            addedWhereClause = true;
        }
        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
            Statement statement = connection.prepareStatement(SQLInstruction.toString() + ";");
            int currentIndex = 1;
            if (nom != null && !nom.isEmpty()) {
                statement.setString(currentIndex, nom);
                currentIndex++;
            }
            if (email != null && !email.isEmpty()) {
                statement.setString(currentIndex, email);
                currentIndex++;
            }
            if (isFidelityCardValid) {
                statement.setString(currentIndex, isFidelityCardValid);
                currentIndex++;
            }

            ResultSet result = statement.executeQuerry();
            while  (result.next()) {
                clientSupplier.add(clientSupplierDA.getById(result.getInt("ID")));
            }
            return clientSupplier;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }
}