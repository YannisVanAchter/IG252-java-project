package main.java.be.henallux.project.data;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import main.java.be.henallux.project.data.exception.DataBaseException;

import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.exception.DataValidationException;

public class ClientSupplierSearchDA {
    private final CRUD<ClientSupplier> clientSupplierDA;

    public ClientSupplierSearchDA() {
        clientSupplierDA = ClientSupplierDA.getInstance();
    }

    /**
     *
     */
    public List<ClientSupplier> search(String nom, String email, boolean isFidelityCardValid ) throws DataBaseException, DataValidationException
    {
        List<ClientSupplier> clientSupplier = new ArrayList<>();
        StringBuilder SQLInstruction = new StringBuilder("""
                    SELECT cs.id_ as ID
                    FROM Client_Supplier AS cs
                    WHERE 1=1
                    """); // ? Add '1=1', if all parameters are null, the where clause would create problems
        boolean addedWhereClause = false;
        if (nom != null && !nom.isEmpty()) {
            SQLInstruction.append(" AND cs.name_=?");
            addedWhereClause = true;
        }
        if (email != null && !email.isEmpty()) {
            SQLInstruction.append(" AND email=?");
            addedWhereClause = true;
        }
        if (isFidelityCardValid){
            SQLInstruction.append("""
                     AND cs.id_ in (
                        SELECT fd.clientID FROM FidelityCard AS fd
                        WHERE isValid=?
                    )
                    """);
            addedWhereClause = true;
        }
        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction.toString() + ";");
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
                statement.setBoolean(currentIndex, isFidelityCardValid);
                currentIndex++;
            }

            ResultSet result = statement.executeQuery();
            while  (result.next()) {
                clientSupplier.add(clientSupplierDA.getById(result.getInt("ID")));
            }
            return clientSupplier;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }
}