package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Fine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FineDAO extends DAO {

    public List<Fine> findAll() {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT ID, name, fineRate, description FROM tblFine";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Fine fine = new Fine();
                fine.setId(resultSet.getInt("ID"));
                fine.setName(resultSet.getString("name"));
                fine.setFineRate(resultSet.getDouble("fineRate"));
                fine.setDescription(resultSet.getString("description"));

                fines.add(fine);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return fines;
    }
}
